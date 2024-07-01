package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 付款信息表
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("payment_info")
public class PaymentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商户订单号
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 微信支付订单号
     */
    @TableField("transactionId")
    private String transactionId;

    /**
     * 交易类型
     */
    @TableField("tradeType")
    private String tradeType;

    /**
     * 交易状态
     * SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * REVOKED：已撤销（仅付款码支付会返回）
     * USERPAYING：用户支付中（仅付款码支付会返回）
     * PAYERROR：支付失败（仅付款码支付会返回）
     */
    @TableField("tradeState")
    private String tradeState;

    /**
     * 交易状态描述
     */
    @TableField("tradeStateDesc")
    private String tradeStateDesc;

    /**
     * 支付完成时间
     */
    @TableField("successTime")
    private String successTime;

    /**
     * 用户标识
     */
    @TableField("openid")
    private String openid;

    /**
     * 用户支付金额
     */
    @TableField("payerTotal")
    private Integer payerTotal;

    /**
     * 货币类型
     */
    @TableField("currency")
    private String currency;

    /**
     * 用户支付币种
     */
    @TableField("payerCurrency")
    private String payerCurrency;

    /**
     * 接口返回内容
     */
    @TableField("content")
    private String content;

    /**
     * 总金额
     */
    @TableField("total")
    private Integer total;

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
