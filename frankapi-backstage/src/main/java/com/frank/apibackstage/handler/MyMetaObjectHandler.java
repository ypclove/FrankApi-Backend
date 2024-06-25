package com.frank.apibackstage.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动插入（更新）创建时间和更新时间
 *
 * @author Frank
 * @date 2024/6/24
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 设置 createTime 和 updateTime 自动 insert
     *
     * @param metaObject 元信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 注意：这里是属性名称，不是字段名称
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    /**
     * 设置 updateTime 自动 update
     *
     * @param metaObject 元信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 注意：这里是属性名称，不是字段名称
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
