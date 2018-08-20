package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.*;
import com.incarcloud.saic.modes.ModeMongo;
import org.bson.Document;

import java.time.Instant;
import java.time.ZonedDateTime;

public class AS24 extends ModeMongo {

    @Override
    public GBx01Overview makeGBx01Overview(Document bsonDoc){
        String vin = bsonDoc.getString("vin");
        long tmEpoch = bsonDoc.getLong("tboxTime");
        ZonedDateTime tm = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);

        int vehEPTRdy = bsonDoc.getInteger("vehEPTRdy");
        int vehSysPwrMod = bsonDoc.getInteger("vehSysPwrMod");

        GBx01Overview data = new GBx01Overview(vin, tm);

        // if(vehEPTRdy == 1)
        /*
        IF vehEPTRdy=1
THEN 车辆状态=0x01
ELSE IF vehSysPwrMod=0
THEN 车辆状态=0x02
ELSE 车辆状态=0x03
         */

        return null;
    }

    @Override
    public GBx02Motor makeGBx02Motor(Document bsonDoc){
        return null;
    }

    @Override
    public GBx04Engine makeGBx04Engine(Document bsonDoc){
        return null;
    }

    @Override
    public GBx05Position makeGBx05Position(Document bsonDoc){
        return null;
    }

    @Override
    public GBx06Peak makeGBx06Peak(Document bsonDoc){
        return null;
    }

    @Override
    public GBx07Alarm makeGBx07Alarm(Document bsonDoc){
        return null;
    }
}
