package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.OLD_IP24.OLD_IP24x06Peak;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX06Peak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/24 16:06
 */
public class OLD_IP24MCEx06Peak extends OracleX implements IOracleX06Peak {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24x06Peak.class);

    @Override
    public GBx06Peak makeGBx06Peak(ResultSet rs) {
        GBx06Peak data = null;
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            data = new GBx06Peak(vin, tmGMT8);

        //最高电压电池子系统号=0x01
        data.setHighBatteryId((short) 0x01);
        //最 高 电 压 电 池 单 体 代 号 = BMSCellMaxVolIndx
        short BMSCellMaxVolIndx = Short.parseShort(rs.getString("BMSCellMaxVolIndx"));
        data.setHighBatteryCode(BMSCellMaxVolIndx);
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最 高 值=BMSPackVoltHSC2/92+Random(min=0,max=0.2)*/
        float BMSPackVoltHSC2 = Float.parseFloat(rs.getString("BMSPackVoltHSC2"));
        data.setHighVoltage(singleBatteryVoltage(BMSPackVoltHSC2));
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 低 电 压 电 池 单 体 代 号 = BMSCellMinVolIndx
        short BMSCellMinVolIndx = Short.parseShort(rs.getString("BMSCellMinVolIndx"));
        data.setLowBatteryCode(BMSCellMinVolIndx);
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
            THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
            ELSE 电 池 单 体 电 压 最低 值=BMSPackVoltHSC2/92-Random(min=0.1,max=0.2)*/
        float BMSPackVoltHSC2Min = Float.parseFloat(rs.getString("BMSPackVoltHSC2"));
        data.setLowVoltage(minimumVoltageOfsingle(BMSPackVoltHSC2Min));
        //最高温度子系统号=0x1
        data.setHighTemperatureId((short) 0x1);
        //最 高 温 度 探 针 序 号 = BMSCellMaxTemIndx
        short BMSCellMaxTemIndx = Short.parseShort(rs.getString("BMSCellMaxTemIndx"));
        data.setHighProbeCode(BMSCellMaxTemIndx);
        /*IF BMSCellTempMax=87 || 87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellTempMax*/
        float BMSCellTempMax = Float.parseFloat(rs.getString("BMSCellTempMax"));
        data.setHighTemperature(maxTemp(BMSCellTempMax));
        //最低温度子系统号=0x1
        data.setLowTemperatureId((short) 0x1);
        //最 低 温 度 探 针 序 号 = BMSCellMinTemIndx
        short BMSCellMinTemIndx = Short.parseShort(rs.getString("BMSCellMinTemIndx"));
        data.setLowProbeCode(BMSCellMinTemIndx);
        /* IF BMSCellTempMinHSC2=87 || 87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellTempMinHSC2*/
        short BMSCellTempMinHSC2 = Short.parseShort(rs.getString("BMSCellTempMinHSC2"));
        data.setLowTemperature(mimTemp(BMSCellTempMinHSC2));
        } catch (SQLException ex) {
            s_logger.error("OLD_IP24x01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }
        return data;
    }

    private static float singleBatteryVoltage(float BMSPackVoltHSC2){
        /*  IF BMSPackVoltHSC2=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVoltHSC2/92+Random(min=0,max=0.2)*/
        float singleBattery;
        if(BMSPackVoltHSC2 == 1023.5 ||BMSPackVoltHSC2 == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVoltHSC2/92+Math.random()/5);
        return singleBattery;
    }

    private static float minimumVoltageOfsingle(float BMSPackVoltHSC2){
        /*IF BMSPackVoltHSC2=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVoltHSC2/92-Random(min=0.1,max=0.2)*/
        float singleBattery;
        if(BMSPackVoltHSC2 == 1023.5 ||BMSPackVoltHSC2 == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVoltHSC2/92+((Math.random()/10)+0.1));
        return singleBattery;
    }

    private static short maxTemp(float BMSCellTempMax){
        /*IF BMSCellTempMax=87 || 87.5
        THEN 最高温度值=0xFF
        ELSE 最 高 温 度 值 =
        BMSCellTempMax*/
        short maxTemperature = 0;
        if(BMSCellTempMax == 87||BMSCellTempMax == 87.5) maxTemperature = 0xFF;
        else maxTemperature = (short) BMSCellTempMax;
        return maxTemperature;
    }

    private static short mimTemp(float BMSCellTempMinHSC2){
        /* IF BMSCellTempMinHSC2=87 || 87.5
            THEN 最低温度值=0xFF
            ELSE 最 低 温 度 值 =
            BMSCellTempMinHSC2*/
        short minTemperature = 0;
        if(BMSCellTempMinHSC2 == 87||BMSCellTempMinHSC2 == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) BMSCellTempMinHSC2;
        return minTemperature;
    }
}
