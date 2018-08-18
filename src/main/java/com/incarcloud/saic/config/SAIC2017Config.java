package com.incarcloud.saic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@ConfigurationProperties(prefix="saic2017")
public class SAIC2017Config {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate beginDate;
    public LocalDate getBeginDate(){ return beginDate; }
    public void setBeginDate(String val){
        beginDate = LocalDate.parse(val, s_fmt);
    }

    private LocalDate endDate;
    public LocalDate getEndDate(){ return endDate; }
    public void setEndDate(String val){
        endDate = LocalDate.parse(val, s_fmt);
    }

    private final MongoConfig mongo = new MongoConfig();
    public MongoConfig getMongo(){ return mongo; }
}
