package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.*;
import com.incarcloud.saic.modes.mongo.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class ModeMongo extends Mode{
    private static final Logger s_logger = LoggerFactory.getLogger(ModeMongo.class);
    private static final HashSet<String> s_setError = new HashSet<>();

    private final String mode;

    private final IMongoX01Overview x01Overview;
    private final IMongoX02Motor x02Motor;
    private final IMongoX04Engine x04Engine;
    private final IMongoX05Position x05Position;
    private final IMongoX06Peak x06Peak;
    private final IMongoX07Alarm x07Alarm;

    private final boolean[] gbSwitches;

    @Override
    public String getMode() {
        return mode;
    }

    ModeMongo(String mode, boolean[] gbSwitches){
        this.mode = mode;
        this.gbSwitches = gbSwitches;

        x01Overview = gbSwitches[0x01] ? create(mode, IMongoX01Overview.class) : null;
        x02Motor    = gbSwitches[0x02] ? create(mode, IMongoX02Motor.class)    : null;
        x04Engine   = gbSwitches[0x04] ? create(mode, IMongoX04Engine.class)   : null;
        x05Position = gbSwitches[0x05] ? create(mode, IMongoX05Position.class) : null;
        x06Peak     = gbSwitches[0x06] ? create(mode, IMongoX06Peak.class)     : null;
        x07Alarm    = gbSwitches[0x07] ? create(mode, IMongoX07Alarm.class)    : null;
    }

    @Override
    public GBx01Overview makeGBx01Overview(Object data){
        if(gbSwitches[0x01] && data instanceof Document)
            return x01Overview.makeGBx01Overview((Document) data);
        return null;
    }

    @Override
    public GBx02Motor makeGBx02Motor(Object data){
        if(gbSwitches[0x02] && data instanceof Document)
            return x02Motor.makeGBx02Motor((Document) data);
        return null;
    }

    @Override
    public GBx04Engine makeGBx04Engine(Object data){
        if(gbSwitches[0x04] && data instanceof Document)
            return x04Engine.makeGBx04Engine((Document) data);
        return null;
    }

    @Override
    public GBx05Position makeGBx05Position(Object data){
        if(gbSwitches[0x05] && data instanceof Document)
            return x05Position.makeGBx05Position((Document) data);
        return null;
    }

    @Override
    public GBx06Peak makeGBx06Peak(Object data){
        if(gbSwitches[0x06] && data instanceof Document)
            return x06Peak.makeGBx06Peak((Document) data);
        return null;
    }

    @Override
    public GBx07Alarm makeGBx07Alarm(Object data){
        if(gbSwitches[0x07] && data instanceof Document)
            return x07Alarm.makeGBx07Alarm((Document) data);
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T create(String mode, Class<T> cls){
        try {
            String modeFixed = mode.replace('-', '_');
            final String prefix = String.format("com.incarcloud.saic.modes.%s.%sx", modeFixed, modeFixed);
            final String name = prefix + cls.getSimpleName().substring("IMongoX".length());
            Class<?> clsObj = Class.forName(name);
            return (T)clsObj.newInstance();
        }
        catch (ClassNotFoundException ex){
            // reduce messages in log
            String key = ex.getMessage().substring(26);
            synchronized (s_setError){
                if(!s_setError.contains(key)){
                    s_setError.add(key);
                    s_logger.error("Class not found: {}", key);
                }
            }
        }
        catch (Exception ex){
            s_logger.error("Create object failed: {}", ex);
        }
        return null;
    }

}
