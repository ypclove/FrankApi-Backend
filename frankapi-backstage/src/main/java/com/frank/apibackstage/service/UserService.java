package com.frank.apibackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.dto.user.*;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank
 * @date 2024/06/22
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户 Id
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户通过邮件注册
     *
     * @param userEmailRegisterRequest 用户通过邮件注册请求
     * @return 用户 Id
     */
    Long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HttpServletRequest
     * @return 登录的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户通过邮箱登录
     *
     * @param userEmailLoginRequest 用户通过邮箱登录请求
     * @param request               HttpServletRequest
     * @return 登录的用户信息
     */
    UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request);

    /**
     * 获取验证码
     *
     * @param emailAccount 邮箱账号
     * @return 验证码
     */
    Boolean getCaptcha(String emailAccount);

    /**
     * 用户绑定邮件
     *
     * @param userBindEmailRequest 用户绑定邮件请求
     * @param request              HttpServletRequest
     * @return 用户信息
     */
    UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request);

    /**
     * 用户解绑邮件
     *
     * @param userUnBindEmailRequest 用户解绑邮件请求
     * @param request                HttpServletRequest
     * @return 用户信息
     */
    UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request HttpServletRequest
     * @return 用户信息
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 用户退出登录
     *
     * @param request HttpServletRequest
     * @return 用户退出登录是否成功
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 添加用户
     *
     * @param userAddRequest 添加用户请求
     * @return 用户 Id
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @return 更新之后的用户信息
     */
    UserVO updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 通过邀请码查询用户
     *
     * @param invitationCode 邀请码
     * @return 用户信息
     */
    UserVO getUserByInvitationCode(String invitationCode);

    /**
     * 更新开发者凭证
     *
     * @param request HttpServletRequest
     * @return 更新之后的用户信息
     */
    UserVO updateDevCred(HttpServletRequest request);

    /**
     * 添加积分
     *
     * @param userId           用户 Id
     * @param addBalanceNumber 添加的积分数量
     * @return 添加积分是否成功
     */
    Boolean addBalance(Long userId, Integer addBalanceNumber);

    /**
     * 是否为管理员
     *
     * @param request HttpServletRequest
     * @return 是否为管理员
     */
    Boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为游客
     *
     * @param request HttpServletRequest
     * @return 是否为游客
     */
    Boolean isTourist(HttpServletRequest request);
}
