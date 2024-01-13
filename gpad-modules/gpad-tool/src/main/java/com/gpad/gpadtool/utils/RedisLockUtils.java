package com.gpad.gpadtool.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 详述：reids锁辅助类1
 *
 * @author： LF
 * @date： 2023年10月3
 */
public class RedisLockUtils {

    /**
     * 全局锁锁定
     */
    public static void lock(Object lockName) {
        if (lockName == null) {
            throw new RuntimeException("锁名称不能为空");
        }

        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);
        RLock lock = redissonClient.getFairLock(lockName.toString());
        lock.lock();
    }

    /**
     * 全局锁开锁
     */
    public static void unlock(Object lockName) {
        if (lockName == null) {
            throw new RuntimeException("锁名称不能为空");
        }
        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);
        RLock lock = redissonClient.getFairLock(lockName.toString());
        lock.unlock();
    }

}
