package com.frank.apibackstage.model.dto.interfaceinfo;

import com.frank.apicommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 界面信息搜索文本请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoSearchTextRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -6337349622479990038L;

    private String searchText;
}
