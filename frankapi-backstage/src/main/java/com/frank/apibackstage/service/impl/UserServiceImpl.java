package com.frank.apibackstage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.UserMapper;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
