package com.strutynskyi.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.movie.pagination")
@Getter
public class PaginationConfig {
    @Value("${api.movie.pagination.enabled}")
    private boolean enabled;
    @Value("${api.movie.pagination.default-page-number}")
    private int defaultPageNumber;
    @Value("${api.movie.pagination.default-page-size}")
    private int defaultPageSize;
    @Value("${api.movie.pagination.max-page-size}")
    private int maxPageSize;
}
