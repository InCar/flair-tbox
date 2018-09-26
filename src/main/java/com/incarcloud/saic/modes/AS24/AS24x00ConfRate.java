package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.modes.MongoX00ConfRate;
import org.bson.Document;

public class AS24x00ConfRate extends MongoX00ConfRate {
    @Override
    public float calc(Document bsonDoc){
        Object[] objs = new Object[3];

        objs[0] = bsonDoc.get("vehBMSPackVolV");
        objs[1] = bsonDoc.get("vehSpdAvgDrvnV");
        objs[2] = bsonDoc.get("vehOdoV");

        float fV = 0.0f;
        for(Object objV : objs){
            if(objV != null) fV += 0.2f;
        }

        return fV;
    }
}
