package com.frank.apibackstage.inner;

/**
 * 用户界面调用服务
 *
 * @author Frank
 * @data 2024/06/22
 */
public interface InnerUserInterfaceInvokeService {

    /**
     * 接口调用
     *
     * @param interfaceInfoId 接口信息 id
     * @param userId          用户 id
     * @param reduceScore     降低分数
     * @return 接口调用是否成功
     */
    boolean invoke(Long interfaceInfoId, Long userId, Integer reduceScore);
}
