package prism.akash.tools.reids.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis基础方法类
 * TODO : 系统·Redis基础方法封装
 *
 * @author HaoNan.Yan
 */
@Component
public class JedisUtil {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取RedisTemplate对象
     *
     * @return RedisTemplate
     */
    public RedisTemplate getRedisTemplate(){
        return redisTemplate;
    }

    /**
     * 设置redis的值
     * TODO :  适用redis数据类型 -> String
     *
     * @param key     redis的Key
     * @param value   待缓存的数据
     * @param timeout 数据过期时间 （ms毫秒）
     */
    public void setString(String key, String value, int timeout) {
        if (timeout == -1) {
            redisTemplate.opsForValue().set(key, value);
            setPersist(key);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 获取redis的值
     * TODO :  适用redis数据类型 -> String
     *
     * @param key 待获取的Key
     * @return
     */
    public String getString(String key) {
        Object getKey = redisTemplate.opsForValue().get(key);
        return getKey == null ? "" : getKey.toString();
    }


    /**
     * 设置redis的值
     * TODO :  适用redis数据类型 -> Hash
     *
     * @param key        redis的Key
     * @param hashValues 待缓存的数据 （Map类型）
     */
    public void setHash(String key, Map<String, String> hashValues) {
        redisTemplate.opsForHash().putAll(key, hashValues);
    }


    /**
     * 获取Hash中指定的某一字段值
     *
     * @param key   待获取数据的Key
     * @param field 待获取的数据字段code
     * @return
     */
    public String getHashField(String key, String field) {
        Object result = null;
        if (redisTemplate.opsForHash().hasKey(key, field)) {
            result = redisTemplate.opsForHash().get(key, field);
        }
        return result == null ? null : result.toString();
    }

    /**
     * 获取Hash全部字段数据
     *
     * @param key 待获取数据的Key
     * @return BaseData数据对象
     */
    public BaseData getHash(String key) {
        BaseData bd = new BaseData();
        if (hashKey(key)) {
            List<Object> kvList = redisTemplate.opsForHash().values(redisTemplate.opsForHash().keys(key));
            for (Object kv : kvList) {
                bd.putAll((Map) kv);
            }
        }
        return bd;
    }


    /**
     * 设置redis的值
     * TODO :  适用redis数据类型 -> List
     *
     * @param key        redis的Key
     * @param listValues 待缓存的数据 （List类型）
     */
    public void setList(String key, List<BaseData> listValues) {
        redisTemplate.opsForList().leftPushAll(key, listValues);
    }

    /**
     * 获取List的全部数据
     * @param key       redis的Key
     * @param start     开始位置，若为空则查询全部
     * @param end       结束位置，若为空则以start为基准查询单条数据
     * @return  list集合对象
     */
    public List<BaseData> getList(String key, Long start, Long end) {
        List<BaseData> list = new ArrayList<>();
        //若start，则视为查询全部
        if (start == null) {
            list.addAll(redisTemplate.opsForList().range(key, 0, redisTemplate.opsForList().size(key)));
        } else {
            //若end为空，则视为查询指定index的数据
            if (end != null) {
                list.addAll(redisTemplate.opsForList().range(key, start, end));
            }else{
                Object o = redisTemplate.opsForList().index(key, start);
                //若获取的对象不为空
                if(o != null){
                    list.add((BaseData)o);
                }
            }
        }
        return list;
    }

    /**
     * 判断当前Key值是否存在
     *
     * @param key 需要判断的Key
     * @return true / false
     */
    public Boolean hashKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除当前Key
     *
     * @param key 待删除的Key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 获取当前Key的过期时间 (ms毫秒)
     *
     * @param key
     * @return true / false
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置Key的过期时间 （默认为ms毫秒）
     *
     * @param key     待设置的key
     * @param timeOut 过期时间（ms毫秒）
     * @return true / false
     */
    public Boolean setExpire(String key, int timeOut) {
        return redisTemplate.expire(key, (int) timeOut, TimeUnit.MILLISECONDS);
    }

    /**
     * 数据持久化
     *
     * @param key 需要持久化的数据Key值
     * @return true / false
     */
    public Boolean setPersist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 获取当前Key的数据类型
     *
     * @param key 待获取数据类型的Key
     * @return 数据类型
     */
    public String getDataType(String key) {
        return redisTemplate.type(key).code();
    }
}
