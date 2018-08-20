package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.*;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

public class ModeMongo extends Mode{

    private final String mode;

    private final IMongoX01Overview x01Overview;

    ModeMongo(String mode){
        this.mode = mode;

        x01Overview = create(mode, IMongoX01Overview.class);
    }

    @Override
    public GBx01Overview makeGBx01Overview(Object data){
        if(data instanceof Document)
            return x01Overview.makeGBx01Overview((Document) data);
        return null;
    }

    @Override
    public GBx02Motor makeGBx02Motor(Object data){
        if(data instanceof Document) return makeGBx02Motor((Document) data);
        return null;
    }

    @Override
    public GBx04Engine makeGBx04Engine(Object data){
        if(data instanceof Document) return makeGBx04Engine((Document) data);
        return null;
    }

    @Override
    public GBx05Position makeGBx05Position(Object data){
        if(data instanceof Document) return makeGBx05Position((Document) data);
        return null;
    }

    @Override
    public GBx06Peak makeGBx06Peak(Object data){
        if(data instanceof Document) return makeGBx06Peak((Document) data);
        return null;
    }

    @Override
    public GBx07Alarm makeGBx07Alarm(Object data){
        if(data instanceof Document) return makeGBx07Alarm((Document) data);
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T create(String mode, Class<T> cls){
        try {
            final String prefix = String.format("com.incarcloud.saic.modes.%s.%sx");
            final String name = prefix + cls.getSimpleName().substring("IMongoX".length());
            Class<?> clsObj = Class.forName(name);
            return (T)clsObj.newInstance();
        }catch (Exception ex){
            throw new RuntimeException("Create mode class failed", ex);
        }
    }

}
