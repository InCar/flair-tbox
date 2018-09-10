package com.incarcloud.saic.modes.OLD_IP24;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.OLD_IP24MCE.OLD_IP24MCEx07Alarm;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX07Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * @Author GuoKun
 * @Despriction:
 * @Date Created in 2018/9/6 14:24
 */
public class OLD_IP24x07Alarm extends OracleX implements IOracleX07Alarm {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24x07Alarm.class);

    @Override
    public GBx07Alarm makeGBx07Alarm(ResultSet rs) {
        GBx07Alarm data = null;
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            data = new GBx07Alarm(vin, tmGMT8);

            float BMSPackVolt = Float.parseFloat(rs.getString("BMSPackVolt"));
            float BMSPackSOC = Float.parseFloat(rs.getString("BMSPackSOC"));
            float BMSCellTempMax = Float.parseFloat(rs.getString("BMSCellTempMax"));
            float BMSCellTempMin = Float.parseFloat(rs.getString("BMSCellTempMin"));
            float BMSPTIsolation = Float.parseFloat(rs.getString("BMSPTIsolation"));
            float BrakeVacuumPumpInfo = Float.parseFloat(rs.getString("BrakeVacuumPumpInfo"));
            float DCState = Float.parseFloat(rs.getString("DCState"));
            float TMInvtTemp = Float.parseFloat(rs.getString("TMInvtTemp"));
            float ISGInvtTemp = Float.parseFloat(rs.getString("ISGInvtTemp"));
            float BMSHvilStat = Float.parseFloat(rs.getString("BMSHvilStat"));
            float TMTemp = Float.parseFloat(rs.getString("TMTemp"));
            float ISGTemp = Float.parseFloat(rs.getString("ISGTemp"));


            data.setTempPlusHigherl(calcTempPlusHigherl(BMSCellTempMax,  BMSCellTempMin));
            data.setTempratureHighestl(calcTempratureHighestl(BMSCellTempMax));
            data.setTotalVolHighestl(calcTotalVolHighestl(BMSPackVolt));
            data.setTotalVolLowestl(calcTotalVolLowestl(BMSPackVolt));
            data.setSocLowerl(calcSocLowerl(BMSPackSOC));
            data.setSellVolHighestL(calcSellVolHighestL());
            data.setSellVolLowestl(calcSellVolLowestl());
            data.setSocHigherAlarm(calcSocHigherAlarm(BMSPackSOC));
            data.setSocJumpAlarm(calcSocJumpAlarm(BMSPackSOC));
            data.setBatterySysDismatch(calcBatterySysDismatch());
            data.setVolPlusBiggerl(calcVolPlusBiggerl());
            data.setInsuLowl(calcInsuLowl(BMSPTIsolation));
            data.setDcdcTempAlarm(calcDcdcTempAlarm());
            data.setIcuBrakeSysErr(calcIcuBrakeSysErr(BrakeVacuumPumpInfo));
            data.setDcdcStatusAlarm(calcDcdcStatusAlarm(DCState));
            data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(TMInvtTemp,  ISGInvtTemp));
            data.setIsLockHigh(calcIsLockHigh(BMSHvilStat));
            data.setIsMotorTempHigh(calcIsMotorTempHigh(TMTemp,  ISGTemp));
            data.setSellVolHighestChargerl(calcSellVolHighestChargerl());

            short def = 0;
            data.setMaxLevel(def);
            data.setDeviceFaultCount(def);
            data.setDeviceFaultCodeList(new ArrayList<>());
            data.setMotorFaultCount(def);
            data.setMotorFaultCodeList(new ArrayList<>());
            data.setEngineFaultCount(def);
            data.setEngineFaultCodeList(new ArrayList<>());
            data.setOtherFaultCount(def);
            data.setOtherFaultCodeList(new ArrayList<>());


        } catch (Exception e) {
            s_logger.error("OLD_IP24MCEx07Alarm.makeGBx07overview() failed, {}", Helper.printStackTrace(e));
        }
        return data;
    }


    // 温度差异报警
    private static byte calcTempPlusHigherl( float BMSCellTempMax, float BMSCellTempMin) {
        /*
        IF BMSCellTempMax<87 && BMSCellTempMin<87 (BMSCellTempMax-BMSCellTempMin)>=20
        THEN Bit0=1
        ELSE Bit0=0
         *
         * 连续2S满足才报警
         */
        if ( BMSCellTempMax < 87 && BMSCellTempMin < 87) {
            return (byte) ((BMSCellTempMax - BMSCellTempMin) >= 20.0f ? 0x01 : 0x00);
        }
        return 0x00;
    }

    // 电池高温报警
    private static byte calcTempratureHighestl(float BMSCellTempMax) {
        /*
        IF BMSCellTempMax<87 && BMSCellTempMax>=70
        THEN Bit1=1
        ELSE Bit1=0
        连续2S满足才报警
        */
        if ( BMSCellTempMax < 87 && BMSCellTempMax >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过压报警
    private static byte calcTotalVolHighestl(float BMSPackVolt) {
        /*
        IF BMSPackVolt<1023.5 && BMSPackVolt>=340.5
        THEN Bit2=1
        ELSE Bit2=0
        连续2S满足才报警
        */
        if (BMSPackVolt < 1023.5 && BMSPackVolt >= 340.5) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型欠压报警
    private static byte calcTotalVolLowestl(float BMSPackVolt) {
        /*
        IF BMSPackVolt<1023.5 && BMSPackVolt<=193
        THEN Bit3=1
        ELSE Bit3=0
        连续2S满足才报警
        */
        if (BMSPackVolt < 1023.5 && BMSPackVolt <= 193) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC低报警
    private static byte calcSocLowerl(float BMSPackSOC) {
        /*
        IF BMSPackSOC<102.2 && BMSPackSOC<=0
        THEN Bit4=1
        ELSE Bit4=0
        N=0 %
        连续2S满足才报警
        */
        if (BMSPackSOC < 102.2 && BMSPackSOC <= 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 单体电池过压报警
    private static byte calcSellVolHighestL() {
        return 0x00;
    }

    // 单体电池欠压报警
    private static byte calcSellVolLowestl() {
        return 0x00;
    }


    // SOC过高报警
    private static byte calcSocHigherAlarm(float BMSPackSOC) {
        /*
        IF BMSPackSOC<102.2 && BMSPackSOC>100
        THEN Bit7=1
        ELSE Bit7=0
        */
        if ( BMSPackSOC < 102.2 && BMSPackSOC > 100) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC跳变报警
    private static byte calcSocJumpAlarm(float BMSPackSOC) {
        /*
        IF  BMSPackSOC<102.2  && (|BMSPackSOC(n+1)- BMSPackSOC(n )|)>=15
        THEN Bit8=1
        ELSE Bit8=0
        */
        if (BMSPackSOC < 102.2 && Math.abs(BMSPackSOC * (2 + 1) - BMSPackSOC * (2)) >= 15) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能系统不匹配报警
    private static byte calcBatterySysDismatch() {
        return 0x00;
    }

    // 电池单体一致性差报警
    private static byte calcVolPlusBiggerl() {
        return 0x00;
    }

    // 绝缘报警
    private static byte calcInsuLowl(float BMSPTIsolation) {
        /*
        IF BMSPTIsolation<=250
        THEN Bit11=1
        ELSE Bit11=0
        连续2S满足才报警
        */
        if (BMSPTIsolation <= 250) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC温度报警
    private static byte calcDcdcTempAlarm() {
        return 0x00;
    }

    // 制动系统报警
    private static byte calcIcuBrakeSysErr(float BrakeVacuumPumpInfo) {
        /*
        IF BrakeVacuumPumpInfo=1
        THEN Bit13=0x1
        ELSE Bit13=0x0

        制动液位低/EBD故障/ABS故障/VSE故障/iBoost故障
        连续2S满足才报警
        */
        if (BrakeVacuumPumpInfo == 1) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC状态报警
    private static byte calcDcdcStatusAlarm(float DCState) {
        /*
        IF DCState=0x5
        THEN Bit14=1
        ELSE Bit14=0
        */
        if (DCState == 5) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机控制器温度报警
    private static byte calcIsMotorControlerTempHigh(float TMInvtTemp, float ISGInvtTemp) {
        /*
        IF TMInvtTemp>=142 || ISGInvtTemp>=142
        THEN Bit15=1
        ELSE Bit15=0
        连续2S满足才报警
        */
        if (TMInvtTemp >= 142 || ISGInvtTemp >= 142) {
            return 0x01;
        }
        return 0x00;
    }

    // 高压互锁状态报警
    private static byte calcIsLockHigh(float BMSHvilStat) {
        /*
        IF BMSHvilStat=0x0
        THEN Bit16=1
        ELSE Bit16=0
        */
        if (BMSHvilStat == 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机温度报警
    private static byte calcIsMotorTempHigh(float TMTemp, float ISGTemp) {
        /*
        IF TMTemp>=186 ||        ISGTemp>=186
        THEN Bit17=1
        ELSE Bit17=0
        连续2S满足才报警
        */
        if (TMTemp >= 186 || ISGTemp >= 186) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过充
    private static byte calcSellVolHighestChargerl() {
        return 0x00;
    }

}
