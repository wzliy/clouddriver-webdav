package com.zlin.clouddriver.config;

import com.zlin.clouddriver.client.AliYunDriverClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliYunDriveProperties.class)
@RequiredArgsConstructor
public class AliYunDriverAutoConfig {
    private final AliYunDriveProperties aliYunDriveProperties;

//    @Bean
    public AliYunDriverClient aliYunDriverClient() {
        return new AliYunDriverClient(aliYunDriveProperties);
    }
}
