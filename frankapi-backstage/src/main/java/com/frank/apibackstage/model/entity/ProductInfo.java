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
 * 产品信息
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("product_info")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 金额(分)
     */
    @TableField("total")
    private Long total;

    /**
     * 增加积分个数
     */
    @TableField("addPoints")
    private Long addPoints;

    /**
     * 产品类型
     * VIP：会员
     * RECHARGE：充值
     * RECHARGEACTIVITY：充值活动
     */
    @TableField("productType")
    private String productType;

    /**
     * 商品状态
     * 0：默认下线
     * 1：上线
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
