package com.frank.apibackstage.model.convert;

import com.frank.apibackstage.model.entity.InterfaceInfo;
import com.frank.apibackstage.model.vo.InterfaceInfoVo;
import org.mapstruct.factory.Mappers;

/**
 * @author Frank
 * @date 2024/7/2
 */
public interface InterfaceConvert {

    InterfaceConvert INSTANCE = Mappers.getMapper(InterfaceConvert.class);

    /**
     * 将 InterfaceInfo 转换为 InterfaceInfoVo
     *
     * @param interfaceInfo {@link InterfaceInfo}
     * @return {@link InterfaceInfoVo}
     */
    InterfaceInfoVo convert(InterfaceInfo interfaceInfo);

    /**
     * 将 InterfaceInfoVo 转换为 InterfaceInfo
     *
     * @param interfaceInfoVo {@link InterfaceInfoVo}
     * @return {@link InterfaceInfo}
     */
    InterfaceInfo convert(InterfaceInfoVo interfaceInfoVo);
}
