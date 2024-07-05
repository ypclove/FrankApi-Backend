package com.frank.apibackstage.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日签到表
 *
 * @author Frank
 * @date 2024/06/22
 */
@Data
@Accessors(chain = true)
@TableName("daily_check_in")
public class DailyCheckIn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 签到人
     */
    @TableField("userId")
    private Long userId;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 签到增加积分个数
     */
    @TableField("addPoints")
    private Integer addPoints;

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
