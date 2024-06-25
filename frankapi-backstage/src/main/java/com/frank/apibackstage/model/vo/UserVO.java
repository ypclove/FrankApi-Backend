package com.frank.apibackstage.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息（脱敏）
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String email;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 账号状态
     * 0：正常
     * 1：封号
     */
    private Integer status;

    /**
     * 积分
     */
    private Integer balance;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user / admin
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}