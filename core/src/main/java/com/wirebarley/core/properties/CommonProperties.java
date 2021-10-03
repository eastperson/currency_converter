package com.wirebarley.core.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
@Getter
@PropertySource("classpath:properties/common.properties")
public class CommonProperties {

    @Value("${host}")
    private String host;

}