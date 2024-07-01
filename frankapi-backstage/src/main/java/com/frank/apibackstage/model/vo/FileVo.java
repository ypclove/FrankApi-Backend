package com.frank.apibackstage.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传文件
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class FileVo implements Serializable {

    private static final long serialVersionUID = -4296258656223039373L;

    /**
     * 文件 Id
     */
    private String uid;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件状态
     */
    private Integer status;

    /**
     * 文件链接
     */
    private String url;
}