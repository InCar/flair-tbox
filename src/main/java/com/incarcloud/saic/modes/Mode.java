package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.*;

import java.time.ZoneId;

/**
 * 把数据转换为GB32960数据
 */
public abstract class Mode<T> {
    public abstract GBx01Overview makeGBx01Overview(Object data);
    public abstract GBx02Motor    makeGBx02Motor(Object data);
    public abstract GBx04Engine   makeGBx04Engine(Object data);
    public abstract GBx05Position makeGBx05Position(Object data);
    public abstract GBx06Peak     makeGBx06Peak(Object data);
    public abstract GBx07Alarm    makeGBx07Alarm(Object data);
    public abstract String        getMode();

    public float calcConfRate(Object data){ return 0.0f; }
}
