package com.frank.apibackstage.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传图片状态vo
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class ImageVo implements Serializable {
    private static final long serialVersionUID = -4296258656223039373L;
    private String uid;
    private String name;
    private String status;
    private String url;
}