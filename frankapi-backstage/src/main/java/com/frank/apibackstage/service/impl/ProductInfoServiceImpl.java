package com.frank.apibackstage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductInfoMapper;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.service.ProductInfoService;
import org.springframework.stereotype.Service;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {

}
