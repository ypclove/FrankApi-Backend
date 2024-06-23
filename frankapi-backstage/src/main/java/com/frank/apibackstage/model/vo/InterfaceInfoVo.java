package com.frank.apibackstage.model.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.frank.apibackstage.model.entity.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class InterfaceInfoVo implements Serializable {
    private static final long serialVersionUID = 5713565919036400439L;
    private List<InterfaceInfo> records;
    private long total;
    private long size;
    private long current;
    private List<OrderItem> orders;
    private boolean optimizeCountSql;
    private boolean searchCount;
    private boolean optimizeJoinOfCountSql;
    private String countId;
    private Long maxLimit;
}
