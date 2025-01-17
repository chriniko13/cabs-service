package com.booking.consumer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration

@EnableJpaRepositories(basePackages = { "com.booking.*" })
@EntityScan(basePackages = { "com.booking.*" })

public class DomainConfig {

}
