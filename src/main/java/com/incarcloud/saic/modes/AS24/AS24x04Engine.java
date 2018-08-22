package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.GBx04Engine;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX04Engine;
import org.bson.Document;

import java.time.ZonedDateTime;

public class AS24x04Engine extends MongoX implements IMongoX04Engine {

    public GBx04Engine makeGBx04Engine(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEnSpdSts = parseIntWithDef(bsonDoc, "vehEnSpdSts");
        int vehRPM = parseIntWithDef(bsonDoc, "vehRPM");
        float vehAvgFuelCsump_g = parseFloatWithDef(bsonDoc,"vehAvgFuelCsump_g");

        GBx04Engine data = new GBx04Engine(vin, tmGMT8);
        data.setStatus(calcEngineStatus(vehEnSpdSts, vehRPM));
        data.setSpeed(calcAxleSpeed(vehEnSpdSts, vehRPM));
        data.setRate(calcOilConsumptionRate(vehAvgFuelCsump_g));

        return data;
    }

    private static short calcEngineStatus(int vehEnSpdSts, int vehRPM){
        /*
        IF vehEnSpdSts=3
        THEN 发动机状态=0xFF
        ELSE IF vehRPM>0
        THEN 发动机状态=0x01
        ELSE 发动机状态=0x02
        */
        short engineStatus;
        if(vehEnSpdSts == 3) engineStatus = 0xFF;
        else if(vehRPM > 0) engineStatus = 0x01;
        else engineStatus = 0x02;

        return engineStatus;
    }

    private static int calcAxleSpeed(int vehEnSpdSts, int vehRPM){
        /*
        沿用原bigdata格式（1rpm）：
        IF vehEnSpdSts=3
        THEN 曲轴转速=0xFF,0xFF
        ELSE 曲轴转速= vehRPM
         */
        int axleSpeed;
        if(vehEnSpdSts == 3)
            axleSpeed = (short)0xFFFF;
        else
            axleSpeed = vehRPM;

        return axleSpeed;
    }

    private static float calcOilConsumptionRate(float vehAvgFuelCsump_g){
        /*
        沿用原bigdata格式（1rpm）：
        燃油消耗率= vehAvgFuelCsump_g
         */
        float oilConsumptionRate = vehAvgFuelCsump_g;

        return oilConsumptionRate;
    }
}
