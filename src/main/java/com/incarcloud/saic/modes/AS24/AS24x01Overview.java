package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

class AS24x01Overview extends MongoX implements IMongoX01Overview  {

    public GBx01Overview makeGBx01Overview(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEPTRdy = bsonDoc.getInteger("vehEPTRdy");
        int vehSysPwrMod = bsonDoc.getInteger("vehSysPwrMod");

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);

        /*
        IF vehEPTRdy=1
        THEN 车辆状态=0x01
        ELSE IF vehSysPwrMod=0
        THEN 车辆状态=0x02
        ELSE 车辆状态=0x03
        */
        if(vehEPTRdy == 1) data.setVehicleStatus((byte)0x01);
        else if(vehSysPwrMod == 0) data.setVehicleStatus((byte)0x02);
        else data.setVehicleStatus((byte)0x03);

        return data;
    }
}
