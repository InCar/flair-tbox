package com.incarcloud.saic.modes.OLD_BP34;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/25 11:06
 */
public class OLD_BP34x06Peak extends MongoX implements IMongoX06Peak {
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
        /*  IF BMSPackVol=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最 高 值=BMSPackVol/90+Random(min=0,max=0.2)*/
        float BMSPackVolt = Float.parseFloat(bsonDoc.getString("BMSPackVolt"));
        data.setHighVoltage(singleBatteryVoltage(BMSPackVolt));
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 低 电 压 电 池 单 体 代 号 = BMSCellMinVolIndx
        short BMSCellMinVolIndx = Short.parseShort(bsonDoc.getString("BMSCellMinVolIndx"));
        data.setLowBatteryCode(BMSCellMinVolIndx);
        /*IF BMSPackVol=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最低 值=BMSPackVol/90-Random(min=0.1,max=0.2)*/
        float BMSPackVoltmin = Float.parseFloat(bsonDoc.getString("BMSPackVolt"));
        data.setLowVoltage(minimumVoltageOfsingle(BMSPackVoltmin));
        //最高温度子系统号=0x1
        data.setHighTemperatureId((short) 0x1);
        //最 高 温 度 探 针 序 号 = BMSCellMaxTemIndx
        short BMSCellMaxTemIndx = Short.parseShort(bsonDoc.getString("BMSCellMaxTemIndx"));
        data.setHighProbeCode(BMSCellMaxTemIndx);
        /*IF BMSCellMaxTem=87||87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellMaxTem*/
        float BMSCellMaxTem = Float.parseFloat(bsonDoc.getString("BMSCellMaxTem"));
        data.setHighTemperature(maxTemp(BMSCellMaxTem));
        //最低温度子系统号=0x1
        data.setLowTemperatureId((short) 0x1);
        //最 低 温 度 探 针 序 号 = BMSCellMinTemIndx
        short BMSCellMinTemIndx = Short.parseShort(bsonDoc.getString("BMSCellMinTemIndx"));
        data.setLowProbeCode(BMSCellMinTemIndx);
        /* IF BMSCellMinTem=87||87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellMinTem*/
        short BMSCellMinTem = Short.parseShort(bsonDoc.getString("BMSCellMinTem"));
        data.setLowTemperature(mimTemp(BMSCellMinTem));
        return data;
    }

    private static float singleBatteryVoltage(float BMSPackVolt){
        /*  IF BMSPackVol=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最 高 值=BMSPackVol/90+Random(min=0,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/90+Math.random()/5);
        return singleBattery;
    }

    private static float minimumVoltageOfsingle(float BMSPackVolt){
        /*IF BMSPackVol=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVol/90-Random(min=0.1,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/90+((Math.random()/10)+0.1));
        return singleBattery;
    }

    private static short maxTemp(float BMSCellMaxTem){
        /*IF BMSCellMaxTem=87||87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellMaxTem*/
        short maxTemperature = 0;
        if(BMSCellMaxTem == 87||BMSCellMaxTem == 87.5) maxTemperature = 0xFF;
        else maxTemperature = (short) BMSCellMaxTem;
        return maxTemperature;
    }

    private static short mimTemp(float BMSCellMinTem){
        /* IF BMSCellMinTem=87||87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellMinTem*/
        short minTemperature = 0;
        if(BMSCellMinTem == 87||BMSCellMinTem == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) BMSCellMinTem;
        return minTemperature;
    }
}
