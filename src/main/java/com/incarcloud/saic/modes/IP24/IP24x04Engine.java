package com.incarcloud.saic.modes.IP24;

import com.incarcloud.saic.GB32960.GBx04Engine;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX04Engine;
import org.bson.Document;

import java.time.ZonedDateTime;

public class IP24x04Engine extends MongoX implements IMongoX04Engine {

    public GBx04Engine makeGBx04Engine(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEnSpdF = Integer.parseInt(bsonDoc.getString("vehEnSpdF"));
        int vehEnSpd = Short.parseShort(bsonDoc.getString("vehEnSpd"));
        float vehAvgFuelCsump_g = Float.parseFloat(bsonDoc.getString("vehAvgFuelCsump_g"));

        GBx04Engine data = new GBx04Engine(vin, tmGMT8);
        data.setStatus(calcEngineStatus(vehEnSpdF, vehEnSpd));
        data.setSpeed(calcAxleSpeed(vehEnSpdF, vehEnSpd));
        data.setRate(calcOilConsumptionRate(vehAvgFuelCsump_g));

        return data;
    }

    private static short calcEngineStatus(int vehEnSpdF, int vehEnSpd){
        /*
        IF vehEnSpdF=1
        THEN 发动机状态=0xFF
        ELSE IF vehEnSpd>0
        THEN发动机状态=0x01
        ELSE发动机状态=0x02
        */
        short engineStatus;
        if(vehEnSpdF == 1) engineStatus = 0xFF;
        else if(vehEnSpd > 0) engineStatus = 0x01;
        else engineStatus = 0x02;

        return engineStatus;
    }

    private static int calcAxleSpeed(int vehEnSpdF, int vehEnSpd){
        /*
        IF vehEnSpdF=1
        THEN 曲轴转速=0xFF,0xFF
        ELSE 曲轴转速=vehEnSpd
         */
        int axleSpeed;
        if(vehEnSpdF == 1)
            axleSpeed = 0xFFFF;
        else
            axleSpeed = vehEnSpd;

        return axleSpeed;
    }

    private static float calcOilConsumptionRate(float vehAvgFuelCsump_g){
        /*
        燃油消耗率= vehAvgFuelCsump_g
         */
        float oilConsumptionRate = vehAvgFuelCsump_g;

        return oilConsumptionRate;
    }
}
