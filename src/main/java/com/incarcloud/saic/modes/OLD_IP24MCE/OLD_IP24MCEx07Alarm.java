package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX07Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * @Author GuoKun
 * @Despriction: 07Alarm
 * @Date Created in 2018/9/6 10:08
 */
public class OLD_IP24MCEx07Alarm extends OracleX implements IOracleX07Alarm {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24MCEx07Alarm.class);

    @Override
    public GBx07Alarm makeGBx07Alarm(ResultSet rs) {
        GBx07Alarm data = null;
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            data = new GBx07Alarm(vin, tmGMT8);

            float BMSCellTempMaxHSC2 = Float.parseFloat(rs.getString("BMSCellTempMaxHSC2"));
            float BMSCellTempMinHSC2 = Float.parseFloat(rs.getString("BMSCellTempMinHSC2"));
            float BMSCellTempMax = Float.parseFloat(rs.getString("BMSCellTempMax"));
            float BMSCellTempMin = Float.parseFloat(rs.getString("BMSCellTempMin"));
            float BMSPackVoltHSC2 = Float.parseFloat(rs.getString("BMSPackVoltHSC2"));
            float BMSPackSOCHSC2 = Float.parseFloat(rs.getString("BMSPackSOCHSC2"));
            float BMSPTIsolationHSC2 = Float.parseFloat(rs.getString("BMSPTIsolationHSC2"));
            float BrakeVacuumPumpInfoMSC = Float.parseFloat(rs.getString("BrakeVacuumPumpInfoMSC"));
            float DCStateHSC2 = Float.parseFloat(rs.getString("DCStateHSC2"));
            float TMInvtTempHSC2 = Float.parseFloat(rs.getString("TMInvtTempHSC2"));
            float ISGInvtTempHSC2 = Float.parseFloat(rs.getString("ISGInvtTempHSC2"));
            float BMSHvilStatHSC2 = Float.parseFloat(rs.getString("BMSHvilStatHSC2"));
            float TMTempHSC2 = Float.parseFloat(rs.getString("TMTempHSC2"));
            float ISGTempHSC2 = Float.parseFloat(rs.getString("ISGTempHSC2"));


            data.setTempPlusHigherl(calcTempPlusHigherl(BMSCellTempMaxHSC2, BMSCellTempMinHSC2, BMSCellTempMax, BMSCellTempMin));
            data.setTempratureHighestl(calcTempratureHighestl(BMSCellTempMaxHSC2));
            data.setTotalVolHighestl(calcTotalVolHighestl(BMSPackVoltHSC2));
            data.setTotalVolLowestl(calcTotalVolLowestl(BMSPackVoltHSC2));
            data.setSocLowerl(calcSocLowerl(BMSPackSOCHSC2));
            data.setSellVolHighestL(calcSellVolHighestL());
            data.setSellVolLowestl(calcSellVolLowestl());
            data.setSocHigherAlarm(calcSocHigherAlarm(BMSPackSOCHSC2));
            data.setSocJumpAlarm(calcSocJumpAlarm(BMSPackSOCHSC2));
            data.setBatterySysDismatch(calcBatterySysDismatch());
            data.setVolPlusBiggerl(calcVolPlusBiggerl());
            data.setInsuLowl(calcInsuLowl(BMSPTIsolationHSC2));
            data.setDcdcTempAlarm(calcDcdcTempAlarm());
            data.setIcuBrakeSysErr(calcIcuBrakeSysErr(BrakeVacuumPumpInfoMSC));
            data.setDcdcStatusAlarm(calcDcdcStatusAlarm(DCStateHSC2));
            data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(TMInvtTempHSC2,  ISGInvtTempHSC2));
            data.setIsLockHigh(calcIsLockHigh(BMSHvilStatHSC2));
            data.setIsMotorTempHigh(calcIsMotorTempHigh(TMTempHSC2, ISGTempHSC2));
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
    private static byte calcTempPlusHigherl(float BMSCellTempMaxHSC2, float BMSCellTempMinHSC2, float BMSCellTempMax, float BMSCellTempMin) {
        /*
        IF BMSCellTempMaxHSC2<87 && BMSCellTempMinHSC2<87 (BMSCellTempMax-BMSCellTempMin)>=20
        THEN Bit0=1
        ELSE Bit0=0
         *
         * 连续2S满足才报警
         */
        if (BMSCellTempMaxHSC2 < 87 && BMSCellTempMinHSC2 < 87) {
            return (byte) ((BMSCellTempMax - BMSCellTempMin) >= 20.0f ? 0x01 : 0x00);
        }
        return 0x00;
    }

    // 电池高温报警
    private static byte calcTempratureHighestl(float BMSCellTempMaxHSC2) {
        /*
        IF BMSCellTempMaxHSC2<87 && BMSCellTempMaxHSC2>=70
        THEN Bit1=1
        ELSE Bit1=0
        连续2S满足才报警
        */
        if (BMSCellTempMaxHSC2 < 87 && BMSCellTempMaxHSC2 >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过压报警
    private static byte calcTotalVolHighestl(float BMSPackVoltHSC2) {
        /*
        IF BMSPackVoltHSC2<1023.5 && BMSPackVoltHSC2>=340.5
        THEN Bit2=1
        ELSE Bit2=0
        连续2S满足才报警
        */
        if (BMSPackVoltHSC2 < 1023.5 && BMSPackVoltHSC2 >= 340.5) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型欠压报警
    private static byte calcTotalVolLowestl(float BMSPackVoltHSC2) {
        /*
        IF BMSPackVoltHSC2<1023.5 && BMSPackVoltHSC2<=193
        THEN Bit3=1
        ELSE Bit3=0
        连续2S满足才报警
        */
        if (BMSPackVoltHSC2 < 1023.5 && BMSPackVoltHSC2 <= 193) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC低报警
    private static byte calcSocLowerl(float BMSPackSOCHSC2) {
        /*
        IF BMSPackSOCHSC2<102.2 && BMSPackSOCHSC2<=0
        THEN Bit4=1
        ELSE Bit4=0
        N=0 %
        连续2S满足才报警
        */
        if (BMSPackSOCHSC2 < 102.2 && BMSPackSOCHSC2 <= 0) {
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
    private static byte calcSocHigherAlarm(float BMSPackSOCHSC2) {
        /*
        IF BMSPackSOCHSC2<102.2 && BMSPackSOCHSC2>100
        THEN Bit7=1
        ELSE Bit7=0
        */
        if (BMSPackSOCHSC2 < 102.2 && BMSPackSOCHSC2 > 100) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC跳变报警
    private static byte calcSocJumpAlarm(float BMSPackSOCHSC2) {
        /*
        IF  BMSPackSOCHSC2<102.2  && (|BMSPackSOCHSC2(n+1)- BMSPackSOCHSC2(n )|)>=15
        THEN Bit8=1
        ELSE Bit8=0
        */
        if (BMSPackSOCHSC2 < 102.2 && Math.abs(BMSPackSOCHSC2 * (2 + 1) - BMSPackSOCHSC2 * (2)) >= 15) {
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
    private static byte calcInsuLowl(float BMSPTIsolationHSC2) {
        /*
        IF BMSPTIsolationHSC2<=250
        THEN Bit11=1
        ELSE Bit11=0
        连续2S满足才报警
        */
        if (BMSPTIsolationHSC2 <= 250) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC温度报警
    private static byte calcDcdcTempAlarm() {
        return 0x00;
    }

    // 制动系统报警
    private static byte calcIcuBrakeSysErr(float BrakeVacuumPumpInfoMSC) {
        /*
        IF BrakeVacuumPumpInfoMSC=1
        THEN Bit13=0x1
        ELSE Bit13=0x0

        制动液位低/EBD故障/ABS故障/VSE故障/iBoost故障
        连续2S满足才报警
        */
        if (BrakeVacuumPumpInfoMSC == 1) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC状态报警
    private static byte calcDcdcStatusAlarm(float DCStateHSC2) {
        /*
        IF DCStateHSC2=0x5
        THEN Bit14=1
        ELSE Bit14=0
        */
        if (DCStateHSC2 == 5) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机控制器温度报警
    private static byte calcIsMotorControlerTempHigh(float TMInvtTempHSC2, float ISGInvtTempHSC2) {
        /*
        IF TMInvtTempHSC2>=142 || ISGInvtTempHSC2>=142
        THEN Bit15=1
        ELSE Bit15=0
        连续2S满足才报警
        */
        if (TMInvtTempHSC2 >= 142 || ISGInvtTempHSC2 >= 142) {
            return 0x01;
        }
        return 0x00;
    }

    // 高压互锁状态报警
    private static byte calcIsLockHigh(float BMSHvilStatHSC2) {
        /*
        IF BMSHvilStatHSC2=0x0
        THEN Bit16=1
        ELSE Bit16=0
        */
        if (BMSHvilStatHSC2 == 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机温度报警
    private static byte calcIsMotorTempHigh(float TMTempHSC2, float ISGTempHSC2) {
        /*
        IF TMTempHSC2>=186 || ISGTempHSC2>=186
        THEN Bit17=1
        ELSE Bit17=0
        连续2S满足才报警
        */
        if (TMTempHSC2 >= 186 || ISGTempHSC2 >= 186) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过充
    private static byte calcSellVolHighestChargerl() {
        return 0x00;
    }

}
