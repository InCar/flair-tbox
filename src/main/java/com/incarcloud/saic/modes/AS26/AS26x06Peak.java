package com.incarcloud.saic.modes.AS26;

import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX06Peak;
import org.bson.Document;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author wanghan
 * @date 2018/8/22 10:33
 */
public class AS26x06Peak extends MongoX implements IMongoX06Peak  {
    @Override
    public GBx06Peak makeGBx06Peak(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        GBx06Peak data = new GBx06Peak(vin, tmGMT8);
        //最高电压电池子系统号=0x01
        data.setHighBatteryId((byte) 0x01);
        /*电池单体电压最高值=max[vehBMSCellVolt(1) ,…, vehBMSCellVolt(单体电池总数)]*/
        List<Document> tmplist = (List<Document>)bsonDoc.get("vehBMSCellVolt", List.class);
        float vehBMSCellMaxVol = 0f;
        if(tmplist != null){
            for(Document subDoc : tmplist){
                float vol = Float.parseFloat(subDoc.getString("vehBMSCellVolt"));
                if (vol > vehBMSCellMaxVol) {
                    vehBMSCellMaxVol = vol;
                }
            }
        }
        data.setHighVoltage(vehBMSCellMaxVol*0.001f);
        /*IF电池单体电压最高值=65.535
        THEN最高电压电池单体代号=0xFF
        ELSE 最高电压电池单体代号=Index{电池单体电压最高值 of [vehBMSCellVolt(1) ,…, vehBMSCellVolt(单体电池总数)]}*/
        float highVoltage = data.getHighVoltage();
        byte indexVol= 0;
        if(highVoltage == 65.535){
            data.setHighBatteryCode((byte)0xFF);
        }else{
            List<Document> tmplist1 = (List<Document>)bsonDoc.get("vehBMSCellVolt", List.class);
            if(tmplist1 != null){
                for (byte i = 0; i < tmplist1.size(); i++) {
                    Document subDoc = tmplist1.get(i);
                    if (highVoltage ==  Float.parseFloat(subDoc.getString("vehBMSCellVolt"))) {
                        indexVol = i ;
                        break;
                    }
                }
            }
         }

        data.setHighBatteryCode((byte) (indexVol+1));
        //最低电压电池子系统号
        data.setLowBatteryId((byte) 0x1);
        //电池单体电压最低值=min[vehBMSCellVolt(1) ,…, vehBMSCellVolt(单体电池总数)]
        List<Document> tmpEminlist = (List<Document>)bsonDoc.get("vehBMSCellVolt", List.class);
        float vehBMSCellVoltMin = Float.MAX_VALUE;
        if(tmpEminlist != null){
            for(Document subDoc : tmpEminlist){
                float vol = Float.parseFloat(subDoc.getString("vehBMSCellVolt"));
                if (vol < vehBMSCellVoltMin) {
                    vehBMSCellVoltMin = vol;
                }
            }
        }
        data.setLowVoltage(vehBMSCellVoltMin*0.001f);
        /*IF电池单体电压最低值=65.535
        THEN最低电压电池单体代号=0xFF
        ELSE 最低电压电池单体代号=Index{电池单体电压最低值 of [vehBMSCellVolt(1) ,…, vehBMSCellVolt(单体电池总数)]}*/
        float minVoltage = data.getLowVoltage();
        byte indexVolMin= 0;
        if(minVoltage == 65.535){
            data.setLowBatteryCode((byte)0xFF);
        }else{
            List<Document> tmplistmin = (List<Document>)bsonDoc.get("vehBMSCellVolt", List.class);
            if(tmplistmin != null){
                for (byte i = 0; i < tmplistmin.size(); i++) {
                    Document subDoc = tmplistmin.get(i);
                    if (minVoltage ==  Float.parseFloat(subDoc.getString("vehBMSCellVolt"))) {
                        indexVolMin = i ;
                        break;
                    }
                }
            }
        }
        data.setLowBatteryCode(indexVolMin);
        //最高温度子系统号
        data.setHighTemperatureId((byte) 0x1);
        //最高温度值=max[vehBMSCellTem(1) ,…, vehBMSCellTem(可充电储能温度探针个数)]
        List<Document> tmpTemperaturelist = (List<Document>)bsonDoc.get("vehBMSCellTem", List.class);
        byte vehBMSCellTemMax = 0;
        if(tmpTemperaturelist != null){
            for(Document subDoc : tmpTemperaturelist){
                byte vol = Byte.parseByte(subDoc.getString("vehBMSCellTem"));
                if (vol > vehBMSCellTemMax) {
                    vehBMSCellTemMax = vol;
                }
            }
        }
        data.setHighTemperature((byte) (vehBMSCellTemMax-40));
        /*IF最高温度值=215
        THEN最高温度探针序号=0xFF
        ELSE 最高温度探针序号=Index{最高温度值 of [vehBMSCellTem(1) ,…, vehBMSCellTem(可充电储能温度探针个数)]}*/
        byte highTemperatureTmp = data.getHighTemperature();
        byte indexTemperature= 0;
        if((highTemperatureTmp & 0xff) == 215){
            data.setHighProbeCode((byte)0xFF);
        }else{
            List<Document> tmplistHighTem = (List<Document>)bsonDoc.get("vehBMSCellTem", List.class);
            if(tmplistHighTem != null){
                for (byte i = 0; i < tmplistHighTem.size(); i++) {
                    Document subDoc = tmplistHighTem.get(i);
                    if (highTemperatureTmp ==  Byte.parseByte(subDoc.getString("vehBMSCellTem"))) {
                        indexTemperature = i ;
                        break;
                    }
                }
            }
        }
        data.setHighProbeCode((byte) (indexTemperature+1));
        //最低温度子系统号
        data.setLowTemperatureId((byte) 0x1);

        /*最低温度值=min[vehBMSCellTem(1) ,…, vehBMSCellTem(可充电储能温度探针个数)]*/
        List<Document> tmplistLowTem = (List<Document>)bsonDoc.get("vehBMSCellTem", List.class);
        byte vehBMSCellTemMin = Byte.MAX_VALUE;
        if(tmplistLowTem != null){
            for(Document subDoc : tmplistLowTem){
                byte vol = Byte.parseByte(subDoc.getString("vehBMSCellTem"));
                if (vol < vehBMSCellTemMin) {
                    vehBMSCellTemMin = vol;
                }
            }
        }

        data.setLowTemperature((byte) (vehBMSCellTemMin - 40));
        /*  IF最低温度值=215
        THEN最低温度探针序号=0xFF
        ELSE 最低温度探针序号=Index{最低温度值 of [vehBMSCellTem(1) ,…, vehBMSCellTem(可充电储能温度探针个数)]}*/
        List<Document> tmplistLowTem1 = (List<Document>)bsonDoc.get("vehBMSCellTem", List.class);
        byte lowTemperatureTmp = data.getLowTemperature();
        byte indexTemperatureMin= 0;
        if((lowTemperatureTmp & 0xff) == 215){
            data.setLowProbeCode((byte)0xFF);
        }else{
            for (byte i = 0; i < tmplistLowTem1.size(); i++) {
                Document subDoc = tmplistLowTem1.get(i);
                if (lowTemperatureTmp ==  Byte.parseByte(subDoc.getString("vehBMSCellTem"))) {
                    indexTemperatureMin = i ;
                    break;
                }
            }
        }
        data.setLowProbeCode((byte) (indexTemperatureMin+1));

        return data;
    }

}
