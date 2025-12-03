package com.example.supergestor.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Profile("prod")
@Configuration
public class RedisConfig {

    @Value("${cache.ttl}")
    private long cacheTtl;

    @Bean
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            GenericJackson2JsonRedisSerializer jsonRedisSerializer) {

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration(jsonRedisSerializer))
                .transactionAware()
                .build();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration(GenericJackson2JsonRedisSerializer jsonRedisSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheTtl))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer)
                );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer jsonRedisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());


        return template;
    }

}
