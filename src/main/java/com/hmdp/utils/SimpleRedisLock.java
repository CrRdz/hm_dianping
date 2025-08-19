package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{

    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String KEY_PREFIX = "lock:";
    @Override
    public boolean tryLock(long timeoutSec) {
        //获取当前线程的标识
        long threadId = Thread.currentThread().getId();
        //获取锁
        /**
         * KEY_PREFIX + name: 锁的键名，使用前缀避免命名冲突
         * threadId + "": 锁的值，存储线程ID（转换为字符串），用于后续解锁时验证锁的拥有者
         * timeoutSec: 锁的过期时间（秒），防止死锁
         * TimeUnit.SECONDS: 时间单位
         */
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + name, threadId + "", timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unLock() {
        //释放锁
        stringRedisTemplate.delete(KEY_PREFIX + name);
    }
}
