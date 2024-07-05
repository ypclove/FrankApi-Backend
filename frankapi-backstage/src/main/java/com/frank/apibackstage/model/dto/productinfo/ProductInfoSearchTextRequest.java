package com.frank.apibackstage.model.dto.productinfo;

import com.frank.apicommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 产品信息搜索文本请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoSearchTextRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchText;
}
