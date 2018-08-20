package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

public class AS24x01Overview extends MongoX implements IMongoX01Overview  {

    public GBx01Overview makeGBx01Overview(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEPTRdy = Integer.parseInt(bsonDoc.getString("vehEPTRdy"));
        int vehSysPwrMod = Integer.parseInt(bsonDoc.getString("vehSysPwrMod"));
        int vehBMSBscSta = Integer.parseInt(bsonDoc.getString("vehBMSBscSta"));
        float vehBMSPackCrnt = Float.parseFloat((bsonDoc.getString("vehBMSPackCrnt")));

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);
        data.setVehicleStatus(calcVehicleStatus(vehEPTRdy, vehSysPwrMod));
        data.setChargingStatus(calcChargingStatus(vehBMSBscSta, vehBMSPackCrnt));

        return data;
    }

    private static byte calcVehicleStatus(int vehEPTRdy, int vehSysPwrMod){
        /*
        IF vehEPTRdy=1
        THEN 车辆状态=0x01
        ELSE IF vehSysPwrMod=0
        THEN 车辆状态=0x02
        ELSE 车辆状态=0x03
        */
        byte vehicleStatus;
        if(vehEPTRdy == 1) vehicleStatus = 0x01;
        else if(vehSysPwrMod == 0) vehicleStatus = 0x02;
        else vehicleStatus = 0x03;

        return vehicleStatus;
    }

    private static byte calcChargingStatus(int vehBMSBscSta, float vehBMSPackCrnt){
        /*
        IF vehBMSBscSta=6 || 7 || 12
        THEN 充电状态=0x01
        ELSE IF vehBMSBscSta=3 &&
        vehBMSPackCrnt<0
        THEN 充电状态=0x02
        ELSE IF vehBMSBscSta=9 || 10 || 13
        THEN 充电状态=0x04
        ELSE 充电状态=0x03
         */
        byte chargingStatus;
        if(vehBMSBscSta == 6 || vehBMSBscSta == 7 || vehBMSBscSta == 12)
            chargingStatus = 0x01;
        else if(vehBMSBscSta == 3 && vehBMSPackCrnt < 0)
            chargingStatus = 0x02;
        else if(vehBMSBscSta == 9 || vehBMSBscSta == 10 || vehBMSBscSta == 13)
            chargingStatus = 0x04;
        else
            chargingStatus = 0x03;

        return chargingStatus;
    }
}
