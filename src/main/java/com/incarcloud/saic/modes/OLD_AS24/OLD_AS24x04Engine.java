package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.GB32960.GBx04Engine;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX04Engine;
import org.bson.Document;

import java.time.ZonedDateTime;

public class OLD_AS24x04Engine extends MongoX implements IMongoX04Engine {

    public GBx04Engine makeGBx04Engine(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehISGSpd = parseIntWithDef(bsonDoc, "vehISGSpd");
        int ISGSpd = parseIntWithDef(bsonDoc, "ISGSpd");
        float AvgFuelCsump = parseIntWithDef(bsonDoc, "AvgFuelCsump");
        float vehAvgFuelCsump_g = parseFloatWithDef(bsonDoc,"vehAvgFuelCsump_g");

        GBx04Engine data = new GBx04Engine(vin, tmGMT8);
        data.setStatus(calcEngineStatus(vehISGSpd));
        data.setSpeed(calcAxleSpeed(vehISGSpd, ISGSpd));
        data.setRate(calcOilConsumptionRate(bsonDoc, AvgFuelCsump, vehAvgFuelCsump_g));

        return data;
    }

    private static short calcEngineStatus(int vehISGSpd){
        /*
        IF vehISGSpd=65534 || 65535
        THEN 发动机状态=0xFF
        ELSE IFvehISGSpd>100
        THEN 发动机状态=0x01
        ELSE 发动机状态=0x02
        */
        short engineStatus;
        if(vehISGSpd == 65534 || vehISGSpd == 65535) engineStatus = 0xFF;
        else if(vehISGSpd > 100) engineStatus = 0x01;
        else engineStatus = 0x02;

        return engineStatus;
    }

    private static int calcAxleSpeed(int vehISGSpd, int ISGSpd){
        /*
        IF vehISGSpd=65534 || 65535
        THEN 曲轴转速=0xFF,0xFF
        ELSE IFvehISGSpd>100
        THEN 曲轴转速=ISGSpd
        ELSE 曲轴转速=0x00
         */
        int axleSpeed;
        if(vehISGSpd == 65534 || vehISGSpd == 65535) axleSpeed = 0xFFFF;
        else if(vehISGSpd > 100) axleSpeed = ISGSpd;
        else axleSpeed = 0x00;

        return axleSpeed;
    }

    private static float calcOilConsumptionRate(Document bsonDoc, float AvgFuelCsump, float vehAvgFuelCsump_g){
        /*
        IF 原始信号 containsKey("AvgFuelCsump") THEN
          AvgFuelCsump;
        ELSE
          vehAvgFuelCsump_g

         */
        float oilConsumptionRate;
        String buf = bsonDoc.getString("AvgFuelCsump");
        if(buf != null) oilConsumptionRate = AvgFuelCsump;
        else oilConsumptionRate = vehAvgFuelCsump_g;


        return oilConsumptionRate;
    }
}
