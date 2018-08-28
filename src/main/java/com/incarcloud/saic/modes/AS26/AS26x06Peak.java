package com.incarcloud.saic.modes.AS26;

import com.alibaba.fastjson.JSONArray;
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

        @SuppressWarnings("unchecked")
        /*电池单体电压最高值=max[vehBMSCellVolt(1) ,…, vehBMSCellVolt(单体电池总数)]*/
        JSONArray voltageList = JSONArray.parseArray(bsonDoc.getString("vehBMSCellVolt"));
        float vehBMSCellMaxVol = 0f;//最高单体电压值
        int indexVehBMSCellMaxVol = 0;//最高单体电压序号

        float vehBMSCellMinVol = 0f;//最低单体电压值
        int indexVehBMSCellMinVol = 0;//最低单体电压序号
        if(voltageList != null){
            for(int i = 0;i < voltageList.size();i ++){
                float vol = voltageList.getFloat(i);
                if(i == 0){
                    vehBMSCellMaxVol = vol;
                    vehBMSCellMinVol = vol;
                    indexVehBMSCellMaxVol = 0;
                    indexVehBMSCellMinVol = 0;
                }else{
                    if (vol > vehBMSCellMaxVol) {
                        vehBMSCellMaxVol = vol;
                        indexVehBMSCellMaxVol = i;
                    }
                    if (vol < vehBMSCellMinVol) {
                        vehBMSCellMinVol = vol;
                        indexVehBMSCellMinVol = i;
                    }
                }
            }
        }
        //最高电压电池子系统号=0x01
        data.setHighBatteryId((short) 0x01);
        data.setHighVoltage(vehBMSCellMaxVol);
        data.setHighBatteryCode((short) (indexVehBMSCellMaxVol + 1));
        //最低电压电池子系统号
        data.setLowBatteryId((short) 0x1);
        data.setLowVoltage(vehBMSCellMinVol);
        data.setLowBatteryCode((short) (indexVehBMSCellMinVol + 1));

        //最高温度值=max[vehBMSCellTem(1) ,…, vehBMSCellTem(可充电储能温度探针个数)]
        @SuppressWarnings("unchecked")
//        List<Document> tmpTemperaturelist = (List<Document>)bsonDoc.get("vehBMSCellTem", List.class);
        JSONArray tempList = JSONArray.parseArray(bsonDoc.getString("vehBMSCellTem"));
        short vehBMSCellTemMax = 0;
        int indexVehBMSCellTemMax= 0;
        short vehBMSCellTemMin = 0;
        int indexVehBMSCellTemMin= 0;
        if(tempList != null){
            for(int i = 0;i < tempList.size();i ++){
                short temp = tempList.getShort(i);
                if(i == 0){
                    vehBMSCellTemMax = temp;
                    vehBMSCellTemMin = temp;
                    indexVehBMSCellTemMax = 0;
                    indexVehBMSCellTemMin = 0;
                }else{
                    if (temp > vehBMSCellTemMax) {
                        vehBMSCellTemMax = temp;
                        indexVehBMSCellTemMax = i;
                    }
                    if (temp < vehBMSCellTemMin) {
                        vehBMSCellTemMin = temp;
                        indexVehBMSCellTemMin = i;
                    }
                }
            }
        }
        //最高温度子系统号
        data.setHighTemperatureId((short) 0x1);
        data.setHighTemperature((vehBMSCellTemMax));
        data.setHighBatteryCode(((short) (indexVehBMSCellTemMax+1)));
        //最低温度子系统号
        data.setLowTemperatureId((short) 0x1);
        data.setLowTemperature((vehBMSCellTemMin));
        data.setLowProbeCode((short) (indexVehBMSCellTemMin+1));

        return data;
    }

}
