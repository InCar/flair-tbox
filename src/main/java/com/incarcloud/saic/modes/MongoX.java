package com.incarcloud.saic.modes;

import org.bson.Document;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MongoX {

    protected static final ZoneId s_zoneGMT8 = ZoneId.of("+8");

    protected String getVin(Document bsonDoc){
        return bsonDoc.getString("vin");
    }

    protected ZonedDateTime getZonedDateTimeGMT8(Document bsonDoc){
        String tboxTime = bsonDoc.getString("tboxTime");
        long tmEpoch = Long.parseLong(tboxTime);
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);
        // JSON数据处理：处理导入Mongodb的Json数据
//        try {
//            String tboxTime = bsonDoc.getString("gnsstime");
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            long tmEpoch = simpleDateFormat.parse(tboxTime).getTime();
//            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    protected static int parseIntWithDef(Document bsonDoc, String field, int def){
        String buf = bsonDoc.getString(field);
        return buf!=null? (int) Float.parseFloat(buf) :def;
    }

    protected static float parseFloatWithDef(Document bsonDoc, String field, float def){
        String buf = bsonDoc.getString(field);
        return buf!=null?Float.parseFloat(buf):def;
    }

    protected static int parseIntWithDef(Document bsonDoc, String field){
        return parseIntWithDef(bsonDoc, field, 0);
    }

    protected static float parseFloatWithDef(Document bsonDoc, String field){
       return parseFloatWithDef(bsonDoc, field, 0.0f);
    }
}
