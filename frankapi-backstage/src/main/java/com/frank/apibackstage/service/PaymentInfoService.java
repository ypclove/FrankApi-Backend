package com.frank.apibackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.entity.PaymentInfo;
import com.frank.apibackstage.model.vo.PaymentInfoVo;

/**
 * @author Frank
 * @data 2024/06/22
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 创建付款信息
     *
     * @param paymentInfoVo 付款信息
     * @return 创建付款是否成功
     */
    boolean createPaymentInfo(PaymentInfoVo paymentInfoVo);
}
