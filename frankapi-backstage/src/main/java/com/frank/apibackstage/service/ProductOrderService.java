package com.frank.apibackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.model.vo.ProductOrderVo;
import com.frank.apibackstage.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank
 * @date 2024/06/22
 */
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 获取产品订单
     *
     * @param productId 产品 Id
     * @param loginUser 登录用户
     * @param payType   支付方式
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo getProductOrder(Long productId, UserVO loginUser, Integer payType);

    /**
     * 保存产品订单
     *
     * @param productId 产品 Id
     * @param loginUser 登录用户
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo saveProductOrder(Long productId, UserVO loginUser);

    /**
     * 更新产品订单
     *
     * @param productOrder 产品订单
     * @return 更新产品订单是否成功
     */
    boolean updateProductOrder(ProductOrder productOrder);

    /**
     * 根据订单 Id 获取订单
     *
     * @param orderId 订单 Id
     * @return 订单
     */
    ProductOrder getProductOrderByOrderId(String orderId);

    /**
     * 根据订单 Id 更新订单状态
     *
     * @param orderId     订单 Id
     * @param orderStatus 订单状态
     * @return 更新订单状态是否成功
     */
    boolean updateOrderStatusByOrderId(String orderId, Integer orderStatus);

    /**
     * 处理付款通知
     *
     * @param notifyData 通知数据
     * @param request    HttpServletRequest
     * @return 通知结果
     */
    String doPaymentNotify(String notifyData, HttpServletRequest request);

    /**
     * 处理超时订单
     * 检查订单状态
     *
     * @param productOrder 产品订单
     */
    void processingTimedOutOrders(ProductOrder productOrder);

    /**
     * 按订单 Id 关闭订单
     *
     * @param OrderId 订单 Id
     * @throws Exception Exception
     */
    void closedOrderByOrderId(String OrderId) throws Exception;
}
