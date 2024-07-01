package com.frank.apibackstage.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Frank
 * @date 2024/6/30
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 积分
     */
    private Integer balance;

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
     * 用户角色
     * 0：管理员
     * 1：普通用户
     */
    private Integer userRole;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
