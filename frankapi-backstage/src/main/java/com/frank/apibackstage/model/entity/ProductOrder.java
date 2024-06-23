package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品订单
 *
 * @author Frank
 * @data 2024/06/22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("product_order")
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单号
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 二维码地址
     */
    @TableField("codeUrl")
    private String codeUrl;

    /**
     * 创建人
     */
    @TableField("userId")
    private Long userId;

    /**
     * 商品id
     */
    @TableField("productId")
    private Long productId;

    /**
     * 商品名称
     */
    @TableField("orderName")
    private String orderName;

    /**
     * 金额(分)
     */
    @TableField("total")
    private Long total;

    /**
     * 交易状态
     * SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * REVOKED：已撤销（仅付款码支付会返回）
     * USERPAYING：用户支付中（仅付款码支付会返回）
     * PAYERROR：支付失败（仅付款码支付会返回）)
     */
    @TableField("status")
    private String status;

    /**
     * 支付方式
     * WX：微信（默认）
     * ZFB：支付宝
     */
    @TableField("payType")
    private String payType;

    /**
     * 商品信息
     */
    @TableField("productInfo")
    private String productInfo;

    /**
     * 支付宝 formData
     */
    @TableField("formData")
    private String formData;

    /**
     * 增加积分个数
     */
    @TableField("addPoints")
    private Long addPoints;

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
}
