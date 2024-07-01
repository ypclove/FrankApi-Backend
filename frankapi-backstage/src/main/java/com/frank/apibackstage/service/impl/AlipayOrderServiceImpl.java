package com.frank.apibackstage.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductOrderMapper;
import com.frank.apibackstage.model.alipay.AliPayAsyncResponse;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.model.entity.RechargeActivity;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.vo.PaymentInfoVo;
import com.frank.apibackstage.model.vo.ProductOrderVo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.PaymentInfoService;
import com.frank.apibackstage.service.ProductOrderService;
import com.frank.apibackstage.service.RechargeActivityService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.config.AliPayAccountConfig;
import com.frank.apicommon.config.EmailConfig;
import com.frank.apicommon.enums.AlipayTradeStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import com.frank.apicommon.utils.EmailUtil;
import com.frank.apicommon.utils.RedissonLockUtil;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.frank.apicommon.constant.PayConstant.*;
import static com.frank.apicommon.enums.PayTypeEnum.ALIPAY;
import static com.frank.apicommon.enums.PaymentStatusEnum.*;

/**
 * @author Frank
 * @date 2024/6/28
 */
@Slf4j
@Service
@Qualifier("2")
public class AlipayOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private AliPayAccountConfig aliPayAccountConfig;

    @Resource
    private UserService userService;

    @Resource
    private ProductInfoServiceImpl productInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @Resource
    private RechargeActivityService rechargeActivityService;

    /**
     * 获取产品订单
     *
     * @param productId 产品 Id
     * @param loginUser 登录用户
     * @param payType   支付方式
     * @return {@link ProductOrderVo}
     */
    @Override
    public ProductOrderVo getProductOrder(Long productId, UserVO loginUser, Integer payType) {
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getProductId, productId);
        lambdaQueryWrapper.eq(ProductOrder::getStatus, NOTPAY.getCode());
        lambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        lambdaQueryWrapper.eq(ProductOrder::getUserId, loginUser.getId());
        ProductOrder productOrder = this.getOne(lambdaQueryWrapper);
        if (Objects.isNull(productOrder)) {
            return null;
        }
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        productOrderVo.setProductInfo(JSONUtil.toBean(productOrder.getProductInfo(), ProductInfo.class));
        productOrderVo.setTotal(productOrder.getTotal().toString());
        return productOrderVo;
    }

    /**
     * 保存产品订单
     *
     * @param productId 产品 Id
     * @param loginUser 登录用户
     * @return {@link ProductOrderVo}
     */
    @Override
    public ProductOrderVo saveProductOrder(Long productId, UserVO loginUser) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (Objects.isNull(productInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "商品不存在");
        }
        // 5 分钟有效期
        Date date = DateUtil.date(System.currentTimeMillis());
        Date expirationTime = DateUtil.offset(date, DateField.MINUTE, ORDER_TTL);

        String orderId = ORDER_PREFIX + RandomUtil.randomNumbers(ORDER_ID_LENGTH);
        ProductOrder productOrder = new ProductOrder();
        productOrder.setUserId(loginUser.getId());
        productOrder.setOrderNo(orderId);
        productOrder.setProductId(productInfo.getId());
        productOrder.setOrderName(productInfo.getName());
        productOrder.setTotal(productInfo.getTotal());
        productOrder.setStatus(NOTPAY.getCode());
        productOrder.setPayType(ALIPAY.getCode());
        productOrder.setExpirationTime(expirationTime);
        productOrder.setProductInfo(JSONUtil.toJsonPrettyStr(productInfo));
        productOrder.setAddPoints(productInfo.getAddPoints());
        boolean saveResult = this.save(productOrder);

        AlipayTradePagePayRequest request = getAlipayTradePagePayRequest(orderId, productInfo);

        try {
            AlipayTradePagePayResponse alipayTradePagePayResponse = AliPayApi.pageExecute(request);
            String payUrl = alipayTradePagePayResponse.getBody();
            productOrder.setFormData(payUrl);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        boolean updateResult = this.updateProductOrder(productOrder);
        if (!updateResult & !saveResult) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        // 构建 vo
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        productOrderVo.setProductInfo(productInfo);
        productOrderVo.setTotal(productInfo.getTotal().toString());
        return productOrderVo;
    }

    /**
     * 更新产品订单
     *
     * @param productOrder 产品订单
     * @return 更新产品订单是否成功
     */
    @Override
    public boolean updateProductOrder(ProductOrder productOrder) {
        String formData = productOrder.getFormData();
        Long id = productOrder.getId();
        ProductOrder updateCodeUrl = new ProductOrder();
        updateCodeUrl.setFormData(formData);
        updateCodeUrl.setId(id);
        return this.updateById(updateCodeUrl);
    }

    /**
     * 通过订单 Id 获取订单
     *
     * @param orderId 订单 Id
     * @return 订单
     */
    @Override
    public ProductOrder getProductOrderByOrderId(String orderId) {
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getOrderNo, orderId);
        return this.getOne(lambdaQueryWrapper);
    }

    /**
     * 根据订单 Id 更新订单状态
     *
     * @param orderId     订单 Id
     * @param orderStatus 订单状态
     * @return 更新订单状态是否成功
     */
    @Override
    public boolean updateOrderStatusByOrderId(String orderId, Integer orderStatus) {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setStatus(orderStatus);
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getOrderNo, orderId);
        return this.update(productOrder, lambdaQueryWrapper);
    }

    /**
     * 处理付款通知
     *
     * @param notifyData 通知数据
     * @param request    HttpServletRequest
     * @return 通知结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doPaymentNotify(String notifyData, HttpServletRequest request) {
        Map<String, String> params = AliPayApi.toMap(request);
        AliPayAsyncResponse aliPayAsyncResponse = JSONUtil.toBean(JSONUtil.toJsonStr(params), AliPayAsyncResponse.class);
        String lockName = NOTIFY_ALIPAY_ORDER + aliPayAsyncResponse.getOutTradeNo();
        return redissonLockUtil.redissonDistributedLocks(lockName, "支付宝异步回调异常：", () -> {
            String result;
            try {
                result = checkAlipayOrder(aliPayAsyncResponse, params);
            } catch (AlipayApiException e) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
            }
            if (!"success".equals(result)) {
                return result;
            }
            String doAliPayOrderBusinessResult = this.doAliPayOrderBusiness(aliPayAsyncResponse);
            if (StringUtils.isBlank(doAliPayOrderBusinessResult)) {
                throw new BusinessException(StatusCode.OPERATION_ERROR);
            }
            return doAliPayOrderBusinessResult;
        });
    }

    /**
     * 处理超时订单
     * 检查订单状态
     *
     * @param productOrder 产品订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processingTimedOutOrders(ProductOrder productOrder) {
        String orderNo = productOrder.getOrderNo();
        try {
            // 查询订单
            AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
            alipayTradeQueryModel.setOutTradeNo(orderNo);
            AlipayTradeQueryResponse alipayTradeQueryResponse = AliPayApi.tradeQueryToResponse(alipayTradeQueryModel);

            // 本地创建了订单，但是用户没有扫码，支付宝端没有订单
            if (!alipayTradeQueryResponse.getCode().equals(RESPONSE_CODE_SUCCESS)) {
                // 更新本地订单状态
                this.updateOrderStatusByOrderId(orderNo, CLOSED.getCode());
                log.info("超时订单 {} 更新成功", orderNo);
                return;
            }
            String tradeStatus = AlipayTradeStatusEnum
                    .findByName(alipayTradeQueryResponse.getTradeStatus())
                    .getPaymentStatusEnum()
                    .getText();
            // 订单没有支付就关闭订单,更新本地订单状态
            if (tradeStatus.equals(NOTPAY.getText()) || tradeStatus.equals(CLOSED.getText())) {
                closedOrderByOrderId(orderNo);
                this.updateOrderStatusByOrderId(orderNo, CLOSED.getCode());
                log.info("超时订单 {} 关闭成功", orderNo);
                return;
            }
            if (tradeStatus.equals(SUCCESS.getText())) {
                // 订单已支付更新商户端的订单状态
                boolean updateOrderStatus = this.updateOrderStatusByOrderId(orderNo, SUCCESS.getCode());
                // 补发积分到用户钱包
                boolean addWalletBalance = userService.addBalance(productOrder.getUserId(), productOrder.getAddPoints());
                // 保存支付记录
                PaymentInfoVo paymentInfoVo = getPaymentInfoVo(alipayTradeQueryResponse);
                boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
                if (!updateOrderStatus & !addWalletBalance & !paymentResult) {
                    throw new BusinessException(StatusCode.OPERATION_ERROR);
                }
                // 更新活动表
                saveRechargeActivity(productOrder);
                sendSuccessEmail(productOrder, alipayTradeQueryResponse.getTotalAmount());
                log.info("超时订单 {} 更新成功", orderNo);
            }
        } catch (AlipayApiException e) {
            log.error("订单 {} 处理失败", orderNo);
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 按订单 Id 关闭订单
     *
     * @param OrderId 订单 Id
     * @throws AlipayApiException AlipayApiException
     */
    @Override
    public void closedOrderByOrderId(String OrderId) throws AlipayApiException {
        AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
        alipayTradeCloseModel.setOutTradeNo(OrderId);
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizModel(alipayTradeCloseModel);
        AliPayApi.doExecute(request);
    }

    /**
     * 获取支付信息
     *
     * @param alipayTradeQueryResponse Alipay 交易响应
     * @return PaymentInfoVo
     */
    private @NotNull PaymentInfoVo getPaymentInfoVo(AlipayTradeQueryResponse alipayTradeQueryResponse) {
        PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
        paymentInfoVo.setAppid(aliPayAccountConfig.getAppId());
        paymentInfoVo.setOutTradeNo(alipayTradeQueryResponse.getOutTradeNo());
        paymentInfoVo.setTransactionId(alipayTradeQueryResponse.getTradeNo());
        paymentInfoVo.setTradeType(ORDER_TYPE);
        paymentInfoVo.setTradeState(alipayTradeQueryResponse.getTradeStatus());
        paymentInfoVo.setTradeStateDesc(ORDER_SUCCESS_DESC);
        paymentInfoVo.setSuccessTime(String.valueOf(alipayTradeQueryResponse.getSendPayDate()));
        WxPayOrderQueryV3Result.Payer payer = new WxPayOrderQueryV3Result.Payer();
        payer.setOpenid(alipayTradeQueryResponse.getBuyerOpenId());
        paymentInfoVo.setPayer(payer);
        WxPayOrderQueryV3Result.Amount amount = new WxPayOrderQueryV3Result.Amount();
        amount.setTotal(new BigDecimal(alipayTradeQueryResponse.getTotalAmount()).multiply(new BigDecimal(AMOUNT_TRANSFER)).intValue());
        amount.setPayerTotal(new BigDecimal(alipayTradeQueryResponse.getReceiptAmount()).multiply(new BigDecimal(AMOUNT_TRANSFER)).intValue());
        amount.setCurrency(alipayTradeQueryResponse.getPayCurrency());
        amount.setPayerCurrency(alipayTradeQueryResponse.getPayCurrency());
        paymentInfoVo.setAmount(amount);
        return paymentInfoVo;
    }

    /**
     * 检查支付宝订单
     *
     * @param response Alipay 异步响应
     * @param params   参数
     * @return 检查结果
     * @throws AlipayApiException AlipayApiException
     */
    private String checkAlipayOrder(AliPayAsyncResponse response, Map<String, String> params) throws AlipayApiException {
        String result = "failure";
        boolean verifyResult = AlipaySignature.rsaCheckV1(
                params,
                AliPayApiConfigKit.getAliPayApiConfig().getAliPayPublicKey(),
                AliPayApiConfigKit.getAliPayApiConfig().getCharset(),
                AliPayApiConfigKit.getAliPayApiConfig().getSignType());
        if (!verifyResult) {
            return result;
        }
        // 1. 验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号
        ProductOrder productOrder = this.getProductOrderByOrderId(response.getOutTradeNo());
        if (Objects.isNull(productOrder)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "订单不存在");
        }
        // 2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）
        int totalAmount = new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal(AMOUNT_TRANSFER)).intValue();
        if (totalAmount != productOrder.getTotal()) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "订单金额不一致");
        }
        // 3. 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）
        String sellerId = aliPayAccountConfig.getSellerId();
        if (!response.getSellerId().equals(sellerId)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "卖家账号校验失败");
        }
        // 4. 验证 app_id 是否为该商家本身
        String appId = aliPayAccountConfig.getAppId();
        if (!response.getAppId().equals(appId)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "校验失败");
        }
        // 5. 状态 TRADE_SUCCESS 的通知触发条件是商家开通的产品支持退款功能的前提下，买家付款成功
        String tradeStatus = response.getTradeStatus();
        if (!TRADE_SUCCESS.equals(String.valueOf(tradeStatus))) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "交易失败");
        }
        return "success";
    }

    /**
     * Alipay 支付交易
     *
     * @param response Alipay 支付异步响应
     * @return 交易结果
     */
    @SneakyThrows
    protected String doAliPayOrderBusiness(AliPayAsyncResponse response) {
        String outTradeNo = response.getOutTradeNo();
        ProductOrder productOrder = this.getProductOrderByOrderId(outTradeNo);
        // 处理重复通知
        if (productOrder.getStatus().equals(SUCCESS.getCode())) {
            return "success";
        }
        // 业务代码
        // 更新订单状态
        boolean updateOrderStatus = this.updateOrderStatusByOrderId(outTradeNo, SUCCESS.getCode());
        // 更新用户积分
        boolean addWalletBalance = userService.addBalance(productOrder.getUserId(), productOrder.getAddPoints());
        // 保存支付记录
        PaymentInfoVo paymentInfoVo = getPaymentInfoVo(response);
        boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
        // 更新活动表
        boolean rechargeActivity = saveRechargeActivity(productOrder);
        if (paymentResult && updateOrderStatus && addWalletBalance && rechargeActivity) {
            log.info("支付回调通知处理成功");
            // 发送邮件
            sendSuccessEmail(productOrder, response.getTotalAmount());
            return "success";
        }
        throw new BusinessException(StatusCode.OPERATION_ERROR);
    }

    /**
     * 获取支付信息
     *
     * @param response Alipay 支付异步响应
     * @return {@link PaymentInfoVo}
     */
    private static @NotNull PaymentInfoVo getPaymentInfoVo(AliPayAsyncResponse response) {
        PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
        paymentInfoVo.setAppid(response.getAppId());
        paymentInfoVo.setOutTradeNo(response.getOutTradeNo());
        paymentInfoVo.setTransactionId(response.getTradeNo());
        paymentInfoVo.setTradeType(ORDER_TYPE);
        paymentInfoVo.setTradeState(response.getTradeStatus());
        paymentInfoVo.setTradeStateDesc(ORDER_SUCCESS_DESC);
        paymentInfoVo.setSuccessTime(response.getNotifyTime());
        WxPayOrderQueryV3Result.Payer payer = new WxPayOrderQueryV3Result.Payer();
        payer.setOpenid(response.getBuyerId());
        paymentInfoVo.setPayer(payer);
        WxPayOrderQueryV3Result.Amount amount = new WxPayOrderQueryV3Result.Amount();
        amount.setTotal(new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal(AMOUNT_TRANSFER)).intValue());
        amount.setPayerTotal(new BigDecimal(response.getReceiptAmount()).multiply(new BigDecimal(AMOUNT_TRANSFER)).intValue());
        amount.setCurrency(ORDER_CURRENCY);
        amount.setPayerCurrency(ORDER_CURRENCY);
        paymentInfoVo.setAmount(amount);
        return paymentInfoVo;
    }

    /**
     * 支付成功发送邮件
     *
     * @param productOrder 产品订单
     * @param orderTotal   金额
     */
    private void sendSuccessEmail(ProductOrder productOrder, String orderTotal) {
        // 发送邮件
        User user = userService.getById(productOrder.getUserId());
        if (StringUtils.isNotBlank(user.getEmail())) {
            try {
                ProductOrder productOrderByOutTradeNo = this.getProductOrderByOrderId(productOrder.getOrderNo());
                new EmailUtil().sendPaySuccessEmail(user.getEmail(), mailSender, emailConfig, productOrderByOutTradeNo.getOrderName(),
                        String.valueOf(orderTotal));
                log.info("发送邮件：{}，成功", user.getEmail());
            } catch (Exception e) {
                log.error("发送邮件：{}，失败：{}", user.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * 保存充值活动
     *
     * @param productOrder 产品订单
     * @return boolean
     */
    private boolean saveRechargeActivity(ProductOrder productOrder) {
        RechargeActivity rechargeActivity = new RechargeActivity();
        rechargeActivity.setUserId(productOrder.getUserId());
        rechargeActivity.setProductId(productOrder.getProductId());
        rechargeActivity.setOrderNo(productOrder.getOrderNo());
        boolean save = rechargeActivityService.save(rechargeActivity);
        if (!save) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "保存失败");
        }
        return true;
    }

    /**
     * 构建 Alipay 交易请求
     *
     * @param orderId     订单 Id
     * @param productInfo 产品信息
     * @return Alipay 交易请求
     */
    private @NotNull AlipayTradePagePayRequest getAlipayTradePagePayRequest(String orderId, ProductInfo productInfo) {
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(orderId);
        model.setSubject(productInfo.getName());
        model.setProductCode(ALIPAY_PRODUCT_CODE);
        // 除以 100 得到以 "￥" 为单位的金额，并且四舍五入，保留 2 位小数
        BigDecimal scaledAmount = new BigDecimal(productInfo.getTotal()).divide(new BigDecimal(AMOUNT_TRANSFER), DECIMAL_POINT, RoundingMode.HALF_UP);
        model.setTotalAmount(String.valueOf(scaledAmount));
        model.setBody(productInfo.getDescription());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizModel(model);
        request.setNotifyUrl(aliPayAccountConfig.getNotifyUrl());
        request.setReturnUrl(aliPayAccountConfig.getReturnUrl());
        return request;
    }
}
