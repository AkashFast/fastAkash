package prism.akash.tools.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache工具类
 * TODO : cache工具类 主要用于持久化Schema Class对象
 *
 * @author HaoNan.Yan
 */
public class CacheClass {
    private static Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    public static void setCache(String key, Object obj, long seconds) {
        cache.put(key, obj);
    }

    public static Object getCache(String key) {
        return cache.get(key);
    }

    public static void removeCache(String key) {
        cache.remove(key);
    }
}
