package com.frank.apicommon.utils;

import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 *
 * @author Frank
 * @date 2024/06/22
 */
@Slf4j
@Component
public class RedissonLockUtil {

    @Resource
    public RedissonClient redissonClient;

    /**
     * redisson 分布式锁
     *
     * @param lockName 要获取的锁的名称，使用不同的锁名可以控制对不同资源的并发访问
     * @param supplier Supplier<T> 类型的函数接口，提供具体的操作逻辑，这个操作逻辑会在获取锁之后执行
     * @param code     状态码
     * @param msg      提示消息
     * @param <T>      数据类型
     * @return 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            // 等待时间为 0，表示不等待。如果锁不可用，立即返回 false
            // 锁的持有时间为 -1，表示无限期持有锁，直到显式解锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                return supplier.get();
            }
            throw new BusinessException(code, msg);
        } catch (Exception e) {
            throw new BusinessException(code, e.getMessage());
        } finally {
            // 检查当前线程是否持有锁
            // 如果是，则释放锁，并记录日志
            if (rLock.isHeldByCurrentThread()) {
                log.error("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName      锁名称
     * @param supplier      供应商
     * @param errorLogTitle 日志标题
     * @param code          状态码
     * @param msg           提示消息
     * @param <T>           数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, String errorLogTitle, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                return supplier.get();
            }
            throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
        } catch (Exception e) {
            log.error(errorLogTitle, e.getMessage());
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.error("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName   锁名称
     * @param supplier   供应商
     * @param logMessage 日志消息
     * @param code       状态码
     * @param msg        提示消息
     * @param <T>        数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, Runnable logMessage, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                return supplier.get();
            }
            throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
        } catch (Exception e) {
            logMessage.run();
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.error("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param waitTime  等待时间
     * @param leaseTime 租赁时间
     * @param unit      时间单位
     * @param lockName  锁名称
     * @param supplier  供应商
     * @param code      状态码
     * @param msg       提示消息
     * @param args      args
     * @param <T>       数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit unit, String lockName, Supplier<T> supplier, StatusCode code, String msg, Object... args) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, unit)) {
                return supplier.get();
            }
            throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param time     时长
     * @param unit     时间单位
     * @param lockName 锁名称
     * @param supplier 供应商
     * @param code     状态码
     * @param msg      提示消息
     * @param <T>      数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(long time, TimeUnit unit, String lockName, Supplier<T> supplier, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(time, unit)) {
                return supplier.get();
            }
            throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 锁名称
     * @param supplier 供应商
     * @param code     状态码
     * @param <T>      数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, StatusCode code) {
        return redissonDistributedLocks(lockName, supplier, code, code.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName   锁名称
     * @param supplier   供应商
     * @param logMessage 日志消息
     * @param code       状态码
     * @param <T>        数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, Runnable logMessage, StatusCode code) {
        return redissonDistributedLocks(lockName, supplier, logMessage, code, code.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 锁名称
     * @param supplier 供应商
     * @param msg      提示消息
     * @param <T>      数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, String msg) {
        return redissonDistributedLocks(lockName, supplier, StatusCode.OPERATION_ERROR, msg);
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 要获取的锁的名称，使用不同的锁名可以控制对不同资源的并发访问
     * @param supplier Supplier<T> 类型的函数接口，提供具体的操作逻辑。这个操作逻辑会在获取锁之后执行
     * @param <T>      数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier) {
        return redissonDistributedLocks(lockName, supplier, StatusCode.OPERATION_ERROR);
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName      锁名称
     * @param errorLogTitle 日志标题
     * @param supplier      供应商
     * @param <T>           数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, String errorLogTitle, Supplier<T> supplier) {
        return redissonDistributedLocks(lockName, supplier, errorLogTitle, StatusCode.OPERATION_ERROR, StatusCode.OPERATION_ERROR.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName   锁名称
     * @param supplier   供应商
     * @param logMessage 日志消息
     * @param <T>        数据类型
     * @return redisson 分布式锁
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, Runnable logMessage) {
        return redissonDistributedLocks(lockName, supplier, logMessage, StatusCode.OPERATION_ERROR);
    }

    /**
     * @param lockName 锁名称
     * @param runnable 可运行
     * @param code     状态码
     * @param msg      提示消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new BusinessException(code, msg);
            }
        } catch (Exception e) {
            throw new BusinessException(code, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("lockName: {}, unLockId: {}", lockName, Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 可运行
     * @param code     错误代码
     * @param msg      错误消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, String errorLogTitle, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
            }
        } catch (Exception e) {
            log.error(errorLogTitle, e.getMessage());
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("lockName: {}, unLockId:{}", lockName, Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName   锁名称
     * @param runnable   可运行
     * @param logMessage 日志消息
     * @param code       状态码
     * @param msg        提示消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, Runnable logMessage, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
            }
        } catch (Exception e) {
            logMessage.run();
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("lockName: {}, unLockId: {}", lockName, Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 可运行
     * @param code     状态码
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, StatusCode code) {
        redissonDistributedLocks(lockName, runnable, code, code.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 可运行
     * @param msg      提示消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, String msg) {
        redissonDistributedLocks(lockName, runnable, StatusCode.OPERATION_ERROR, msg);
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 可运行
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable) {
        redissonDistributedLocks(lockName, runnable, StatusCode.OPERATION_ERROR, StatusCode.OPERATION_ERROR.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName   锁名称
     * @param runnable   可运行
     * @param logMessage 日志消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, Runnable logMessage) {
        redissonDistributedLocks(lockName, runnable, logMessage, StatusCode.OPERATION_ERROR, StatusCode.OPERATION_ERROR.getMsg());
    }

    /**
     * redisson 分布式锁
     *
     * @param lockName      锁名称
     * @param errorLogTitle 日志消息
     * @param runnable      可运行
     */
    public void redissonDistributedLocks(String lockName, String errorLogTitle, Runnable runnable) {
        redissonDistributedLocks(lockName, runnable, errorLogTitle, StatusCode.OPERATION_ERROR, StatusCode.OPERATION_ERROR.getMsg());
    }

    /**
     * redisson 分布式锁
     * 可自定义 waitTime、leaseTime、TimeUnit
     *
     * @param waitTime  等待时间
     * @param leaseTime 租赁时间
     * @param unit      时间单位
     * @param lockName  锁名称
     * @param runnable  可运行
     * @param code      状态码
     * @param msg       提示消息
     */
    public void redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit unit, String lockName, Runnable runnable, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, unit)) {
                runnable.run();
            } else {
                throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
            }
        } catch (Exception e) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson 分布式锁，可自定义 time、unit
     *
     * @param time     时长
     * @param unit     时间单位
     * @param lockName 锁名称
     * @param runnable 可运行
     * @param code     状态码
     * @param msg      提示消息
     */
    public void redissonDistributedLocks(long time, TimeUnit unit, String lockName, Runnable runnable, StatusCode code, String msg) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(time, unit)) {
                runnable.run();
            } else {
                throw new BusinessException(StatusCode.PARAMS_ERROR, msg);
            }
        } catch (Exception e) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }
}