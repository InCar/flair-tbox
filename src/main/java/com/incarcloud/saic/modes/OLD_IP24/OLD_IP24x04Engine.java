package com.incarcloud.saic.modes.OLD_IP24;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx04Engine;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX04Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 *
 * @author ty
 */
public class OLD_IP24x04Engine extends OracleX implements IOracleX04Engine {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24x04Engine.class);

    public GBx04Engine makeGBx04Engine(ResultSet rs){

        GBx04Engine data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int ISGSpeed = rs.getInt("ISGSpeed");
            float AvgFuelConsumption = rs.getInt("AvgFuelConsumption");

            data = new GBx04Engine(vin, tmGMT8);
            // TODO: ...
            data.setStatus(calcEngineStatus(ISGSpeed));
            data.setSpeed(calcAxleSpeed(ISGSpeed));
            data.setRate(calcOilConsumptionRate(AvgFuelConsumption));

        }
        catch (SQLException ex){
            s_logger.error("OLD_IP24x04Engine.makeGBx04Engine() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }

    private static short calcEngineStatus(int ISGSpeed){
        /*
        IF ISGSpeed=65534 || 65535
        THEN 发动机状态=0xFF
        ELSE IF ISGSpeed>100
        THEN 发动机状态=0x01
        ELSE 发动机状态=0x02
        */
        short engineStatus;
        if(ISGSpeed == 65534 || ISGSpeed == 65535)
            engineStatus = 0xFF;
        else if(ISGSpeed > 100)
            engineStatus = 0x01;
        else engineStatus = 0x02;
        return engineStatus;
    }

    private static int calcAxleSpeed(int ISGSpeed){
        /*
        IF ISGSpeed=65534 || 65535
        THEN 曲轴转速=0xFF,0xFF
        ELSE IF ISGSpeed>100
        THEN 曲轴转速=ISGSpeed
        ELSE 曲轴转速=0x00
         */
        int axleSpeed;
        if(ISGSpeed == 65534 || ISGSpeed == 65535)
            axleSpeed = 0xFFFF;
        else if(ISGSpeed > 100)
            axleSpeed = ISGSpeed;
        else axleSpeed = 0x01;

        return axleSpeed;
    }

    private static float calcOilConsumptionRate(float AvgFuelConsumption){
        /*
        燃料消耗率=AvgFuelConsumption
         */
        float oilConsumptionRate = AvgFuelConsumption;

        return oilConsumptionRate;
    }
}
