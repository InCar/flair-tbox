package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX01Overview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * IP34和AS24标准一模一样
 * @author xy
 */
public class OLD_IP24MCEx01Overview extends OracleX implements IOracleX01Overview {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24MCEx01Overview.class);

    public GBx01Overview makeGBx01Overview(ResultSet rs){

        GBx01Overview data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int vehEPTRdy = rs.getInt("vehEPTRdy");
            int vehSysPwrMod = rs.getInt("vehSysPwrMod");

            data = new GBx01Overview(vin, tmGMT8);
            // TODO: ...
            data.setVehicleStatus(calcVehicleStatus(vehEPTRdy, vehSysPwrMod));

        }
        catch (SQLException ex){
            s_logger.error("OLD_IP24MCEx01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }

    private static byte calcVehicleStatus(int vehEPTRdy, int vehSysPwrMod){
        // TODO: 实现转换算法
        return 0x00;
    }

}
