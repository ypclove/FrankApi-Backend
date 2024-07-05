package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Frank
 * @since 2024/06/26
 */
@Data
@Accessors(chain = true)
@TableName("interface_info")
public class InterfaceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 发布人 Id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 请求方法
     */
    @TableField("method")
    private Integer method;

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
     * 扣除积分数个数
     */
    @TableField("reduceScore")
    private Integer reduceScore;

    /**
     * 请求示例
     */
    @TableField("requestExample")
    private String requestExample;

    /**
     * 响应示例
     */
    @TableField("responseExample")
    private String responseExample;

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
     * 返回格式
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
     * 0：关闭
     * 1：开启
     * 2：审核中
     */
    @TableField("status")
    private Integer status;

    /**
     * 接口总调用次数
     */
    @TableField("totalInvokes")
    private Integer totalInvokes;

    /**
     * 创建时间
     */
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除
     * 0：未删除
     * 1：已删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
