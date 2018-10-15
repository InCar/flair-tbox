package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.modes.MongoX00ConfRate;
import org.bson.Document;

public class OLD_AS24x00ConfRate extends MongoX00ConfRate {

    @Override
    public float calc(Document bsonDoc){
        Object[] objs = new Object[3];

        objs[0] = bsonDoc.get("vehBMSPackVol");
        objs[1] = bsonDoc.get("vehSpdAvgDrvn");
        objs[2] = bsonDoc.get("vehOdo");

        float fV = 0.0f;
        for(Object objV : objs){
            if(objV != null) fV += 0.15f;
        }

        return fV;
    }
}

