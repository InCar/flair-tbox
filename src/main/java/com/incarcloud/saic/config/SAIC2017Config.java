package com.incarcloud.saic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="saic2017")
public class SAIC2017Config {
    private final MongoConfig mongo = new MongoConfig();
    public MongoConfig getMongo(){ return mongo; }
}
