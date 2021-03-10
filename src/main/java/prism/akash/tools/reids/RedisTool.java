package prism.akash.tools.reids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;
import prism.akash.tools.StringKit;
import prism.akash.tools.reids.lock.RedisLock;
import prism.akash.tools.reids.util.JedisUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis工具类
 * TODO : redis工具类
 *
 * @author HaoNan.Yan
 */
@Component
public class RedisTool {

    @Autowired
    JedisUtil JedisUtil;

    //日志记录
    private static Logger logger = LoggerFactory.getLogger(RedisTool.class);

    /**
     * 设置Key的有效时间
     * TODO 单位：ms（毫秒）
     *
     * @param key     需要设置有效时间的Key值
     * @param timeOut 有效时间（毫秒）, 若该值为 -1 则视为将该Key持久化
     * @return
     */
    private Boolean setExpire(String key, int timeOut) {
        Boolean expire = false;
        //判断KEY是否存在
        if (JedisUtil.hashKey(key)) {
            //判断有效时间是否为-1
            if (timeOut == -1) {
                // TODO 持久化
                expire = JedisUtil.setPersist(key);
            } else {
                expire = JedisUtil.setExpire(key, timeOut);
            }
        }
        return expire;
    }


    /**
     * 获取锁
     *
     * @param key          需要加锁的KEY
     * @param timeOutMsecs 超时时间
     * @param expireMsescs 等待时间
     * @param requestId    用户标识
     * @return
     */
    private RedisLock getLock(String key, int timeOutMsecs, int expireMsescs, String requestId) {
        //判断KEY是否有效
        if (key.isEmpty() || key == null) {
            return null;
        } else {
            //未填写timeOutMsecs及expireMsescs
            if (timeOutMsecs == -1 && expireMsescs == -1) {
                return new RedisLock(JedisUtil.getRedisTemplate(), key);
            } else if (timeOutMsecs == -1 && expireMsescs > 0) {
                //未填写timeOutMsecs,默认timeOutMsecs时间为(expireMsescs/10)ms
                return new RedisLock(JedisUtil.getRedisTemplate(), key, (expireMsescs / 10), expireMsescs);
            } else if (timeOutMsecs > 0 && expireMsescs == -1) {
                //未填写expireMsescs,默认expireMsescs时间为60s
                return new RedisLock(JedisUtil.getRedisTemplate(), key, timeOutMsecs);
            } else {
                //两个时间都存在
                return new RedisLock(JedisUtil.getRedisTemplate(), key, timeOutMsecs, expireMsescs);
            }
        }
    }

    /**
     * 删除数据
     *
     * @param key
     */
    public void delete(String key) {
        JedisUtil.deleteKey(key);
    }

    /**
     * 写入数据
     * TODO  String
     *
     * @param key
     * @param value
     * @param timeOut
     */
    public void set(String key, String value, int timeOut) {
        JedisUtil.setString(key, value, timeOut);
    }

    /**
     * 写入数据
     * TODO  Hash
     *
     * @param key
     * @param value
     * @param timeOut
     */
    public void set(String key, Map<String, String> value, int timeOut) {
        JedisUtil.setHash(key, value);
        setExpire(key, timeOut);
    }

    /**
     * 写入数据
     * TODO  List
     *
     * @param key
     * @param value
     * @param timeOut
     */
    public void set(String key, List<BaseData> value, int timeOut) {
        JedisUtil.setList(key, value);
        setExpire(key, timeOut);
    }



    /**
     * 读锁（String）
     * TODO 在类似秒杀的场景对读取数据
     *
     * @param key
     * @return
     */
    public String getOnLock(String key) {
        String result = "";
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, 500, 5000, requestId);
        try {
            if (lock.lock(requestId)) {
                result = get(key);
            }
        } catch (InterruptedException e) {
            logger.error("read(get) lock type string : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
        return result;
    }

    /**
     * 读锁（Hash）
     * TODO 在类似秒杀的场景对读取数据
     *
     * @param key
     * @return
     */
    public Map<String, String> getHashOnLock(String key) {
        Map<String, String> result = new HashMap<>();
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, 500, 5000, requestId);
        try {
            if (lock.lock(requestId)) {
                result = getHash(key);
            }
        } catch (InterruptedException e) {
            logger.error("read(get) lock type hash : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
        return result;
    }

    /**
     * 读锁（Hash - Field 单字段取值String）
     * TODO 在类似秒杀的场景对读取数据
     *
     * @param key
     * @return
     */
    public String getHashFieldOnLock(String key, String field) {
        String result = "";
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, 500, 5000, requestId);
        try {
            if (lock.lock(requestId)) {
                result = getHashField(key, field);
            }
        } catch (InterruptedException e) {
            logger.error("read(get) lock type hashField : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
        return result;
    }


    /**
     * 读锁（List）
     * TODO 在类似秒杀的场景对读取数据
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<BaseData> getListOnLock(String key, long start, long end) {
        List<BaseData> result = new ArrayList<>();
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, 500, 5000, requestId);
        try {
            if (lock.lock(requestId)) {
                result = getList(key, start, end);
            }
        } catch (InterruptedException e) {
            logger.error("read(get) lock type list : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
        return result;
    }


    /**
     * 写入数据（锁）
     * TODO  String
     *
     * @param key
     * @param value
     * @param timeOut
     * @param timeOutMsecs
     * @param expireMsescs
     */
    public void setOnLock(String key, String value, int timeOut, int timeOutMsecs, int expireMsescs) {
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, timeOutMsecs, expireMsescs, requestId);
        try {
            if (lock.lock(requestId)) {
                set(key, value, timeOut);
            }
        } catch (InterruptedException e) {
            logger.error("write(set) lock type string : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
    }

    /**
     * 写入数据（锁）
     * TODO  List
     *
     * @param key
     * @param value
     * @param timeOut
     * @param timeOutMsecs
     * @param expireMsescs
     */
    public void setOnLock(String key, Map<String, String> value, int timeOut, int timeOutMsecs, int expireMsescs) {
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, timeOutMsecs, expireMsescs, requestId);
        try {
            if (lock.lock(requestId)) {
                set(key, value, timeOut);
            }
        } catch (InterruptedException e) {
            logger.error("read(set) lock type hash : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
    }

    /**
     * 写入数据（锁）
     * TODO  Hash
     *
     * @param key
     * @param value
     * @param timeOut
     * @param timeOutMsecs
     * @param expireMsescs
     */
    public void setOnLock(String key, List<BaseData> value, int timeOut, int timeOutMsecs, int expireMsescs) {
        String requestId = StringKit.getUUID();
        RedisLock lock = getLock(key, timeOutMsecs, expireMsescs, requestId);
        try {
            if (lock.lock(requestId)) {
                set(key, value, timeOut);
            }
        } catch (InterruptedException e) {
            logger.error("read(set) lock type list : InterruptedException error -> " , e);
        } finally {
            lock.unlock(requestId);
        }
    }

    /**
     * 获取String数据
     * TODO String
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return JedisUtil.getString(key);
    }

    /**
     * 获取Hash数据
     * TODO Hash
     *
     * @param key
     * @return
     */
    public BaseData getHash(String key) {
        return JedisUtil.getHash(key);
    }

    /**
     * 获取Hash中某一字段的数据
     * TODO Hash
     *
     * @param key
     * @param field 字段名称
     * @return
     */
    public String getHashField(String key, String field) {
        return JedisUtil.getHashField(key, field);
    }

    /**
     * 获取List数据
     * TODO List （start，end都为-1时查询全部）
     *
     * @param key
     * @param start 开始
     * @param end   结束
     * @return
     */
    public List<BaseData> getList(String key, Long start, Long end) {
        return JedisUtil.getList(key, start, end);
    }
}
