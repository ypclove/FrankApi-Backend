package com.frank.apibackstage.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Frank
 * @date 2024/7/4
 */
@Data
public class PhoneRespParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运营商
     */
    private String op;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 邮政编码
     */
    private String zipcode;

    /**
     * 地区编码
     */
    private String areacode;
}
