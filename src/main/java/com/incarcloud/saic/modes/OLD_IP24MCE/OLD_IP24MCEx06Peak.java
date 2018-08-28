package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/24 16:06
 */
public class OLD_IP24MCEx06Peak extends MongoX implements IMongoX06Peak {
    @Override
    public GBx06Peak makeGBx06Peak(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        GBx06Peak data = new GBx06Peak(vin, tmGMT8);

        //最高电压电池子系统号=0x01
        data.setHighBatteryId((short) 0x01);
        //最 高 电 压 电 池 单 体 代 号 = BMSCellMaxVolIndx
        short BMSCellMaxVolIndx = Short.parseShort(bsonDoc.getString("BMSCellMaxVolIndx"));
        data.setHighBatteryCode(BMSCellMaxVolIndx);
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最 高 值=BMSPackVoltHSC2/92+Random(min=0,max=0.2)*/
        float BMSPackVoltHSC2 = Float.parseFloat(bsonDoc.getString("BMSPackVoltHSC2"));
        data.setHighVoltage(singleBatteryVoltage(BMSPackVoltHSC2));
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 低 电 压 电 池 单 体 代 号 = BMSCellMinVolIndx
        short BMSCellMinVolIndx = Short.parseShort(bsonDoc.getString("BMSCellMinVolIndx"));
        data.setLowBatteryCode(BMSCellMinVolIndx);
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最低 值=BMSPackVoltHSC2/92-Random(min=0.1,max=0.2)*/
        float BMSPackVoltHSC2Min = Float.parseFloat(bsonDoc.getString("BMSPackVoltHSC2"));
        data.setLowVoltage(minimumVoltageOfsingle(BMSPackVoltHSC2Min));
        //最高温度子系统号=0x1
        data.setHighTemperatureId((short) 0x1);
        //最 高 温 度 探 针 序 号 = BMSCellMaxTemIndx
        short BMSCellMaxTemIndx = Short.parseShort(bsonDoc.getString("BMSCellMaxTemIndx"));
        data.setHighProbeCode(BMSCellMaxTemIndx);
        /*IF BMSCellTempMax=87 || 87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellTempMax*/
        float BMSCellTempMax = Float.parseFloat(bsonDoc.getString("BMSCellTempMax"));
        data.setHighTemperature(maxTemp(BMSCellTempMax));
        //最低温度子系统号=0x1
        data.setLowTemperatureId((short) 0x1);
        //最 低 温 度 探 针 序 号 = BMSCellMinTemIndx
        short BMSCellMinTemIndx = Short.parseShort(bsonDoc.getString("BMSCellMinTemIndx"));
        data.setLowProbeCode(BMSCellMinTemIndx);
        /* IF BMSCellTempMinHSC2=87 || 87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellTempMinHSC2*/
        short BMSCellTempMinHSC2 = Short.parseShort(bsonDoc.getString("BMSCellTempMinHSC2"));
        data.setLowTemperature(mimTemp(BMSCellTempMinHSC2));
        return data;
    }

    private static float singleBatteryVoltage(float BMSPackVoltHSC2){
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVoltHSC2/92+Random(min=0,max=0.2)*/
        float singleBattery;
        if(BMSPackVoltHSC2 == 1023.5 ||BMSPackVoltHSC2 == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVoltHSC2/92+Math.random()/5);
        return singleBattery;
    }

    private static float minimumVoltageOfsingle(float BMSPackVoltHSC2){
        /*IF BMSPackVoltHSC2=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVoltHSC2/92-Random(min=0.1,max=0.2)*/
        float singleBattery;
        if(BMSPackVoltHSC2 == 1023.5 ||BMSPackVoltHSC2 == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVoltHSC2/92+((Math.random()/10)+0.1));
        return singleBattery;
    }

    private static short maxTemp(float BMSCellTempMax){
        /*IF BMSCellTempMax=87 || 87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellTempMax*/
        short maxTemperature = 0;
        if(BMSCellTempMax == 87||BMSCellTempMax == 87.5) maxTemperature = 0xFF;
        else maxTemperature = (short) BMSCellTempMax;
        return maxTemperature;
    }

    private static short mimTemp(float BMSCellTempMinHSC2){
        /* IF BMSCellTempMinHSC2=87 || 87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellTempMinHSC2*/
        short minTemperature = 0;
        if(BMSCellTempMinHSC2 == 87||BMSCellTempMinHSC2 == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) BMSCellTempMinHSC2;
        return minTemperature;
    }
}
