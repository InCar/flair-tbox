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

    ModeMongo(String mode){
        this.mode = mode;

        x01Overview = create(mode, IMongoX01Overview.class);
        x02Motor = create(mode, IMongoX02Motor.class);
        x04Engine = create(mode, IMongoX04Engine.class);
        x05Position = create(mode, IMongoX05Position.class);
        x06Peak = create(mode, IMongoX06Peak.class);
        x07Alarm = create(mode, IMongoX07Alarm.class);
    }

    @Override
    public GBx01Overview makeGBx01Overview(Object data){
        if(data instanceof Document && x01Overview != null)
            return x01Overview.makeGBx01Overview((Document) data);
        return null;
    }

    @Override
    public GBx02Motor makeGBx02Motor(Object data){
        if(data instanceof Document && x02Motor != null)
            return x02Motor.makeGBx02Motor((Document) data);
        return null;
    }

    @Override
    public GBx04Engine makeGBx04Engine(Object data){
        if(data instanceof Document && x04Engine != null)
            return x04Engine.makeGBx04Engine((Document) data);
        return null;
    }

    @Override
    public GBx05Position makeGBx05Position(Object data){
        if(data instanceof Document)
            return x05Position.makeGBx05Position((Document) data);
        return null;
    }

    @Override
    public GBx06Peak makeGBx06Peak(Object data){
        if(data instanceof Document && x06Peak != null)
            return x06Peak.makeGBx06Peak((Document) data);
        return null;
    }

    @Override
    public GBx07Alarm makeGBx07Alarm(Object data){
        if(data instanceof Document && x07Alarm != null)
            return x07Alarm.makeGBx07Alarm((Document) data);
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T create(String mode, Class<T> cls){
        try {
            final String prefix = String.format("com.incarcloud.saic.modes.%s.%sx", mode, mode);
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
