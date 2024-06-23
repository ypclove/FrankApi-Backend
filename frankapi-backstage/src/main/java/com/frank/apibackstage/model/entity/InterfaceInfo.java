package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息表
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("interface_info")
public class InterfaceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 接口名称
     */
    @TableField("name")
    private String name;

    /**
     * 接口地址
     */
    @TableField("url")
    private String url;

    /**
     * 发布人
     */
    @TableField("userId")
    private Long userId;

    /**
     * 请求方法
     */
    @TableField("method")
    private String method;

    /**
     * 接口请求参数
     */
    @TableField("requestParams")
    private String requestParams;

    /**
     * 接口响应参数
     */
    @TableField("responseParams")
    private String responseParams;

    /**
     * 扣除积分数
     */
    @TableField("reduceScore")
    private Long reduceScore;

    /**
     * 请求示例
     */
    @TableField("requestExample")
    private String requestExample;

    /**
     * 请求头
     */
    @TableField("requestHeader")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField("responseHeader")
    private String responseHeader;

    /**
     * 返回格式(JSON等等)
     */
    @TableField("returnFormat")
    private String returnFormat;

    /**
     * 描述信息
     */
    @TableField("description")
    private String description;

    /**
     * 接口状态
     * 0：默认下线
     * 1：上线
     */
    @TableField("status")
    private Integer status;

    /**
     * 接口总调用次数
     */
    @TableField("totalInvokes")
    private Long totalInvokes;

    /**
     * 接口头像
     */
    @TableField("avatarUrl")
    private String avatarUrl;

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
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
