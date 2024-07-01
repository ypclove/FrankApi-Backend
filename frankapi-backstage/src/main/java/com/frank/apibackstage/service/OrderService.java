package com.frank.apibackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.model.vo.ProductOrderVo;
import com.frank.apibackstage.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Frank
 * @date 2024/6/28
 */
public interface OrderService extends IService<ProductOrder> {

    /**
     * 按支付方式创建订单
     *
     * @param productId 产品 Id
     * @param payType   支付方式
     * @param loginUser 付款用户
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo createOrderByPayType(Long productId, Integer payType, UserVO loginUser);

    /**
     * 按支付方式获取产品订单服务
     *
     * @param payType 支付方式
     * @return ProductOrderService
     */
    ProductOrderService getProductOrderServiceByPayType(Integer payType);

    /**
     * 处理订单通知
     *
     * @param notifyData 通知数据
     * @param request    HttpServletRequest
     * @return 通知结果
     */
    String doOrderNotify(String notifyData, HttpServletRequest request);

    /**
     * 按时间获得未支付订单
     *
     * @param minutes 分钟
     * @param remove  是否是删除
     * @param payType 支付方式
     * @return 未支付订单集合
     */
    List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType);
}
