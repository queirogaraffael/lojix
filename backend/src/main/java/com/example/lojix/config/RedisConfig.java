package com.example.lojix.config;

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
                JsonTypeInfo.As.PROPERTY);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            GenericJackson2JsonRedisSerializer jsonRedisSerializer) {

        java.util.Map<String, RedisCacheConfiguration> cacheConfigurations = new java.util.HashMap<>();
        cacheConfigurations.put("categoriasCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofHours(12)));
        cacheConfigurations.put("promocaoCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("produtosCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(30)));

        cacheConfigurations.put("produtosPageCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("promocoesPageCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("categoriasPageCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("clientesPageCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("funcionariosPageCache",
                cacheConfiguration(jsonRedisSerializer).entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration(jsonRedisSerializer))
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration(GenericJackson2JsonRedisSerializer jsonRedisSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheTtl))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer));
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
