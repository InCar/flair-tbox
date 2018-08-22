package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX07Alarm;
import org.bson.Document;

import java.time.ZonedDateTime;

public class AS24x07Alarm extends MongoX implements IMongoX07Alarm {
    @Override
    public GBx07Alarm makeGBx07Alarm(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehBMSCellMaxTemV = Integer.parseInt(bsonDoc.getString("vehBMSCellMaxTemV"));
        int vehBMSCellMinTemV = Integer.parseInt(bsonDoc.getString("vehBMSCellMinTemV"));
        float vehBMSCellMaxTem = Float.parseFloat(bsonDoc.getString("vehBMSCellMaxTem"));
        float vehBMSCellMinTem = Float.parseFloat(bsonDoc.getString("vehBMSCellMinTem"));

        GBx07Alarm data = new GBx07Alarm(vin, tmGMT8);
        data.setTempPlusHigherl(calcTempPlusHigherl(vehBMSCellMinTemV, vehBMSCellMaxTemV, vehBMSCellMinTem, vehBMSCellMaxTem));

        return data;
    }


    private static byte calcTempPlusHigherl(int vehBMSCellMinTemV, int vehBMSCellMaxTemV,
                                            float vehBMSCellMinTem, float vehBMSCellMaxTem){
        /*
         * IF vehBMSCellMaxTemV=0 && vehBMSCellMinTemv=0 && (vehBMSCellMaxTem-vehBMSCellMinTem)>=N
         * THEN Bit0=1
         * ELSE Bit0=0
         *
         * N=20 ℃
         * 连续2S满足才报警
         */

        if(vehBMSCellMaxTemV==0 && vehBMSCellMinTemV==0)
            return (byte)(vehBMSCellMaxTem - vehBMSCellMinTem >= 20.0f?0x01:0x00);
        return 0x00;
    }
}
