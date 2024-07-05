package com.frank.apiinterface.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Frank
 * @date 2024/7/4
 */
@Data
public class IpRespParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String prov;

    /**
     * 城市
     */
    private String city;

    /**
     * 运营商
     */
    private String isp;
}
