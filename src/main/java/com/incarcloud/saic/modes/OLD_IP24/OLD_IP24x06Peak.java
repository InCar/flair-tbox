package com.incarcloud.saic.modes.OLD_IP24;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.GB32960.GBx06Peak;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX06Peak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * @author wanghan
 * @date 2018/8/25 10:34
 */
public class OLD_IP24x06Peak extends OracleX implements IOracleX06Peak {
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
        /*  IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVolt/92+Random(min=0,max=0.2)*/
        float BMSPackVolt = Float.parseFloat(rs.getString("BMSPackVolt"));
        data.setHighVoltage(singleBatteryVoltage(BMSPackVolt));
        //最低电压电池子系统号=0x1
        data.setLowBatteryId((short) 0x1);
        //最 低 电 压 电 池 单 体 代 号 = BMSCellMinVolIndx
        short BMSCellMinVolIndx = Short.parseShort(rs.getString("BMSCellMinVolIndx"));
        data.setLowBatteryCode(BMSCellMinVolIndx);
        /*IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVolt/92-Random(min=0.1,max=0.2)*/
        float BMSPackVoltmin = Float.parseFloat(rs.getString("BMSPackVolt"));
        data.setLowVoltage(minimumVoltageOfsingle(BMSPackVoltmin));
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
        /* IF BMSCellTempMin=87 || 87.5
        THEN 最低温度值=0xFF
        ELSE 最 低 温 度 值 =
        BMSCellTempMin*/
        short BMSCellTempMin = Short.parseShort(rs.getString("BMSCellTempMin"));
        data.setLowTemperature(mimTemp(BMSCellTempMin));
        } catch (SQLException ex) {
            s_logger.error("OLD_IP24x01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }
        return data;
    }

    private static float singleBatteryVoltage(float BMSPackVolt){
        /*  IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最 高 值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最 高 值=BMSPackVolt/92+Random(min=0,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/92+Math.random()/5);
        return singleBattery;
    }

    private static float minimumVoltageOfsingle(float BMSPackVolt){
        /*IF BMSPackVolt=1023.5 || 1023.75
        THEN 电 池 单 体 电 压 最低值=0xFF,0xFF
        ELSE 电 池 单 体 电 压 最低 值=BMSPackVolt/92-Random(min=0.1,max=0.2)*/
        float singleBattery;
        if(BMSPackVolt == 1023.5 ||BMSPackVolt == 1023.75) singleBattery = 0xFFFF;
        else singleBattery = (float) (BMSPackVolt/92+((Math.random()/10)+0.1));
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

    private static short mimTemp(float BMSCellTempMin){
        /* IF BMSCellTempMin=87 || 87.5
        THEN 最低温度值=0xFF
        ELSE 最 低 温 度 值 =
        BMSCellTempMin*/
        short minTemperature = 0;
        if(BMSCellTempMin == 87||BMSCellTempMin == 87.5) minTemperature = 0xFF;
        else minTemperature = (short) BMSCellTempMin;
        return minTemperature;
    }


}
