package com.incarcloud.saic.modes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OracleX {

    protected static final ZoneId s_zoneGMT8 = ZoneId.of("+8");

    protected String getVin(ResultSet rs) throws SQLException {
        return rs.getString("vin");
    }

    protected ZonedDateTime getZonedDateTimeGMT8(ResultSet rs) throws SQLException{
        String tboxTime = rs.getString("tboxTime");
        long tmEpoch = Long.parseLong(tboxTime);
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(tmEpoch), s_zoneGMT8);
    }
}
