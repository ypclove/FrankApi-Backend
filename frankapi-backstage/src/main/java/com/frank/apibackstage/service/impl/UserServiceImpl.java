package com.frank.apibackstage.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.UserMapper;
import com.frank.apibackstage.model.convert.UserConvert;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.request.UserRequest;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.EmailService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.UserRoleEnum;
import com.frank.apicommon.enums.UserStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import com.frank.apicommon.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.util.RadixUtil.RADIXS_59;
import static com.frank.apicommon.constant.RedisConstant.*;
import static com.frank.apicommon.constant.UserConstant.*;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailService emailService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户 Id
     */
    @Override
    public Long userRegister(UserRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();

        String redissonLock = (REGISTER_KEY + userAccount).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "账号重复");
            }
            User invitationCodeUser = null;
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复 invitationCode，查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (invitationCodeUser == null) {
                    throw new BusinessException(StatusCode.OPERATION_ERROR, "该邀请码无效");
                }
            }
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 设置 accessKey / secretKey 的时候，顺序不要随意变动
            String accessKey = DigestUtils.md5DigestAsHex((userAccount + SALT + DEV_CRED).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + DEV_CRED + userAccount).getBytes());

            // 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            if (invitationCodeUser != null) {
                // 通过邀请码注册，用户初始积分为 100，并且邀请人的积分 +100
                user.setBalance(INIT_BALANCE);
                boolean addRes = this.addBalance(invitationCodeUser.getId(), INVITER_ADD_BALANCE);
                if (BooleanUtil.isFalse(addRes)) {
                    throw new BusinessException(StatusCode.PARAMS_ERROR, "通过邀请码注册失败");
                }
            }
            user.setInvitationCode(generateCaptcha(INVITATION_CODE_LENGTH));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }, "注册账号失败");
    }

    /**
     * 用户通过邮件注册
     *
     * @param userEmailRegisterRequest 用户通过邮件注册请求
     * @return 用户 Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long userEmailRegister(UserRequest userEmailRegisterRequest) {
        String emailAccount = userEmailRegisterRequest.getEmailAccount();
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }

        String captcha = userEmailRegisterRequest.getCaptcha();
        if (!cacheCaptcha.trim().equalsIgnoreCase(captcha.trim())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }

        String redissonLock = (CAPTCHA_CACHE_KEY + emailAccount).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", emailAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "账号重复");
            }

            // 邀请码无效
            User invitationCodeUser = null;
            String invitationCode = userEmailRegisterRequest.getInvitationCode();
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复 invitationCode，查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (Objects.isNull(invitationCodeUser)) {
                    throw new BusinessException(StatusCode.OPERATION_ERROR, "该邀请码无效");
                }
            }

            // 生成 accessKey / secretKey
            String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(DEV_CRED_KEY_RANDOM_BYTES_LENGTH)) + SALT + DEV_CRED).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + DEV_CRED + Arrays.toString(RandomUtil.randomBytes(DEV_CRED_KEY_RANDOM_BYTES_LENGTH))).getBytes());

            // 插入数据
            User user = new User();
            user.setUserAccount(emailAccount);
            user.setEmail(emailAccount);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            if (Objects.nonNull(invitationCodeUser)) {
                // 通过邀请码注册，用户初始积分为 100，并且邀请人的积分 +100
                user.setBalance(INIT_BALANCE);
                this.addBalance(invitationCodeUser.getId(), INVITER_ADD_BALANCE);
            }
            user.setInvitationCode(generateCaptcha(INVITATION_CODE_LENGTH));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }, "邮箱账号注册失败");
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HttpServletRequest
     * @return 登录的用户信息
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount).eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "该账号已封禁");
        }
        UserVO userVO = UserConvert.INSTANCE.convert(user);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 用户通过邮箱登录
     *
     * @param userEmailLoginRequest 用户通过邮箱登录请求
     * @param request               HttpServletRequest
     * @return 登录的用户信息
     */
    @Override
    public UserVO userEmailLogin(UserRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String cacheCaptcha = Objects.requireNonNull(redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount));
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }

        String captcha = userEmailLoginRequest.getCaptcha();
        if (!cacheCaptcha.trim().equalsIgnoreCase(captcha.trim())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该邮箱未绑定账号，请先绑定账号");
        }

        // 用户状态
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = UserConvert.INSTANCE.convert(user);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 获取验证码
     *
     * @param emailAccount 邮箱账号
     * @return 验证码
     */
    @Override
    public Boolean getCaptcha(String emailAccount) {
        String captcha = generateCaptcha(CAPTCHA_LENGTH);
        try {
            boolean res = emailService.sendEmail(emailAccount, captcha);
            if (BooleanUtil.isFalse(res)) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "邮件发送给失败");
            }
            // 邮箱验证码缓存 5 分钟
            redisTemplate.opsForValue().set(CAPTCHA_CACHE_KEY + emailAccount, captcha, CAPTCHA_CACHE_TTL, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("验证码获取失败：{}", e.getMessage());
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码获取失败");
        }
    }

    /**
     * 用户绑定邮件
     *
     * @param userBindEmailRequest 用户绑定邮件请求
     * @param request              HttpServletRequest
     * @return 用户信息
     */
    @Override
    public UserVO userBindEmail(UserRequest userBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userBindEmailRequest.getEmailAccount();
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }

        String captcha = userBindEmailRequest.getCaptcha();
        if (!cacheCaptcha.trim().equalsIgnoreCase(captcha.trim())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }

        // 查询该邮箱是否已经绑定该用户
        UserVO loginUser = this.getLoginUser(request);
        if (Objects.nonNull(loginUser.getEmail()) && emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号已绑定此邮箱，请更换新的邮箱");
        }

        // 查询该邮箱是否已经绑定其他用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.nonNull(user)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "此邮箱已被其他用户绑定，请更换新的邮箱");
        }

        loginUser.setEmail(emailAccount);
        User convertUser = UserConvert.INSTANCE.convert(loginUser);
        int bindEmailResult = userMapper.updateById(convertUser);
        if (bindEmailResult <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "邮箱绑定失败");
        }
        return UserConvert.INSTANCE.convert(convertUser);
    }

    /**
     * 用户解绑邮件
     *
     * @param userUnBindEmailRequest 用户解绑邮件请求
     * @param request                HttpServletRequest
     * @return 用户信息
     */
    @Override
    public UserVO userUnBindEmail(UserRequest userUnBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userUnBindEmailRequest.getEmailAccount();
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }

        String captcha = userUnBindEmailRequest.getCaptcha();
        if (!cacheCaptcha.trim().equalsIgnoreCase(captcha.trim())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }

        // 查询用户是否绑定该邮箱
        UserVO loginUser = this.getLoginUser(request);
        if (Objects.isNull(loginUser.getEmail())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号未绑定邮箱");
        }

        // 如果绑定了，但是不是该邮箱，抛出异常
        if (!emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号未绑定此邮箱");
        }

        loginUser.setEmail("");
        User user = UserConvert.INSTANCE.convert(loginUser);
        int unBindEmailResult = userMapper.updateById(user);
        if (unBindEmailResult <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "邮箱解绑失败");
        }
        return UserConvert.INSTANCE.convert(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request HttpServletRequest
     * @return 用户信息
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (Objects.isNull(currentUser) || Objects.isNull(currentUser.getId())) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        User user = this.getById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        if (user.getStatus().equals(UserStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "账号已封禁");
        }
        return UserConvert.INSTANCE.convert(user);
    }

    /**
     * 用户退出登录
     *
     * @param request HttpServletRequest
     * @return 用户退出登录是否成功
     */
    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if (Objects.isNull(request.getSession().getAttribute(USER_LOGIN_STATE))) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 添加用户
     *
     * @param userAddRequest 添加用户请求
     * @return 用户 Id
     */
    @Override
    public Long addUser(UserRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", user.getUserAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号重复");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes());
        String accessKey = DigestUtils.md5DigestAsHex((userAddRequest.getUserAccount() + SALT + DEV_CRED).getBytes());
        String secretKey = DigestUtils.md5DigestAsHex((SALT + DEV_CRED + userAddRequest.getUserAccount()).getBytes());

        user.setInvitationCode(generateCaptcha(INVITATION_CODE_LENGTH));
        user.setUserPassword(encryptPassword);
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "添加用户失败");
        }
        return user.getId();
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 更新用户请求
     * @return 更新之后的用户信息
     */
    @Override
    public UserVO updateUser(UserRequest userUpdateRequest, HttpServletRequest request) {
        UserVO loginUser = getLoginUser(request);
        if (!loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getCode()) || !userUpdateRequest.getId().equals(loginUser.getId())) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR, "操作无权限");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        String userAccount = user.getUserAccount();
        // 账户不能重复
        if (StringUtils.isNotBlank(userAccount)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            // 排除当前用户
            queryWrapper.eq("userAccount", userAccount).ne("id", user.getId());
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "该账号已使用，请重换一个");
            }
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", user.getId());
        int result = userMapper.update(user, wrapper);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "更新用户失败");
        }
        return UserConvert.INSTANCE.convert(user);
    }

    /**
     * 通过邀请码查询用户
     *
     * @param invitationCode 邀请码
     * @return 用户信息
     */
    @Override
    public UserVO getUserByInvitationCode(String invitationCode) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
        User invitationCodeUser = userMapper.selectOne(userLambdaQueryWrapper);
        if (Objects.isNull(invitationCodeUser)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "邀请码不存在");
        }
        return UserConvert.INSTANCE.convert(invitationCodeUser);
    }

    /**
     * 更新开发者凭证
     *
     * @param request HttpServletRequest
     * @return 更新之后的用户信息
     */
    @Override
    public UserVO updateDevCred(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        UserVO loginUser = getLoginUser(request);
        User user = UserConvert.INSTANCE.convert(loginUser);
        String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(DEV_CRED_KEY_RANDOM_BYTES_LENGTH)) + SALT + DEV_CRED).getBytes());
        String secretKey = DigestUtils.md5DigestAsHex((SALT + DEV_CRED + Arrays.toString(RandomUtil.randomBytes(DEV_CRED_KEY_RANDOM_BYTES_LENGTH))).getBytes());
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "开发者凭证更新失败");
        }
        return UserConvert.INSTANCE.convert(user);
    }


    /**
     * 用户通过邀请码注册成功时，增加邀请人的积分
     *
     * @param userId           用户 Id
     * @param addBalanceNumber 添加积分
     */
    @Override
    public Boolean addBalance(Long userId, Integer addBalanceNumber) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance + " + addBalanceNumber);
        return this.update(userLambdaUpdateWrapper);
    }

    /**
     * 是否为管理员
     *
     * @param request HttpServletRequest
     * @return 是否为管理员
     */
    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (Objects.isNull(currentUser) || Objects.isNull(currentUser.getId())) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询，TODO：追求性能的话可以注释，直接走缓存
        long userId = currentUser.getId();
        User user = userMapper.selectById(userId);
        return Objects.nonNull(user) && user.getUserRole().equals(UserRoleEnum.ADMIN.getCode());
    }

    /**
     * 是否为游客
     *
     * @param request HttpServletRequest
     * @return 游客信息
     */
    @Override
    public Boolean isTourist(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (Objects.isNull(currentUser) || Objects.isNull(currentUser.getId())) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询，TODO：追求性能的话可以注释，直接走缓存
        long userId = currentUser.getId();
        User user = userMapper.selectById(userId);
        return Objects.nonNull(user) && user.getUserRole().equals(UserRoleEnum.USER.getCode());
    }

    /**
     * 生成校验码（验证码 / 邀请码）
     * 字符集：A-Z，a-z，0-9
     *
     * @param length 长度
     * @return 校验码
     */
    private String generateCaptcha(Integer length) {
        // 或者使用 RandomStringUtils.randomAlphanumeric(length)
        return RandomUtil.randomString(RADIXS_59, length);
    }
}
