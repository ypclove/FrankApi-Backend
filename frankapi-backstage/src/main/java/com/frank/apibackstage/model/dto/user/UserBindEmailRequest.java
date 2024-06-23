package com.frank.apibackstage.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户绑定电子邮件请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class UserBindEmailRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
