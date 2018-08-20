package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.*;
import org.bson.Document;

public abstract class ModeMongo extends Mode{

    @Override
    public GBx01Overview makeGBx01Overview(Object data){
        if(data instanceof Document) return makeGBx01Overview((Document) data);
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

    public abstract GBx01Overview makeGBx01Overview(Document bsonDoc);
    public abstract GBx02Motor    makeGBx02Motor(Document bsonDoc);
    public abstract GBx04Engine   makeGBx04Engine(Document bsonDoc);
    public abstract GBx05Position makeGBx05Position(Document bsonDoc);
    public abstract GBx06Peak     makeGBx06Peak(Document bsonDoc);
    public abstract GBx07Alarm    makeGBx07Alarm(Document bsonDoc);

}
