package com.incarcloud.saic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix="saic2017")
public class SAIC2017Config {
    private final MongoConfig mongo = new MongoConfig();
    public MongoConfig getMongo(){ return mongo; }
}
