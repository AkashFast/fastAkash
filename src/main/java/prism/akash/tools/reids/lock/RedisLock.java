package prism.akash.tools.reids.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisLock {

    //日志记录
    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private RedisTemplate redisTemplate;

    //设置默认
    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;

    private String lockKey;

    //锁超时时间，防止线程入锁后无限等待
    private int expireMsecs = 60 * 1000;

    //锁等待时间，防止线程饥饿
    private int timeoutMsecs = 10 * 1000;

    //当前加锁状态，默认为false
    private volatile boolean locked = false;

    /**
     * 初始化Redis锁
     * TODO   默认超时时间60s,默认等待时间10s
     *
     * @param redisTemplate
     * @param lockKey
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_lock";
    }

    /**
     * 初始化带有等待时间的Redis锁
     * TODO   默认超时时间60s
     *
     * @param redisTemplate
     * @param lockKey
     * @param timeoutMsecs  等待时间，单位为ms（毫秒）
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }


    /**
     * 初始化带有等待时间及超时时间的Redis锁
     * TODO   默认超时时间60s
     *
     * @param redisTemplate
     * @param lockKey
     * @param timeoutMsecs  等待时间，单位为ms（毫秒）
     * @param expireMsecs   超时时间，单位为ms（毫秒）
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    /**
     * 获取锁
     *
     * @return lock Key
     */
    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] data = connection.get(serializer.serialize(key));
                    connection.close();
                    if (data == null) {
                        return null;
                    }
                    return serializer.deserialize(data);
                }
            });
        } catch (Exception e) {
            logger.error("get redis error : key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    /**
     * Set if Not Exists 如果不存在则设置
     *
     * @param key
     * @param value
     * @return
     */
    private boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }

    /**
     * 重写（设置）已超时 / 过期的锁
     * @param key       锁的KEY
     * @param value     新的过期时间
     * @return
     */
    private String getSet(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return serializer.deserialize(ret);
                }
            });
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    /**
     * 获取锁
     * TODO     主要使用了setnx命令对锁进行了缓存
     * TODO     其缓存的key是当前锁的key,value是当前锁的到期时间
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean lock(String requestId) throws InterruptedException {
        int timeout = timeoutMsecs;
        //等待时间大于0则执行
        while (timeout >= 0) {
            //lock_key的超时 / 到期时间
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            String expiresStr = String.valueOf(expires + "," + requestId);
            //如果当前锁不存在，则对其进行设置
            if (this.setNX(lockKey, expiresStr)) {
                //TODO 设置成功 锁已获取
                locked = true;
                return true;
            }
            //获取当前KEY存储的超时 / 到期时间
            String currentValueStr = this.get(lockKey);
            //获取value中存储的过期时间
            String currentValue = currentValueStr == null ? null : currentValueStr.split(",")[0];
            //TODO 比对判断Redis中存储的时间是否已经超时 / 到期
            if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
                //锁已超时 / 到期
                String oldValueStr = this.getSet(lockKey, expiresStr);
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //TODO 设置成功 锁已获取
                    //防止信息误删 / 覆盖
                    //分布式排它锁：在多线程情况下，多个设置仅有一个线程满足获取锁的条件
                    locked = true;
                    return true;
                }
                //每执行一次，时间减少0.1秒
                timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;

                /**
                 * 相对公平·随机延迟竞争机制 （0.01s~0.1s）
                 * 不能保证绝对公平，但0.01~0.1s的差距影响极小
                 * 可以极大程度的防止饥饿进程出现
                 */
                Thread.sleep((int) (Math.random() * 90) + 10);
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public synchronized void unlock(String requestId) {
        if (locked) {
            //获取锁
            String currentValueStr = this.get(lockKey);
            //获取value中存储的过期时间
            String currentValue = currentValueStr == null ? null : currentValueStr.split(",")[1];
            //判断当前锁是否已被其他用户获取
            //未被其他人获取则释放
            if(currentValue != null && currentValue.equals(requestId)){
                redisTemplate.delete(lockKey);
            }
            locked = false;
        }
    }
}
