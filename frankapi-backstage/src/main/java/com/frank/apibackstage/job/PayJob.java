package com.frank.apibackstage.job;

import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.service.OrderService;
import com.frank.apibackstage.service.ProductOrderService;
import com.frank.apicommon.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.frank.apicommon.enums.PayTypeEnum.ALIPAY;
import static com.frank.apicommon.enums.PayTypeEnum.WX;


/**
 * @author Frank
 * @date 2024/6/28
 */
@Slf4j
@Component
public class PayJob {

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    /**
     * 微信订单确认
     * 每 25s 查询一次超过 5 分钟过期且未支付的订单
     */
    @Scheduled(cron = "0/25 * * * * ?")
    public void wxOrderConfirm() {
        redissonLockUtil.redissonDistributedLocks("wxOrderConfirm", () -> {
            List<ProductOrder> orderList = orderService.getNoPayOrderByDuration(5, false, WX.getText());
            ProductOrderService productOrderService = orderService.getProductOrderServiceByPayType(WX.getCode());
            for (ProductOrder productOrder : orderList) {
                String orderNo = productOrder.getOrderNo();
                try {
                    productOrderService.processingTimedOutOrders(productOrder);
                } catch (Exception e) {
                    log.error("微信超时订单：{}，确认异常：{}", orderNo, e.getMessage());
                    break;
                }
            }
        });
    }

    /**
     * 支付宝订单确认
     * 每 20s 查询一次超过 5 分钟过期且未支付的订单
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void aliPayOrderConfirm() {
        redissonLockUtil.redissonDistributedLocks("aliPayOrderConfirm", () -> {
            List<ProductOrder> orderList = orderService.getNoPayOrderByDuration(5, false, ALIPAY.getText());
            ProductOrderService productOrderService = orderService.getProductOrderServiceByPayType(ALIPAY.getCode());
            for (ProductOrder productOrder : orderList) {
                String orderNo = productOrder.getOrderNo();
                try {
                    productOrderService.processingTimedOutOrders(productOrder);
                } catch (Exception e) {
                    log.error("支付宝超时订单：{}，确认异常：{}", orderNo, e.getMessage());
                    break;
                }
            }
        });
    }

    /**
     * 订单确认
     * 每天 2 点删除一次 15 天前未支付且已关闭的订单
     */
    @Scheduled(cron = "* * 2 * * ?")
    public void clearOverdueOrders() {
        redissonLockUtil.redissonDistributedLocks("clearOverdueOrders", () -> {
            List<ProductOrder> orderList = orderService.getNoPayOrderByDuration(15 * 24 * 60, true, "");
            boolean removeResult = productOrderService.removeBatchByIds(orderList);
            if (removeResult) {
                log.info("清除成功");
            }
        });
    }
}

