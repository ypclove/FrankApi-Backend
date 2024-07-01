package com.frank.apibackstage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductOrderMapper;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.model.entity.RechargeActivity;
import com.frank.apibackstage.model.vo.ProductOrderVo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.OrderService;
import com.frank.apibackstage.service.ProductOrderService;
import com.frank.apibackstage.service.RechargeActivityService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.PaymentStatusEnum;
import com.frank.apicommon.enums.ProductTypeEnum;
import com.frank.apicommon.exception.BusinessException;
import com.frank.apicommon.utils.RedissonLockUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.frank.apicommon.constant.PayConstant.CREATE_ORDER;
import static com.frank.apicommon.constant.PayConstant.GET_ORDER;
import static com.frank.apicommon.enums.PayTypeEnum.ALIPAY;
import static com.frank.apicommon.enums.PayTypeEnum.WX;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements OrderService {

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private List<ProductOrderService> productOrderServices;

    @Resource
    private RechargeActivityService rechargeActivityService;

    @Resource
    private ProductInfoServiceImpl productInfoService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    /**
     * 按支付方式创建订单
     *
     * @param productId 产品 Id
     * @param payType   支付方式
     * @param loginUser 付款用户
     * @return {@link ProductOrderVo}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductOrderVo createOrderByPayType(Long productId, Integer payType, UserVO loginUser) {
        // 按支付方式获取产品订单服务 Bean
        ProductOrderService productOrderService = getProductOrderServiceByPayType(payType);
        // 防止同一个用户重复创建相同的订单
        String getOrderLock = (GET_ORDER + loginUser.getUserAccount()).intern();
        ProductOrderVo getProductOrderVo = redissonLockUtil.redissonDistributedLocks(getOrderLock, () -> {
            // 如果订单存在就直接返回，不用再创建新的订单
            return productOrderService.getProductOrder(productId, loginUser, payType);
        });
        if (Objects.nonNull(getProductOrderVo)) {
            return getProductOrderVo;
        }
        // 防止同一个用户同时发起多个创建订单的请求，从而导致并发问题
        String createOrderLock = (CREATE_ORDER + loginUser.getUserAccount()).intern();
        // 分布式锁工具
        return redissonLockUtil.redissonDistributedLocks(createOrderLock, () -> {
            // 检查是否购买充值活动
            checkBuyRechargeActivity(loginUser.getId(), productId);
            // 保存订单，返回 vo 信息
            return productOrderService.saveProductOrder(productId, loginUser);
        }, "创建订单失败");
    }

    /**
     * 按支付方式获取产品订单服务
     * 实现思路：
     * 1. 遍历 productOrderServices 流，对每个服务 productOrderService，获取其类上的 Qualifier 注解
     * 2. 检查注解是否存在，并且注解的值是否等于传入的 payType 转换成字符串后的值
     * 3. 如果找到符合条件的服务，则返回它
     * 4. 如果没有找到，则抛出 BusinessException 异常
     *
     * @param payType 支付方式
     * @return ProductOrderService
     */
    @Override
    public ProductOrderService getProductOrderServiceByPayType(Integer payType) {
        return productOrderServices.stream()
                .filter(s -> {
                    Qualifier qualifierAnnotation = s.getClass().getAnnotation(Qualifier.class);
                    return qualifierAnnotation != null && qualifierAnnotation.value().equals(String.valueOf(payType));
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException(StatusCode.PARAMS_ERROR, "暂无该支付方式"));
    }

    /**
     * 处理订单通知
     *
     * @param notifyData 通知数据
     * @param request    HttpServletRequest
     * @return 通知结果
     */
    @Override
    public String doOrderNotify(String notifyData, HttpServletRequest request) {
        int payType;
        if (notifyData.startsWith("gmt_create=")
                && notifyData.contains("gmt_create")
                && notifyData.contains("sign_type")
                && notifyData.contains("notify_type")) {
            payType = ALIPAY.getCode();
        } else {
            payType = WX.getCode();
        }
        return this.getProductOrderServiceByPayType(payType).doPaymentNotify(notifyData, request);
    }

    /**
     * 按时间获得未支付订单
     * 查找超过 minutes 分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @param remove  是否是删除
     * @param payType 支付方式
     * @return 未支付订单集合
     */
    @Override
    public List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType) {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getCode());
        if (StringUtils.isNotBlank(payType)) {
            productOrderLambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        }
        // 删除
        if (remove) {
            productOrderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.CLOSED.getCode());
        }
        productOrderLambdaQueryWrapper.and(p -> p.le(ProductOrder::getCreateTime, instant));
        return productOrderService.list(productOrderLambdaQueryWrapper);
    }

    /**
     * 检查购买充值活动
     *
     * @param userId    用户 Id
     * @param productId 产品订单 Id
     */
    private void checkBuyRechargeActivity(Long userId, Long productId) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (productInfo.getProductType().equals(ProductTypeEnum.RECHARGEACTIVITY.getCode())) {
            LambdaQueryWrapper<ProductOrder> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderLambdaQueryWrapper.eq(ProductOrder::getUserId, userId);
            orderLambdaQueryWrapper.eq(ProductOrder::getProductId, productId);
            orderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getCode());
            orderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.SUCCESS.getCode());

            long orderCount = productOrderService.count(orderLambdaQueryWrapper);
            if (orderCount > 0) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "该商品只能购买一次，请查看是否已经创建了该订单，或者挑选其他商品吧！");
            }
            LambdaQueryWrapper<RechargeActivity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            activityLambdaQueryWrapper.eq(RechargeActivity::getUserId, userId);
            activityLambdaQueryWrapper.eq(RechargeActivity::getProductId, productId);
            long count = rechargeActivityService.count(activityLambdaQueryWrapper);
            if (count > 0) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "该商品只能购买一次，请查看是否已经创建了该订单，或者挑选其他商品吧！！");
            }
        }
    }
}
