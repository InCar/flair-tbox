package com.incarcloud.saic.modes.AS26;

import com.incarcloud.saic.GB32960.GBx04Engine;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX04Engine;
import org.bson.Document;

import java.time.ZonedDateTime;


public class AS26x04Engine extends MongoX implements IMongoX04Engine {

    public GBx04Engine makeGBx04Engine(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        GBx04Engine data = new GBx04Engine(vin, tmGMT8);
        data.setStatus(calcEngineStatus());
        data.setSpeed(calcAxleSpeed());
        data.setRate(calcOilConsumptionRate());

        return data;
    }

    private static short calcEngineStatus(){
        /*
        发动机状态=0xFF
        */
        short engineStatus = (byte)0xFF;

        return engineStatus;
    }

    private static int calcAxleSpeed(){
        /*
        曲轴转速=0xFF,0xFF
         */
        int axleSpeed = 0xFFFF;

        return axleSpeed;
    }

    private static float calcOilConsumptionRate(){
        /*
        燃油消耗率= 0xFF,0xFF
         */
        float oilConsumptionRate = 0xFFFF;

        return oilConsumptionRate;
    }
}
