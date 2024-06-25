package com.frank.apibackstage.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frank.apibackstage.model.entity.DailyCheckIn;
import com.frank.apibackstage.service.DailyCheckInService;
import com.frank.apicommon.utils.RedissonLockUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签到任务
 *
 * @author Frank
 * @date 2024/6/23
 */
@Component
public class DailyCheckInJob {

    @Resource
    private DailyCheckInService dailyCheckInService;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    /**
     * 每天晚上 12 点批量清除签到列表
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void clearCheckInList() {
        redissonLockUtil.redissonDistributedLocks("clearCheckInList", () -> {
            // 每批删除的数据量
            int batchSize = 1000;
            // 是否还有数据需要删除
            boolean hasMoreData = true;

            while (hasMoreData) {
                // 分批查询数据
                List<DailyCheckIn> dataList = dailyCheckInService.list(new QueryWrapper<DailyCheckIn>().last("LIMIT " + batchSize));

                if (dataList.isEmpty()) {
                    // 没有数据了，退出循环
                    hasMoreData = false;
                } else {
                    // 批量删除数据
                    dailyCheckInService.removeByIds(dataList.stream().map(DailyCheckIn::getId).collect(Collectors.toList()));
                }
            }
        });
    }
}
