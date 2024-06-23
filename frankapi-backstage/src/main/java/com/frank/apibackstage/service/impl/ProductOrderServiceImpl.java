package com.frank.apibackstage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductOrderMapper;
import com.frank.apibackstage.model.entity.ProductOrder;
import com.frank.apibackstage.service.ProductOrderService;
import org.springframework.stereotype.Service;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {

}
