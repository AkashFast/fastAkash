package prism.akash.tools.reids.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import prism.akash.tools.fastJson.FastJson2JsonRedisSerializer;

/**
 * Redis序列化及反序列化
 *
 * @author HaoNan.Yan
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        fastJson2JsonRedisSerializer.setObjectMapper(objectMapper);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //TODO key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //TODO string的value采用fastJson序列化方式
        template.setValueSerializer(fastJson2JsonRedisSerializer);
        //TODO hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //TODO hash的value采用fastJson序列化方式
        template.setHashValueSerializer(fastJson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
