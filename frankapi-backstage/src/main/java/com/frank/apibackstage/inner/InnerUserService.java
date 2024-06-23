package com.frank.apibackstage.inner;


import com.frank.apibackstage.model.vo.UserVO;

/**
 * 用户服务
 *
 * @author Frank
 * @data 2024/06/22
 */
public interface InnerUserService {

    /**
     * 通过访问密钥获取 invoke 用户
     *
     * @param accessKey 访问密钥
     * @return invoke 用户
     */
    UserVO getInvokeUserByAccessKey(String accessKey);
}
