package com.incarcloud.saic.GB32960;

/**
 * 告警过滤器
 * 按时间顺序依次通过告警原始数据包
 * 过滤掉不符合条件的告警
 */
public class GBAlarmFilter {

    private final String vin;

    public GBAlarmFilter(String vin){
        this.vin = vin;
    }

    public GBx07Alarm filter(GBx07Alarm alarm){
        return null;
    }
}
