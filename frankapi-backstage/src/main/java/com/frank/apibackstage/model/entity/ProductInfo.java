package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Frank
 * @since 2024/06/27
 */
@Data
@Accessors(chain = true)
@TableName("product_info")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    @TableField("name")
    private String name;

    /**
     * 产品描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建人
     */
    @TableField("userId")
    private Long userId;

    /**
     * 产品金额（单位：分）
     */
    @TableField("total")
    private Long total;

    /**
     * 增加积分个数
     */
    @TableField("addPoints")
    private Integer addPoints;

    /**
     * 产品类型
     * 1：VIP会员；
     * 2：RECHARGE充值；
     * 3：RECHARGEACTIVITY充值活动
     */
    @TableField("productType")
    private Integer productType;

    /**
     * 产品状态
     * 0：下架；
     * 1：上架
     */
    @TableField("status")
    private Integer status;

    /**
     * 过期时间
     */
    @TableField("expirationTime")
    private Date expirationTime;

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
     * 0：未删除；
     * 1：已删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
