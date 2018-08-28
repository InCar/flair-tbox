package com.incarcloud.saic.modes.OLD_IP24;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/25 10:34
 */
public class OLD_IP24x06Peak extends MongoX implements IMongoX06Peak {
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
        /*  IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVolt/92+Random(min=0,max=0.2)*/
        float BMSPackVolt = Float.parseFloat(bsonDoc.getString("BMSPackVolt"));
        data.setHighVoltage(singleBatteryVoltage(BMSPackVolt));
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 低 电 压 电 池 单 体 代 号 = BMSCellMinVolIndx
        short BMSCellMinVolIndx = Short.parseShort(bsonDoc.getString("BMSCellMinVolIndx"));
        data.setLowBatteryCode(BMSCellMinVolIndx);
        /*IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVolt/92-Random(min=0.1,max=0.2)*/
        float BMSPackVoltmin = Float.parseFloat(bsonDoc.getString("BMSPackVolt"));
        data.setLowVoltage(minimumVoltageOfsingle(BMSPackVoltmin));
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
        /* IF BMSCellTempMin=87 || 87.5
        THEN 最低温度值=0xFF
        ELSE 最 低 温 度 值 =
        BMSCellTempMin*/
        short BMSCellTempMin = Short.parseShort(bsonDoc.getString("BMSCellTempMin"));
        data.setLowTemperature(mimTemp(BMSCellTempMin));
        return data;
    }

    private static float singleBatteryVoltage(float BMSPackVolt){
        /*  IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVolt/92+Random(min=0,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/92+Math.random()/5);
        return singleBattery;
    }

    private static float minimumVoltageOfsingle(float BMSPackVolt){
        /*IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVolt/92-Random(min=0.1,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/92+((Math.random()/10)+0.1));
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

    private static short mimTemp(float BMSCellTempMin){
        /* IF BMSCellTempMin=87 || 87.5
        THEN 最低温度值=0xFF
        ELSE 最 低 温 度 值 =
        BMSCellTempMin*/
        short minTemperature = 0;
        if(BMSCellTempMin == 87||BMSCellTempMin == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) BMSCellTempMin;
        return minTemperature;
    }
}
