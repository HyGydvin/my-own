
package ru.valera.my_own.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("ru.valera.my_own.dto.")
                .build();

        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .build();

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                serializer
                        )
                );
    }
}
