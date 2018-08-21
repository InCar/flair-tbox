package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.GBx05Position;
import com.incarcloud.saic.modes.mongo.IMongoX05Position;
import org.bson.Document;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class MongoX05Position extends MongoX implements IMongoX05Position {
    @Override
    public GBx05Position makeGBx05Position(Document bsonDoc) {

        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        int gpsStatus = Integer.parseInt(bsonDoc.getString("gpsStatus"));
        double gnssLat = Double.parseDouble(bsonDoc.getString("gnssLat"));
        double gnssLong = Double.parseDouble(bsonDoc.getString("gnssLong"));

        GBx05Position gBx05Position = new GBx05Position(vin, tmGMT8);
        gBx05Position.setPositionStatus(calcGpsStatus(gpsStatus));
        gBx05Position.setLatitude(calcGnssLong(gnssLat));
        gBx05Position.setLongitude(calcGnssLat(gnssLong));
        return gBx05Position;
    }

    private static byte calcGpsStatus(int gpsStatus){
        /*IF gpsStatus=0
        THEN 定位状态=1
        ELSE 定位状态=0*/
        byte GS;
        if (gpsStatus == 0) GS = 1;
        else GS = 0;
        return GS;
    }

    private static Double calcGnssLat(double gnssLat){
        //经度,精确到百万分之一度。
        return gnssLat;
    }

    private static Double calcGnssLong(double gnssLong){
        //纬度,精确到百万分之一度。
        return gnssLong;
    }


    /**
     * 提供精确的乘法运算。
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    private static Double mul(Double value1, Double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.multiply(b2).doubleValue();
    }
}

