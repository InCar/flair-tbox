package com.incarcloud.saic.modes.OLD_BP34;

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
 * @Date Created in 2018/9/6 14:28
 */
public class OLD_BP34x07Alarm extends OracleX implements IOracleX07Alarm {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24MCEx07Alarm.class);

    @Override
    public GBx07Alarm makeGBx07Alarm(ResultSet rs) {
        GBx07Alarm data = null;
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            data = new GBx07Alarm(vin, tmGMT8);

            float BMSCellMaxTem = Float.parseFloat(rs.getString("BMSCellMaxTem"));
            float BMSCellMinTem = Float.parseFloat(rs.getString("BMSCellMinTem"));
            float BMSPackVol = Float.parseFloat(rs.getString("BMSPackVol"));
            float BMSPACKSOC = Float.parseFloat(rs.getString("BMSPACKSOC"));
            float BMSCellVoltMin = Float.parseFloat(rs.getString("BMSCellVoltMin"));
            float BMSPtIsltnRstc = Float.parseFloat(rs.getString("BMSPtIsltnRstc"));
            float HVDCDCTem = Float.parseFloat(rs.getString("HVDCDCTem"));
            float BrkVacuumPumpSts = Float.parseFloat(rs.getString("BrkVacuumPumpSts"));
            float HVDCDCSta = Float.parseFloat(rs.getString("HVDCDCSta"));
            float TMInvtrTem = Float.parseFloat(rs.getString("TMInvtrTem"));
            float ISGInvtrTem = Float.parseFloat(rs.getString("ISGInvtrTem"));
            float BMSHVILClsd = Float.parseFloat(rs.getString("BMSHVILClsd"));
            float TMSttrTem = Float.parseFloat(rs.getString("TMSttrTem"));
            float ISGSttrTem = Float.parseFloat(rs.getString("ISGSttrTem"));


            data.setTempPlusHigherl(calcTempPlusHigherl(BMSCellMaxTem,  BMSCellMinTem));
            data.setTempratureHighestl(calcTempratureHighestl(BMSCellMaxTem));
            data.setTotalVolHighestl(calcTotalVolHighestl(BMSPackVol));
            data.setTotalVolLowestl(calcTotalVolLowestl(BMSPackVol));
            data.setSocLowerl(calcSocLowerl(BMSPACKSOC));
            data.setSellVolHighestL(calcSellVolHighestL());
            data.setSellVolLowestl(calcSellVolLowestl(BMSCellVoltMin));
            data.setSocHigherAlarm(calcSocHigherAlarm(BMSPACKSOC));
            data.setSocJumpAlarm(calcSocJumpAlarm(BMSPACKSOC));
            data.setBatterySysDismatch(calcBatterySysDismatch());
            data.setVolPlusBiggerl(calcVolPlusBiggerl());
            data.setInsuLowl(calcInsuLowl(BMSPtIsltnRstc));
            data.setDcdcTempAlarm(calcDcdcTempAlarm(HVDCDCTem));
            data.setIcuBrakeSysErr(calcIcuBrakeSysErr(BrkVacuumPumpSts));
            data.setDcdcStatusAlarm(calcDcdcStatusAlarm(HVDCDCSta));
            data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(TMInvtrTem,  ISGInvtrTem));
            data.setIsLockHigh(calcIsLockHigh(BMSHVILClsd));
            data.setIsMotorTempHigh(calcIsMotorTempHigh(TMSttrTem,  ISGSttrTem));
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
            System.out.println(e);
        }
        return data;
    }


    // 温度差异报警
    private static byte calcTempPlusHigherl(float BMSCellMaxTem, float BMSCellMinTem) {
        /*
        IF BMSCellMaxTem<87 && BMSCellMinTem<87 (BMSCellMaxTem-BMSCellMinTem)>=20
        THEN Bit0=1
        ELSE Bit0=0
         *
         * 连续2S满足才报警
         */
        if ( BMSCellMaxTem<87 && BMSCellMinTem<87 ) {
            return (byte) ((BMSCellMaxTem-BMSCellMinTem) >= 20.0f ? 0x01 : 0x00);
        }
        return 0x00;
    }

    // 电池高温报警
    private static byte calcTempratureHighestl(float BMSCellMaxTem) {
        /*
        IF BMSCellMaxTem<87 && BMSCellMaxTem>=70
        THEN Bit1=1
        ELSE Bit1=0
        连续2S满足才报警
        */
        if (BMSCellMaxTem < 87 && BMSCellMaxTem >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过压报警
    private static byte calcTotalVolHighestl(float BMSPackVol) {
        /*
        IF BMSPackVol<1023.5 && BMSPackVol>=391
        THEN Bit2=1
        ELSE Bit2=0
        连续2S满足才报警
        */
        if (BMSPackVol < 1023.5 && BMSPackVol >= 391) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型欠压报警
    private static byte calcTotalVolLowestl(float BMSPackVol) {
        /*
        IF BMSPackVol<1023.5 && BMSPackVol<=171
        THEN Bit3=1
        ELSE Bit3=0
        连续2S满足才报警
        */
        if (BMSPackVol < 1023.5 && BMSPackVol <= 171) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC低报警
    private static byte calcSocLowerl(float BMSPACKSOC) {
        /*
        IF BMSPACKSOC<102.2 && BMSPACKSOC<=0
        THEN Bit4=1
        ELSE Bit4=0
        N=0 %
        连续2S满足才报警
        */
        if (BMSPACKSOC < 102.2 && BMSPACKSOC <= 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 单体电池过压报警
    private static byte calcSellVolHighestL() {
        return 0x00;
    }

    // 单体电池欠压报警
    private static byte calcSellVolLowestl(float BMSCellVoltMin) {
        //        IF BMSCellVoltMin<8.191 && BMSCellVoltMin<=1.95
        //        THEN Bit6=1
        //        ELSE Bit6=0
        if (BMSCellVoltMin<8.191 && BMSCellVoltMin<=1.95) {
            return 0x01;
        }
        return 0x00;
    }


    // SOC过高报警
    private static byte calcSocHigherAlarm(float BMSPACKSOC) {
        /*
        IF BMSPACKSOC<102.2 && BMSPACKSOC>100
        THEN Bit7=1
        ELSE Bit7=0
        */
        if (BMSPACKSOC < 102.2 && BMSPACKSOC > 100) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC跳变报警
    private static byte calcSocJumpAlarm(float BMSPACKSOC) {
        /*
        IF  BMSPACKSOC<102.2  && (|BMSPACKSOC(n+1)- BMSPACKSOC(n )|)>=15
        THEN Bit8=1
        ELSE Bit8=0
        */
        if (BMSPACKSOC < 102.2 && Math.abs(BMSPACKSOC * (2 + 1) - BMSPACKSOC * (2)) >= 15) {
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
    private static byte calcInsuLowl(float BMSPtIsltnRstc) {
        /*
        IF BMSPtIsltnRstc<=250
        THEN Bit11=1
        ELSE Bit11=0
        连续2S满足才报警
        */
        if (BMSPtIsltnRstc <= 250) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC温度报警
    private static byte calcDcdcTempAlarm(float HVDCDCTem) {
        //        IF HVDCDCTem>=87
        //        THEN Bit12=1
        //        ELSE Bit12=0
        if (HVDCDCTem>=87) {
            return 0x01;
        }
        return 0x00;
    }

    // 制动系统报警
    private static byte calcIcuBrakeSysErr(float BrkVacuumPumpSts) {
        /*
        IF BrkVacuumPumpSts=1
        THEN Bit13=0x1
        ELSE Bit13=0x0

        制动液位低/EBD故障/ABS故障/VSE故障/iBoost故障
        连续2S满足才报警
        */
        if (BrkVacuumPumpSts == 1) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC状态报警
    private static byte calcDcdcStatusAlarm(float HVDCDCSta) {
        /*
        IF HVDCDCSta=0x5
        THEN Bit14=1
        ELSE Bit14=0
        */
        if (HVDCDCSta == 5) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机控制器温度报警
    private static byte calcIsMotorControlerTempHigh(float TMInvtrTem, float ISGInvtrTem) {
        /*
        IF TMInvtrTem>=142 || ISGInvtrTem>=142
        THEN Bit15=1
        ELSE Bit15=0

        */
        if (TMInvtrTem >= 142 || ISGInvtrTem >= 142) {
            return 0x01;
        }
        return 0x00;
    }

    // 高压互锁状态报警
    private static byte calcIsLockHigh(float BMSHVILClsd) {
        /*
        IF BMSHVILClsd=0x0
        THEN Bit16=1
        ELSE Bit16=0
        */
        if (BMSHVILClsd == 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机温度报警
    private static byte calcIsMotorTempHigh(float TMSttrTem, float ISGSttrTem) {
        /*
        IF TMSttrTem>=N ||
        ISGSttrTem>=N ||

        THEN Bit17=1
        ELSE Bit17=0
        */
        if (TMSttrTem >= 186 || ISGSttrTem >= 186) {
            return 0x01;
        }
        return 0x00;
    }

    // 车载储能装置类型过充
    private static byte calcSellVolHighestChargerl() {
        return 0x00;
    }

}
