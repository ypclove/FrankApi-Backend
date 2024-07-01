package com.frank.apibackstage.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductOrderMapper;
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
import com.frank.apicommon.config.EmailConfig;
import com.frank.apicommon.exception.BusinessException;
import com.frank.apicommon.utils.EmailUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.frank.apicommon.constant.PayConstant.*;
import static com.frank.apicommon.enums.PayTypeEnum.WX;
import static com.frank.apicommon.enums.PaymentStatusEnum.*;
import static com.frank.apicommon.utils.WxPayUtil.getRequestHeader;

/**
 * @author Frank
 * @date 2024/6/28
 */
@Slf4j
@Primary
@Service
@Qualifier("1")
public class WxOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {

    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    @Resource
    private ProductInfoServiceImpl productInfoService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private WxPayService wxPayService;

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private UserService userService;

    @Resource
    private PaymentInfoService paymentInfoService;

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
        lambdaQueryWrapper.gt(ProductOrder::getExpirationTime, DateUtil.date(System.currentTimeMillis()));

        ProductOrder productOrder = this.getOne(lambdaQueryWrapper);
        if (Objects.isNull(productOrder)) {
            return null;
        }
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        productOrderVo.setTotal(productOrder.getTotal().toString());
        productOrderVo.setProductInfo(JSONUtil.toBean(productOrder.getProductInfo(), ProductInfo.class));
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
        String orderNo = ORDER_PREFIX + RandomUtil.randomNumbers(ORDER_ID_LENGTH);
        ProductOrder productOrder = new ProductOrder();
        productOrder.setUserId(loginUser.getId());
        productOrder.setOrderNo(orderNo);
        productOrder.setProductId(productInfo.getId());
        productOrder.setOrderName(productInfo.getName());
        productOrder.setTotal(productInfo.getTotal());
        productOrder.setStatus(NOTPAY.getCode());
        productOrder.setPayType(WX.getCode());
        productOrder.setExpirationTime(expirationTime);
        productOrder.setProductInfo(JSONUtil.toJsonPrettyStr(productInfo));
        productOrder.setAddPoints(productInfo.getAddPoints());
        boolean saveResult = this.save(productOrder);
        // 构建支付请求
        WxPayUnifiedOrderV3Request wxPayRequest = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(productOrder.getTotal());
        wxPayRequest.setAmount(amount);
        wxPayRequest.setDescription(productOrder.getOrderName());
        // 设置订单的过期时间为 5 分钟
        String format = DateUtil.format(expirationTime, TIME_FORMAT);
        wxPayRequest.setTimeExpire(format);
        wxPayRequest.setOutTradeNo(productOrder.getOrderNo());
        try {
            String codeUrl = wxPayService.createOrderV3(TradeTypeEnum.NATIVE, wxPayRequest);
            if (StringUtils.isBlank(codeUrl)) {
                throw new BusinessException(StatusCode.OPERATION_ERROR);
            }
            productOrder.setCodeUrl(codeUrl);
            // 更新微信订单的二维码，不用重复创建
            boolean updateResult = this.updateProductOrder(productOrder);
            if (!updateResult & !saveResult) {
                throw new BusinessException(StatusCode.OPERATION_ERROR);
            }
        } catch (WxPayException e) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
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
        String codeUrl = productOrder.getCodeUrl();
        Long id = productOrder.getId();
        ProductOrder updateCodeUrl = new ProductOrder();
        updateCodeUrl.setCodeUrl(codeUrl);
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
        ProductOrder productOrder = this.getOne(lambdaQueryWrapper);
        if (Objects.isNull(productOrder)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        return productOrder;
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
        RLock rLock = redissonClient.getLock(NOTIFY_WX_ORDER);
        try {
            log.info("微信支付回调通知处理：{}", notifyData);
            WxPayOrderNotifyV3Result result = wxPayService.parseOrderNotifyV3Result(notifyData, getRequestHeader(request));
            // 解密后的数据
            WxPayOrderNotifyV3Result.DecryptNotifyResult notifyResult = result.getResult();
            if (WxPayConstants.WxpayTradeStatus.SUCCESS.equals(notifyResult.getTradeState())) {
                String outTradeNo = notifyResult.getOutTradeNo();
                if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    ProductOrder productOrder = this.getProductOrderByOrderId(outTradeNo);
                    // 处理重复通知
                    if (productOrder.getStatus().equals(SUCCESS.getCode())) {
                        redisTemplate.delete(QUERY_ORDER_STATUS + outTradeNo);
                        return WxPayNotifyResponse.success("支付成功");
                    }
                    // 更新订单状态
                    boolean updateOrderStatus = this.updateOrderStatusByOrderId(outTradeNo, SUCCESS.getCode());
                    // 更新用户积分
                    boolean addWalletBalance = userService.addBalance(productOrder.getUserId(), productOrder.getAddPoints());
                    // 保存支付记录
                    PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
                    BeanUtils.copyProperties(notifyResult, paymentInfoVo);
                    WxPayOrderQueryV3Result.Payer payer = new WxPayOrderQueryV3Result.Payer();
                    payer.setOpenid(notifyResult.getPayer().getOpenid());
                    WxPayOrderQueryV3Result.Amount amount = new WxPayOrderQueryV3Result.Amount();
                    amount.setTotal(notifyResult.getAmount().getTotal());
                    amount.setPayerTotal(notifyResult.getAmount().getPayerTotal());
                    amount.setCurrency(notifyResult.getAmount().getCurrency());
                    amount.setPayerCurrency(notifyResult.getAmount().getPayerCurrency());
                    paymentInfoVo.setPayer(payer);
                    paymentInfoVo.setAmount(amount);
                    boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
                    // 更新活动表
                    boolean rechargeActivity = saveRechargeActivity(productOrder);
                    if (paymentResult && updateOrderStatus && addWalletBalance && rechargeActivity) {
                        log.info("支付回调通知处理成功");
                        // 发送邮件
                        redisTemplate.delete(QUERY_ORDER_STATUS + outTradeNo);
                        sendPaySuccessEmail(productOrder);
                        return WxPayNotifyResponse.success("支付成功");
                    }
                }
            }
            if (WxPayConstants.WxpayTradeStatus.PAY_ERROR.equals(notifyResult.getTradeState())) {
                log.error("微信支付失败：{}", result);
                throw new WxPayException("支付失败");
            }
            if (WxPayConstants.WxpayTradeStatus.USER_PAYING.equals(notifyResult.getTradeState())) {
                throw new WxPayException("支付中");
            }
            throw new BusinessException(StatusCode.OPERATION_ERROR, "支付失败");
        } catch (Exception e) {
            log.error("支付失败：{}", e.getMessage());
            try {
                throw new WxPayException("支付失败");
            } catch (WxPayException ex) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "支付失败");
            }
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
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
        WxPayOrderQueryV3Request wxPayOrderQueryV3Request = new WxPayOrderQueryV3Request();
        wxPayOrderQueryV3Request.setOutTradeNo(orderNo);
        WxPayOrderQueryV3Result wxPayOrderQueryV3Result;
        try {
            wxPayOrderQueryV3Result = wxPayService.queryOrderV3(wxPayOrderQueryV3Request);
            String tradeState = wxPayOrderQueryV3Result.getTradeState();
            // 订单已支付
            if (tradeState.equals(SUCCESS.getText())) {
                this.updateOrderStatusByOrderId(orderNo, SUCCESS.getCode());
                // 用户余额补发
                userService.addBalance(productOrder.getUserId(), productOrder.getAddPoints());
                // 创建支付记录
                PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
                BeanUtils.copyProperties(wxPayOrderQueryV3Result, paymentInfoVo);
                paymentInfoService.createPaymentInfo(paymentInfoVo);
                // 更新活动表
                saveRechargeActivity(productOrder);
                sendPaySuccessEmail(productOrder);
                log.info("超时订单 {} 状态已更新", orderNo);
            }
            if (tradeState.equals(NOTPAY.getText()) || tradeState.equals(CLOSED.getText())) {
                closedOrderByOrderId(orderNo);
                this.updateOrderStatusByOrderId(orderNo, CLOSED.getCode());
                log.info("超时订单 {} 订单已关闭", orderNo);
            }
            redisTemplate.delete(QUERY_ORDER_STATUS + orderNo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 按订单 Id 关闭订单
     *
     * @param OrderId 订单 Id
     * @throws WxPayException WxPayException
     */
    @Override
    public void closedOrderByOrderId(String OrderId) throws WxPayException {
        wxPayService.closeOrderV3(OrderId);
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
        boolean save = rechargeActivityService.save(rechargeActivity);
        if (!save) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "保存失败");
        }
        return true;
    }

    /**
     * 发送支付成功电子邮件
     *
     * @param productOrder 产品订单
     */
    private void sendPaySuccessEmail(ProductOrder productOrder) {
        // 发送邮件
        User user = userService.getById(productOrder.getUserId());
        if (StringUtils.isNotBlank(user.getEmail())) {
            try {
                new EmailUtil().sendPaySuccessEmail(
                        user.getEmail(), mailSender, emailConfig, productOrder.getOrderName(),
                        String.valueOf(new BigDecimal(productOrder.getTotal()).divide(
                                new BigDecimal(AMOUNT_TRANSFER), DECIMAL_POINT, RoundingMode.HALF_UP)));
                log.info("发送邮件：{}，成功", user.getEmail());
            } catch (Exception e) {
                log.error("发送邮件：{}，失败：{}", user.getEmail(), e.getMessage());
            }
        }
    }
}
