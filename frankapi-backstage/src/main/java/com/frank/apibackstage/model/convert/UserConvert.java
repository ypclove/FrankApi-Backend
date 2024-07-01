package com.frank.apibackstage.model.convert;

import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Frank
 * @date 2024/6/30
 */
@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    /**
     * 将 User 转换为 UserVO
     *
     * @param user {@link User}
     * @return {@link UserVO}
     */
    UserVO convert(User user);

    /**
     * 将 UserVO 转换为 User
     *
     * @param userVO {@link UserVO}
     * @return {@link User}
     */
    User convert(UserVO userVO);
}