package com.strutynskyi.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "api.movie.filtering")
@Getter
public class FilteringConfig {
    @Value("${api.movie.filtering.enabled}")
    private boolean enabled;
    @Value("${api.movie.filtering.allowed-fields[0]}")
    private List<String> allowedFields;
}
