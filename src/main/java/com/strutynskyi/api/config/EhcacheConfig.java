package com.strutynskyi.api.config;

import com.strutynskyi.api.models.Director;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class EhcacheConfig {
    @Bean
    public CacheManager getCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        CacheConfiguration<Long, Director> configuration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Director.class,
                        ResourcePoolsBuilder
                                .heap(100)
                                .offheap(1024, MemoryUnit.MB))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(1)))
                .build();

        javax.cache.configuration.Configuration<Long, Director> cacheConfiguration = Eh107Configuration
                .fromEhcacheCacheConfiguration(configuration);

        cacheManager.createCache("directors", cacheConfiguration);

        return cacheManager;
    }
}