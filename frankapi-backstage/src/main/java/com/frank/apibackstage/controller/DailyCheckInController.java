package com.frank.apibackstage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frank.apibackstage.model.entity.DailyCheckIn;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.DailyCheckInService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import com.frank.apicommon.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.frank.apicommon.constant.UserConstant.USER_DAILY_CHECK_IN;

/**
 * @author Frank
 * @date 2024/06/22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dailyCheckIn")
public class DailyCheckInController {

    @Resource
    private UserService userService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @Resource
    private DailyCheckInService dailyCheckInService;

    /**
     * 签到
     *
     * @param request HttpServletRequest
     * @return 签到是否成功
     */
    @PostMapping("/doCheckIn")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> doDailyCheckIn(HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        String redissonLock = (USER_DAILY_CHECK_IN + loginUser.getUserAccount()).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            LambdaQueryWrapper<DailyCheckIn> dailyCheckInLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dailyCheckInLambdaQueryWrapper.eq(DailyCheckIn::getUserId, loginUser.getId());
            DailyCheckIn dailyCheckIn = dailyCheckInService.getOne(dailyCheckInLambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(dailyCheckIn)) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "签到失败，今日已签到");
            }
            dailyCheckIn = new DailyCheckIn();
            dailyCheckIn.setUserId(loginUser.getId());
            dailyCheckIn.setAddPoints(10);
            boolean dailyCheckInResult = dailyCheckInService.save(dailyCheckIn);
            boolean addWalletBalance = userService.addBalance(loginUser.getId(), dailyCheckIn.getAddPoints());
            boolean result = dailyCheckInResult & addWalletBalance;
            if (!result) {
                throw new BusinessException(StatusCode.OPERATION_ERROR);
            }
            return ResultUtils.success(true);
        }, "签到失败");
    }
}
