package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户昵称
     */
    @TableField("userName")
    private String userName;

    /**
     * 账号
     */
    @TableField("userAccount")
    private String userAccount;

    /**
     * 用户头像
     */
    @TableField("userAvatar")
    private String userAvatar;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 性别
     * 0：男
     * 1：女
     */
    @TableField("gender")
    private String gender;

    /**
     * 用户角色：user / admin
     */
    @TableField("userRole")
    private String userRole;

    /**
     * 密码
     */
    @TableField("userPassword")
    private String userPassword;

    /**
     * accessKey
     */
    @TableField("accessKey")
    private String accessKey;

    /**
     * secretKey
     */
    @TableField("secretKey")
    private String secretKey;

    /**
     * 钱包余额，注册送 30 币
     */
    @TableField("balance")
    private Long balance;

    /**
     * 邀请码
     */
    @TableField("invitationCode")
    private String invitationCode;

    /**
     * 账号状态
     * 0：正常
     * 1：封号
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("isDelete")
    private Integer isDelete;
}
