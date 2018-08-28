package com.incarcloud.saic.modes;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx05Position;
import com.incarcloud.saic.modes.oracle.IOracleX05Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 *
 * @author lgy
 */
public class OracleX05Position extends OracleX implements IOracleX05Position {
    private static final Logger s_logger = LoggerFactory.getLogger(OracleX05Position.class);
    @Override
    public GBx05Position makeGBx05Position(ResultSet rs) {

        GBx05Position gBx05Position = null;
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            int gpsStatus = rs.getInt("gpsStatus");
            double gnssLat = rs.getInt("gnssLat");
            double gnssLong = rs.getInt("gnssLong");

            gBx05Position = new GBx05Position(vin,tmGMT8);
            gBx05Position.setPositionStatus(calcGpsStatus(gpsStatus));
            gBx05Position.setLatitude(calcGnssLong(gnssLat));
            gBx05Position.setLongitude(calcGnssLat(gnssLong));
        }
        catch (SQLException ex) {
            s_logger.error("OracleX05Position.makeGBx05Position() failed, {}", Helper.printStackTrace(ex));
        }

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

