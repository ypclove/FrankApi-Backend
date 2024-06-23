package com.frank.apibackstage.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口更新头像请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class InterfaceInfoUpdateAvatarRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    /**
     * 接口头像
     */
    private String avatarUrl;
}
