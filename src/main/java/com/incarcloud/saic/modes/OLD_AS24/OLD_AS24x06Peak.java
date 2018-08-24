package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/22 16:58
 */
public class OLD_AS24x06Peak extends MongoX implements IMongoX06Peak {
    public GBx06Peak makeGBx06Peak(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        GBx06Peak data = new GBx06Peak(vin, tmGMT8);
        //最高电压电池子系统号=0x01
        data.setHighBatteryId((short) 0x01);
        // 最 高 电 压 电 池 单 体 代 号=0xFF
        data.setHighBatteryCode((short) 0xFF);
        //电 池 单 体 电 压 最 高 值 = vehBMSCellMaxVol
        float vehBMSCellMaxVol = Float.parseFloat(bsonDoc.getString("vehBMSCellMaxVol"));
        data.setHighVoltage(vehBMSCellMaxVol);
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 高低电 压 电 池 单 体 代 号=0xFF
        data.setLowBatteryCode((short) 0xFF);
        //电池单体电压最低值=vehBMSCellMinVol
        float vehBMSCellMinVol = Float.parseFloat(bsonDoc.getString("vehBMSCellMinVol"));
        data.setLowVoltage(vehBMSCellMinVol);
        //最高温度子系统号=0x1
        data.setHighTemperatureId((short) 0x1);
        //最 高 温 度 探 针 序 号=0xFF
        data.setHighProbeCode((short) 0xFF);
        //最高温度值=vehBMSCellMaxTem
        short vehBMSCellMaxTem = Short.parseShort(bsonDoc.getString("vehBMSCellMaxTem"));
        data.setHighTemperature(vehBMSCellMaxTem);
        //最低温度子系统号=0x1
        data.setLowTemperatureId((short) 0x1);
        //最 高 温 度 探 针 序 号 =0xFF
        data.setLowProbeCode((short) 0xFF);
        //最低温度值=vehBMSCellMinTem
        short vehBMSCellMinTem = Short.parseShort(bsonDoc.getString("vehBMSCellMinTem"));
        data.setLowTemperature(vehBMSCellMinTem);
        return data;
    }
}
