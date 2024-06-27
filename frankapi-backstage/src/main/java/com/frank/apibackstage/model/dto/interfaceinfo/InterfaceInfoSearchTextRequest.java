package com.frank.apibackstage.model.dto.interfaceinfo;

import com.frank.apicommon.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 文本搜索请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoSearchTextRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -6337349622479990038L;

    @Schema(description = "搜索文本", requiredMode = Schema.RequiredMode.REQUIRED, example = "壁纸")
    @NotEmpty(message = "搜索文本不能为空")
    private String searchText;
}
