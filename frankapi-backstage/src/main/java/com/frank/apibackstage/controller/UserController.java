package com.frank.apibackstage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.frank.apibackstage.annotation.AuthCheck;
import com.frank.apibackstage.model.dto.user.*;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.UserAccountStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.frank.apicommon.constant.RedisConstant.CAPTCHA_CACHE_KEY;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户 Id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        Long userId = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(userId);
    }

    /**
     * 用户通过邮箱注册
     *
     * @param userEmailRegisterRequest 用户通过邮箱注册请求
     * @return 用户 Id
     */
    @PostMapping("/email/register")
    public BaseResponse<Long> userEmailRegister(@Valid @RequestBody UserEmailRegisterRequest userEmailRegisterRequest) {
        long result = userService.userEmailRegister(userEmailRegisterRequest);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailRegisterRequest.getEmailAccount());
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          HttpServletRequest
     * @return 用户信息
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        UserVO user = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(user);
    }

    /**
     * 用户通过邮箱登录
     *
     * @param userEmailLoginRequest 用户通过邮箱登录的请求
     * @param request               HttpServletRequest
     * @return 用户信息
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> userEmailLogin(@Valid @RequestBody UserEmailLoginRequest userEmailLoginRequest,
                                               HttpServletRequest request) {
        UserVO user = userService.userEmailLogin(userEmailLoginRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailLoginRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 获取验证码
     *
     * @param emailAccount 邮件帐号
     * @return 获取验证码是否成功
     */
    @GetMapping("/getCaptcha")
    public BaseResponse<Boolean> getCaptcha(@Email(message = "邮箱格式不正确")
                                            @NotEmpty(message = "邮箱不能为空")
                                            @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
                                            String emailAccount) {
        Boolean result = userService.getCaptcha(emailAccount);
        return ResultUtils.success(result);
    }

    /**
     * 用户绑定邮件
     *
     * @param userBindEmailRequest 用户绑定邮件请求
     * @param request              HttpServletRequest
     * @return 绑定邮件后的新用户信息
     */
    @PostMapping("/bindEmail")
    public BaseResponse<UserVO> userBindEmail(@Valid @RequestBody UserBindEmailRequest userBindEmailRequest,
                                              HttpServletRequest request) {
        UserVO user = userService.userBindEmail(userBindEmailRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户解绑邮件
     *
     * @param userUnBindEmailRequest 用户解绑邮件请求
     * @param request                HttpServletRequest
     * @return 解绑邮件后的新用户信息
     */
    @PostMapping("/unbindEmail")
    public BaseResponse<UserVO> userUnBindEmail(@Valid @RequestBody UserUnBindEmailRequest userUnBindEmailRequest,
                                                HttpServletRequest request) {
        if (ObjectUtils.anyNull(userUnBindEmailRequest)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        UserVO user = userService.userUnBindEmail(userUnBindEmailRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userUnBindEmailRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request HttpServletRequest
     * @return 当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 用户退出登录
     *
     * @param request HttpServletRequest
     * @return 退出登录是否成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (ObjectUtils.anyNull(request)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 添加用户
     *
     * @param userAddRequest 添加用户请求
     * @return 用户 Id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Long> addUser(@Valid @RequestBody UserAddRequest userAddRequest) {
        Long userId = userService.addUser(userAddRequest);
        return ResultUtils.success(userId);
    }

    /**
     * 删除用户
     *
     * @param userId 用户 Id
     * @return 删除用户是否成功
     */
    @DeleteMapping("/delete/{userId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> deleteUser(@PathVariable @Valid
                                            @NotNull(message = "用户 Id 不能为空")
                                            @Min(value = 1L, message = "用户 Id 错误")
                                            Long userId) {
        boolean result = userService.removeById(userId);
        if (!result) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "删除用户失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 更新用户请求
     * @return 更新之后的用户信息
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = 0)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        Boolean result = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 Id 查询用户
     *
     * @param userId 用户 Id
     * @return 用户信息
     */
    @GetMapping("/get/{userId}")
    public BaseResponse<UserVO> getUserById(@PathVariable @Valid
                                            @NotNull(message = "用户 Id 不能为空")
                                            @Min(value = 1L, message = "用户 Id 错误")
                                            Long userId) {
        User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "查询用户失败");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户分页列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> getUserListByPage(UserQueryRequest userQueryRequest) {
        User userQuery = new User();

        if (userQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        BeanUtils.copyProperties(userQueryRequest, userQuery);
        String userAccount = userQueryRequest.getUserAccount();
        Integer gender = userQueryRequest.getGender();
        Integer userRole = userQueryRequest.getUserRole();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 判断查询条件是否为空，如果不为空则拼接查询条件；如果为空则查询所有数据
        queryWrapper.eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount)
                .eq(ObjectUtils.anyNotNull(gender), "gender", gender)
                .eq(ObjectUtils.anyNotNull(userRole), "userRole", userRole);
        Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> userVoPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVOList);
        return ResultUtils.success(userVoPage);
    }

    /**
     * 封号
     *
     * @param userId 用户 Id
     * @return 封号是否成功
     */
    @PostMapping("/ban/{userId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> banUser(@PathVariable @Valid
                                         @NotNull(message = "用户 Id 不能为空")
                                         @Min(value = 1L, message = "用户 Id 错误")
                                         Long userId) {
        User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserAccountStatusEnum.BAN.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 解封
     *
     * @param userId 用户 Id
     * @return 解封是否成功
     */
    @AuthCheck(mustRole = 0)
    @PostMapping("/normal/{userId}")
    public BaseResponse<Boolean> normalUser(@PathVariable @Valid
                                            @NotNull(message = "用户 Id 不能为空")
                                            @Min(value = 1L, message = "用户 Id 错误")
                                            Long userId) {
        User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserAccountStatusEnum.NORMAL.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 通过邀请码查询用户
     *
     * @param invitationCode 邀请码
     * @return 用户信息
     */
    @PostMapping("/get/invitationCode")
    public BaseResponse<UserVO> getUserByInvitationCode(@Valid @NotEmpty(message = "邀请码不能为空")
                                                        @Length(min = 6, max = 6, message = "邀请码不存在")
                                                        String invitationCode) {
        if (StringUtils.isBlank(invitationCode)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
        User invitationCodeUser = userService.getOne(userLambdaQueryWrapper);
        if (ObjectUtils.anyNull(invitationCodeUser)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "邀请码不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(invitationCodeUser, userVO);
        return ResultUtils.success(userVO);
    }
}
