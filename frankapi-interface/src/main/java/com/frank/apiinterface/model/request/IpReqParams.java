package com.frank.apiinterface.model.request;

import com.frank.apiinterface.annotation.CheckIpAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Frank
 * @date 2024/7/4
 */
@Data
public class IpReqParams implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "IP 地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "255.255.255.255")
    @CheckIpAddress(message = "IP 地址无效")
    private String ip;
}
