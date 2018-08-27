package com.incarcloud.saic.modes.OLD_BP34;

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
public class OLD_BP34x04Engine extends OracleX implements IOracleX04Engine {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_BP34x04Engine.class);

    public GBx04Engine makeGBx04Engine(ResultSet rs){

        GBx04Engine data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int ISGSpd = rs.getInt("ISGSpd");
            float AvgFuelConsumption = rs.getInt("AvgFuelConsumption");

            data = new GBx04Engine(vin, tmGMT8);
            // TODO: ...
            data.setStatus(calcEngineStatus(ISGSpd));
            data.setSpeed(calcAxleSpeed(ISGSpd));
            data.setRate(calcOilConsumptionRate(AvgFuelConsumption));

        }
        catch (SQLException ex){
            s_logger.error("OLD_BP34x04Engine.makeGBx04Engine() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }

    private static short calcEngineStatus(int ISGSpd){
        /*
        IF ISGSpd=65534 || 65535
        THEN 发动机状态=0xFF
        ELSE IF ISGSpd>100
        THEN 发动机状态=0x01
        ELSE 发动机状态=0x02
        */
        short engineStatus;
        if(ISGSpd == 65534 || ISGSpd == 65535)
            engineStatus = 0xFF;
        else if(ISGSpd > 100)
            engineStatus = 0x01;
        else engineStatus = 0x02;
        return engineStatus;
    }

    private static int calcAxleSpeed(int ISGSpd){
        /*
        IF ISGSpd=65534 || 65535
        THEN 曲轴转速=0xFF,0xFF
        ELSE IF ISGSpd>100
        THEN 曲轴转速=ISGSpd
        ELSE 曲轴转速=0x00
         */
        int axleSpeed;
        if(ISGSpd == 65534 || ISGSpd == 65535)
            axleSpeed = 0xFFFF;
        else if(ISGSpd > 100)
            axleSpeed = ISGSpd;
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
