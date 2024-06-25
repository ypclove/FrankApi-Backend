package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户接口调用表
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("user_interface_invoke")
public class UserInterfaceInvoke implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 调用人 id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 接口 id
     */
    @TableField("interfaceId")
    private Long interfaceId;

    /**
     * 总调用次数
     */
    @TableField("totalInvokes")
    private Long totalInvokes;

    /**
     * 调用状态
     * 0：正常
     * 1：封号
     */
    @TableField("status")
    private Integer status;

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
     * 是否删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}
