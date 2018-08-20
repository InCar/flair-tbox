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
        long tmEpoch = bsonDoc.getLong("tboxTime");
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);
    }
}
