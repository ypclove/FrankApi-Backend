package com.frank.apibackstage.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.UserMapper;
import com.frank.apibackstage.model.dto.user.*;
import com.frank.apibackstage.model.entity.User;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.UserAccountStatusEnum;
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
import java.util.regex.Pattern;

import static cn.hutool.core.util.RadixUtil.RADIXS_59;
import static com.frank.apicommon.constant.EmailConstant.EMAIL_PATTERN;
import static com.frank.apicommon.constant.RedisConstant.CAPTCHA_CACHE_KEY;
import static com.frank.apicommon.constant.RedisConstant.REGISTER_KEY;
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
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(UserRegisterRequest userRegisterRequest) {
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
            // 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // accessKey / secretKey
            String accessKey = DigestUtils.md5DigestAsHex((userAccount + SALT + VOUCHER).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + userAccount).getBytes());

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
            user.setInvitationCode(RandomUtil.randomString(RADIXS_59, INVITATION_CODE_LENGTH));
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
    public long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest) {
        String emailAccount = userEmailRegisterRequest.getEmailAccount();
        String captcha = userEmailRegisterRequest.getCaptcha();
        String invitationCode = userEmailRegisterRequest.getInvitationCode();

        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
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

            // accessKey / secretKey
            String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(ACCESS_KEY_RANDOM_BYTES_LENGTH)) + SALT + VOUCHER).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + Arrays.toString(RandomUtil.randomBytes(SECRET_KEY_RANDOM_BYTES_LENGTH))).getBytes());

            // 插入数据
            User user = new User();
            user.setUserAccount(emailAccount);
            user.setAccessKey(accessKey);
            user.setEmail(emailAccount);
            user.setSecretKey(secretKey);
            if (invitationCodeUser != null) {
                // 通过邀请码注册，用户初始积分为 100，并且邀请人的积分 +100
                user.setBalance(INIT_BALANCE);
                this.addBalance(invitationCodeUser.getId(), INVITER_ADD_BALANCE);
            }
            user.setInvitationCode(RandomUtil.randomString(RADIXS_59, INVITATION_CODE_LENGTH));
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
     * @return 用户信息
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "该账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 用户通过邮箱登录
     *
     * @param userEmailLoginRequest 用户通过邮箱登录请求
     * @param request               HttpServletRequest
     * @return 用户信息
     */
    @Override
    public UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String captcha = userEmailLoginRequest.getCaptcha();

        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该邮箱未绑定账号，请先绑定账号");
        }

        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 用户绑定邮件
     *
     * @param userBindEmailRequest 用户绑定邮件请求
     * @param request              HttpServletRequest
     * @return 绑定邮件后的新用户信息
     */
    @Override
    public UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userBindEmailRequest.getEmailAccount();
        String captcha = userBindEmailRequest.getCaptcha();
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否绑定该邮箱
        UserVO loginUser = this.getLoginUser(request);
        if (loginUser.getEmail() != null && emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号已绑定此邮箱，请更换新的邮箱！");
        }
        // 查询邮箱是否已经绑定
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "此邮箱已被绑定！");
        }
        user = new User();
        user.setId(loginUser.getId());
        user.setEmail(emailAccount);
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "邮箱绑定失败！");
        }
        loginUser.setEmail(emailAccount);
        return loginUser;
    }

    /**
     * 用户解绑邮件
     *
     * @param userUnBindEmailRequest 用户解绑邮件请求
     * @param request                HttpServletRequest
     * @return 解绑邮件后的新用户信息
     */
    @Override
    public UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        String emailAccount = userUnBindEmailRequest.getEmailAccount();
        String captcha = userUnBindEmailRequest.getCaptcha();
        if (StringUtils.isAnyBlank(emailAccount, captcha)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        if (!Pattern.matches(EMAIL_PATTERN, emailAccount)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码已过期，请重新获取");
        }
        captcha = captcha.trim();
        if (!cacheCaptcha.equals(captcha)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "验证码输入有误");
        }
        // 查询用户是否绑定该邮箱
        UserVO loginUser = this.getLoginUser(request);
        if (loginUser.getEmail() == null) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号未绑定邮箱");
        }
        if (!emailAccount.equals(loginUser.getEmail())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "该账号未绑定此邮箱");
        }
        User user = new User();
        user.setId(loginUser.getId());
        user.setEmail("");
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "邮箱解绑失败，请稍后再试！");
        }
        loginUser.setEmail(null);
        return loginUser;
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
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
            throw new BusinessException(StatusCode.PROHIBITED, "账号已封禁");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 用户退出登录
     *
     * @param request HttpServletRequest
     * @return 用户退出登录是否成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
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
    public Long addUser(UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", user.getUserAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号重复");
        }
        user.setInvitationCode(RandomUtil.randomString(RADIXS_59, INVITATION_CODE_LENGTH));
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes());
        user.setUserPassword(encryptPassword);
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
     * @return 用户更新是否成功
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes());
        user.setUserPassword(encryptPassword);
        int result = userMapper.updateById(user);
        if (!(result > 0)) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "更新用户失败");
        }
        return true;
    }

    /**
     * 更新用户代金券
     *
     * @param user 用户
     * @return 更新代金券之后的用户
     */
    @Override
    public UserVO updateVoucher(User user) {
        String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(ACCESS_KEY_RANDOM_BYTES_LENGTH)) + SALT + VOUCHER).getBytes());
        String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + Arrays.toString(RandomUtil.randomBytes(SECRET_KEY_RANDOM_BYTES_LENGTH))).getBytes());
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean result = this.updateById(user);
        if (!result) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 用户通过邀请码注册成功时，增加邀请人的积分
     *
     * @param userId           用户 Id
     * @param addBalanceNumber 添加积分
     */
    @Override
    public boolean addBalance(Long userId, Integer addBalanceNumber) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.setSql("balance = balance + " + addBalanceNumber);
        return this.update(userLambdaUpdateWrapper);
    }
}
