package com.incarcloud.saic.modes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class OracleX {

    protected static final ZoneId s_zoneGMT8 = ZoneId.of("+8");
    protected static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected String getVin(ResultSet rs) throws SQLException {
        return rs.getString("vin");
    }

    protected ZonedDateTime getZonedDateTimeGMT8(ResultSet rs) throws SQLException{
        Date tboxTime = rs.getDate("tboxTime");
//        long tmEpoch = Long.parseLong(tboxTime);
        long tmEpoch = tboxTime.getTime();
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);
    }
}
