package com.incarcloud.saic.modes.IP24;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/22 15:27
 */
public class IP24x06Peak extends MongoX implements IMongoX06Peak {
    @Override
    public GBx06Peak makeGBx06Peak(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        GBx06Peak data = new GBx06Peak(vin, tmGMT8);


        //最高电压电池子系统号=0x01
        data.setHighBatteryId((byte) 0x01);
        //最高电压电池单体代号 = vehBMSCellMaxVolIndx
        byte vehBMSCellMaxVolIndx = Byte.parseByte(bsonDoc.getString("vehBMSCellMaxVolIndx"));
        data.setHighBatteryCode(vehBMSCellMaxVolIndx);
        /* IF vehBMSCellMaxVol=8.19||8.191
        THEN 电 池 单 体 电 压 最 高 值 =0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值 = vehBMSCellMaxVol*/
        float vehBMSCellMaxVol = Float.parseFloat(bsonDoc.getString("vehBMSCellMaxVol"));
        data.setHighVoltage(singleBatteryVoltage(vehBMSCellMaxVol));
        //最低电压电池子系统号
        data.setLowBatteryId((byte) 0x1);
        //最低电压电池单体代号
        byte vehBMSCellMinVolIndx = Byte.parseByte(bsonDoc.getString("vehBMSCellMinVolIndx"));
        data.setLowBatteryCode(vehBMSCellMinVolIndx);
        /*IF vehBMSCellMinVol=8.19||8.191
        THEN 电 池 单 体 电 压 最 低 值 =0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 低 值 = vehBMSCellMinVol*/
        float vehBMSCellMinVol = Float.parseFloat(bsonDoc.getString("vehBMSCellMinVol"));
        data.setLowVoltage(minimumVoltageOfsingle(vehBMSCellMinVol));
        //最高温度子系统号
        data.setHighTemperatureId((byte) 0x1);
        //最高温度探针序号  最 高 温 度 探 针 序 号 = /vehBMSCellMaxTemIndx
        byte vehBMSCellMaxTemIndx = Byte.parseByte(bsonDoc.getString("vehBMSCellMaxTemIndx"));
        data.setHighProbeCode(vehBMSCellMaxTemIndx);
        /*IF vehBMSCellMaxTem=87||87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =   vehBMSCellMaxTem*/
        float vehBMSCellMaxTem = Float.parseFloat(bsonDoc.getString("vehBMSCellMaxTem"));
        data.setHighTemperature(maxTemp(vehBMSCellMaxTem));
        //最低温度子系统号
        data.setLowTemperatureId((short) 0x1);
        //最低温度探针序号 最 低 温 度 探 针 序 号 = vehBMSCellMinTemIndx
        short vehBMSCellMinTemIndx = Short.parseShort(bsonDoc.getString("vehBMSCellMinTemIndx"));
        data.setLowProbeCode(vehBMSCellMinTemIndx);
        /* IF vehBMSCellMinTem=87||87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            vehBMSCellMinTem */
        short vehBMSCellMinTem = Short.parseShort(bsonDoc.getString("vehBMSCellMinTem"));
        data.setLowTemperature(mimTemp(vehBMSCellMinTem));
        return data;
    }
    private static float singleBatteryVoltage(float vehBMSCellMaxVol){
        /* IF vehBMSCellMaxVol=8.19||8.191
        THEN 电 池 单 体 电 压 最 高 值 =0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值 = vehBMSCellMaxVol*/
        float singleBattery;
        if(vehBMSCellMaxVol == 8.19||vehBMSCellMaxVol ==8.191) singleBattery = 0xFFFF;
        else singleBattery = vehBMSCellMaxVol * 0.001f;
        return singleBattery;
    }
    private static float minimumVoltageOfsingle(float vehBMSCellMinVol){
         /*IF vehBMSCellMinVol=8.19||8.191
        THEN 电 池 单 体 电 压 最 低 值 =0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 低 值 = vehBMSCellMinVol*/
        float minimumVoltage;
        if(vehBMSCellMinVol == 8.19||vehBMSCellMinVol == 8.191) minimumVoltage = 0xFFFF;
        else minimumVoltage = vehBMSCellMinVol * 0.001f;
        return minimumVoltage;
    }
    private static short maxTemp(float vehBMSCellMaxTem){
         /*IF vehBMSCellMaxTem=87||87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =   vehBMSCellMaxTem*/
        short maxTemperature = 0;
        if(vehBMSCellMaxTem == 87||vehBMSCellMaxTem == 87.5) maxTemperature = 0xFF;
        else maxTemperature = (short) vehBMSCellMaxTem;
        return maxTemperature;
    }
    private static short mimTemp(float vehBMSCellMinTem){
         /* IF vehBMSCellMinTem=87||87.5
        THEN 最低温度值=0xFF
        ELSE 最 低 温 度 值 =  vehBMSCellMinTem */
        short minTemperature = 0;
        if(vehBMSCellMinTem == 87||vehBMSCellMinTem == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) vehBMSCellMinTem;
        return minTemperature;
    }

}
