package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.*;
import com.incarcloud.saic.modes.mongo.*;
import org.bson.Document;

public class ModeMongo extends Mode{

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

        // TODO: 实现这些类型
        x02Motor = null;
        x04Engine = null;
        x05Position = null;
        x06Peak = null;
        x07Alarm = null;
        // x02Motor = create(mode, IMongoX02Motor.class);
        // x04Engine = create(mode, IMongoX04Engine.class);
        // x05Position = create(mode, IMongoX05Position.class);
        // x06Peak = create(mode, IMongoX06Peak.class);
        // x07Alarm = create(mode, IMongoX07Alarm.class);
    }

    @Override
    public GBx01Overview makeGBx01Overview(Object data){
        if(data instanceof Document)
            return x01Overview.makeGBx01Overview((Document) data);
        return null;
    }

    @Override
    public GBx02Motor makeGBx02Motor(Object data){
        if(data instanceof Document) return null;
        return null;
    }

    @Override
    public GBx04Engine makeGBx04Engine(Object data){
        if(data instanceof Document) return null;
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
        if(data instanceof Document) return null;
        return null;
    }

    @Override
    public GBx07Alarm makeGBx07Alarm(Object data){
        if(data instanceof Document) return null;
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T create(String mode, Class<T> cls){
        try {
            final String prefix = String.format("com.incarcloud.saic.modes.%s.%sx", mode, mode);
            final String name = prefix + cls.getSimpleName().substring("IMongoX".length());
            Class<?> clsObj = Class.forName(name);
            return (T)clsObj.newInstance();
        }catch (Exception ex){
            throw new RuntimeException("Create mode class failed", ex);
        }
    }

}
