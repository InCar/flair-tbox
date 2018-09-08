package com.incarcloud.saic.modes.IP24;

import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX07Alarm;
import org.bson.Document;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * @Author GuoKun
 * @Despriction: 07Alarm
 * @Date Created in 2018/9/5 13:56
 */
public class IP24x07Alarm extends MongoX implements IMongoX07Alarm {
    @Override
    public GBx07Alarm makeGBx07Alarm(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);


        float vehBMSCellMaxTem = parseFloatWithDef(bsonDoc, "vehBMSCellMaxTem");
        float vehBMSCellMinTem = parseFloatWithDef(bsonDoc, "vehBMSCellMinTem");

        float vehBMSPackVolt = parseFloatWithDef(bsonDoc, "vehBMSPackVolt");

        float vehBMSPackSOC = parseFloatWithDef(bsonDoc, "vehBMSPackSOC");

        float vehBMSCellMaxVol = parseFloatWithDef(bsonDoc, "vehBMSCellMaxVol");

        float vehBMSCellMinVol = parseFloatWithDef(bsonDoc, "vehBMSCellMinVol");

        float vehBMSPtIsltnRstc = parseFloatWithDef(bsonDoc, "vehBMSPtIsltnRstc");
        int vehHVDCDCTem = parseIntWithDef(bsonDoc, "vehHVDCDCTem");
        int vehBrkFludLvlLow = parseIntWithDef(bsonDoc, "vehBrkFludLvlLow");

        int vehEBDF = parseIntWithDef(bsonDoc, "vehEBDF");
        int vehABSF = parseIntWithDef(bsonDoc, "vehABSF");
        int vehDSCSts = parseIntWithDef(bsonDoc, "vehDSCSts");
        int vehDCSta = parseIntWithDef(bsonDoc, "vehDCSta");
        int vehTMInvtrTem = parseIntWithDef(bsonDoc, "vehTMInvtrTem");
        int vehISGInvtrTem = parseIntWithDef(bsonDoc, "vehISGInvtrTem");
        int vehBMSHVILClsd = parseIntWithDef(bsonDoc, "vehBMSHVILClsd");
        int vehTMSttrTem = parseIntWithDef(bsonDoc, "vehTMSttrTem");
        int vehISGSttrTem = parseIntWithDef(bsonDoc, "vehISGSttrTem");

        GBx07Alarm data = new GBx07Alarm(vin, tmGMT8);
        data.setTempPlusHigherl(calcTempPlusHigherl(vehBMSCellMinTem, vehBMSCellMaxTem));
        data.setTempratureHighestl(calcTempratureHighestl(vehBMSCellMaxTem));
        data.setTotalVolHighestl(calcTotalVolHighestl(vehBMSPackVolt));
        data.setTotalVolLowestl(calcTotalVolLowestl(vehBMSPackVolt));
        data.setSocLowerl(calcSocLowerl(vehBMSPackSOC));
        data.setSellVolHighestL(calcSellVolHighestL(vehBMSCellMaxVol));
        data.setSellVolLowestl(calcSellVolLowestl(vehBMSCellMinVol));
        data.setSocHigherAlarm(calcSocHigherAlarm(vehBMSPackSOC));
        data.setSocJumpAlarm(calcSocJumpAlarm(vehBMSPackSOC));
        data.setBatterySysDismatch(calcBatterySysDismatch());
        data.setVolPlusBiggerl(calcVolPlusBiggerl(vehBMSCellMaxVol, vehBMSCellMinVol));
        data.setInsuLowl(calcInsuLowl(vehBMSPtIsltnRstc));
        data.setDcdcTempAlarm(calcDcdcTempAlarm(vehHVDCDCTem));
        data.setIcuBrakeSysErr(calcIcuBrakeSysErr(vehBrkFludLvlLow,  vehEBDF,  vehABSF,  vehDSCSts));
        data.setDcdcStatusAlarm(calcDcdcStatusAlarm(vehDCSta));
        data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(vehTMInvtrTem, vehISGInvtrTem));
        data.setIsLockHigh(calcIsLockHigh(vehBMSHVILClsd));
        data.setIsMotorTempHigh(calcIsMotorTempHigh(vehTMSttrTem, vehISGSttrTem));
        data.setSellVolHighestChargerl(calcSellVolHighestChargerl(vehBMSCellMaxVol));

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

        return data;
    }


    // 温度差异报警
    private static byte calcTempPlusHigherl(float vehBMSCellMinTem, float vehBMSCellMaxTem) {
        /*
         * IF vehBMSCellMaxTem<87 && vehBMSCellMinTem<87 && (vehBMSCellMaxTem-vehBMSCellMinTem)>=N
            THEN Bit0=1
            ELSE Bit0=0
         * N=20 ℃
         * 连续2S满足才报警
         */
        if (vehBMSCellMaxTem < 87  && vehBMSCellMinTem  < 87){
            return (byte) (vehBMSCellMaxTem - vehBMSCellMinTem >= 20.0f ? 0x01 : 0x00);
        }
        return 0x00;
    }

    // 电池高温报警
    private static byte calcTempratureHighestl( float vehBMSCellMaxTem) {
        /*
        IF vehBMSCellMaxTem<87 && vehBMSCellMaxTem>=N
        THEN Bit1=1
        ELSE Bit1=0
        N=70 ℃
        连续2S满足才报警
        */
        if (vehBMSCellMaxTem < 87 && vehBMSCellMaxTem >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包过压报警 车载储能装置类型过压报警
    private static byte calcTotalVolHighestl( float vehBMSPackVolt) {
        /*
        "混动/电动：
        IF vehBMSPackVolt<1023.5 &&  vehBMSPackVolt>=N
        THEN Bit2=1
        ELSE Bit2=0"
        "N=340.5V
        连续2S满足才报警"
        */
        if (vehBMSPackVolt < 1023.5 && vehBMSPackVolt >= 340.5) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包欠压报警  车载储能装置类型欠压报警
    private static byte calcTotalVolLowestl(float vehBMSPackVolt) {
        /*
        混动/电动：
        IF vehBMSPackVolt<1023.5 && vheBMSPackVolt<=N
        THEN Bit3=1
        ELSE Bit3=0
        N=193 V
        连续2S满足才报警
        */
        if (vehBMSPackVolt <1023.5 && vehBMSPackVolt <= 193) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC低报警
    private static byte calcSocLowerl(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC<102.2 && vehBMSPackSOC<=N
        THEN Bit4=1
        ELSE Bit4=0
        N=0 %
        连续2S满足才报警
        */
        if (vehBMSPackSOC < 102.2 && vehBMSPackSOC <= 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 单体可充电储能子系统过压报警  单体电池过压报警
    private static byte calcSellVolHighestL(float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVol<8.19 && vehBMSCellMaxVol>=N
        THEN Bit5=1
        ELSE Bit5=0
        N=3.65 V
        连续2S满足才报警
        */
        if (vehBMSCellMaxVol <8.19 && vehBMSCellMaxVol >=3.65) {
            return 0x01;
        }
        return 0x00;
    }


    // 单体可充电储能子系统欠压报警  单体电池欠压报警
    private static byte calcSellVolLowestl(float vehBMSCellMinVol) {
        /*
        IF vehBMSCellMinVol<8.19  && vehBMSCellMinVol<=N
        THEN Bit6=1
        ELSE Bit6=0
        N=1.95 V
        连续2S满足才报警
        */
        if (vehBMSCellMinVol<8.19  && vehBMSCellMinVol <= 1.95) {
            return 0x01;
        }
        return 0x00;
    }


    // SOC过高报警
    private static byte calcSocHigherAlarm(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC<102.2 && vehBMSPackSOC>=N
        THEN Bit7=1
        ELSE Bit7=0
        N=100 %
        连续2S满足才报警
        */
        if (vehBMSPackSOC<102.2 && vehBMSPackSOC>= 100) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC跳变报警
    private static byte calcSocJumpAlarm(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC<102.2 && (|vehBMSPackSOC(n+1)- vehBMSPackSOC(n )|)>=N
        THEN Bit8=1
        ELSE Bit8=0

        N=15 %
        满足条件1次即报警，不需要2次
        */
        if (vehBMSPackSOC<102.2 &&  Math.abs(vehBMSPackSOC*(2+1)- vehBMSPackSOC * 2) >= 15 ) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包不匹配报警 可充电储能系统不匹配报警
    private static byte calcBatterySysDismatch() {
        /*
        Bit9=0
        注：针对可换电池包方案，不适用公司现有车型。
        */
        return 0x00;
    }

    // 可充电储能子系统单体一致性差报警  电池单体一致性差报警
    private static byte calcVolPlusBiggerl(float vehBMSCellMaxVol, float vehBMSCellMinVol) {
        /*
        IF vehBMSCellMaxVol<8.19&& vehBMSCellMinVol<8.19 && (vehBMSCellMaxVol- vehBMSCellMinVol)>=N
        THEN Bit10=1
        ELSE Bit10=0
        N=0.3 V
        连续2S满足才报警
        */
        if (vehBMSCellMaxVol<8.19 && vehBMSCellMinVol<8.19 && (vehBMSCellMaxVol - vehBMSCellMinVol >= 0.3)) {
            return 0x01;
        }
        return 0x00;
    }

    // 绝缘报警
    private static byte calcInsuLowl( float vehBMSPtIsltnRstc) {
        /*
        IF vehBMSPtIsltnRstc<8191 && vehBMSPtIsltnRstc<=N
        THEN Bit11=1
        ELSE Bit11=0
        N=250 kohm
        连续2S满足才报警
        */
        if (vehBMSPtIsltnRstc < 8191 && vehBMSPtIsltnRstc <= 250) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC温度报警
    private static byte calcDcdcTempAlarm(int vehHVDCDCTem) {
        /*
        IF vehHVDCDCTem>=N
        THEN Bit12=1
        ELSE Bit12=0
        N=96 Deg C
        连续2S满足才报警
        */
        if (vehHVDCDCTem >= 96) {
            return 0x01;
        }
        return 0x00;
    }

    // 制动系统报警
    private static byte calcIcuBrakeSysErr(int vehBrkFludLvlLow, int vehEBDF,
                                           int vehABSF, int vehDSCSts) {
        /*
        IF vehBrkFludLvlLow=1 || vehEBDF=1 ||
        vehABSF=1 ||
        vehDSCSts=2
        THEN Bit13=0x1
        ELSE Bit13=0x0

        制动液位低/EBD故障/ABS故障/VSE故障/iBoost故障
        连续2S满足才报警
        */
        if (vehBrkFludLvlLow ==1 || vehEBDF ==1 || vehABSF ==1 || vehDSCSts ==2 ) {
            return 0x01;
        }
        return 0x00;
    }

    // DC-DC状态报警
    private static byte calcDcdcStatusAlarm(int vehDCSta) {
        /*
        IF vehDCSta=5
        THEN Bit14=1
        ELSE Bit14=0
        */
        if (vehDCSta == 5) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机控制器温度报警
    private static byte calcIsMotorControlerTempHigh(int vehTMInvtrTem, int vehISGInvtrTem) {
        /*
        IF vehTMInvtrTem>=N || vehISGInvtrTem>=N
        THEN Bit15=1
        ELSE Bit15=0

        N=142 ℃
        连续2S满足才报警
        */
        int n = 142;
        if (vehTMInvtrTem >= n || vehISGInvtrTem >= n ) {
            return 0x01;
        }
        return 0x00;
    }


    // 高压互锁状态报警
    private static byte calcIsLockHigh(int vehBMSHVILClsd) {
        /*
        IF vehBMSHVILClsd=0
        THEN Bit16=1
        ELSE Bit16=0
        */
        if (vehBMSHVILClsd == 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机温度报警
    private static byte calcIsMotorTempHigh(int vehTMSttrTem, int vehISGSttrTem) {
        /*
        IF vehTMSttrTem>=N || vehISGSttrTem>=N
        THEN Bit17=1
        ELSE Bit17=0

        N= 186 Deg C
        连续2S满足才报警
        */
        int n = 186;
        if (vehTMSttrTem >= n || vehISGSttrTem >= n ) {
            return 0x01;
        }
        return 0x00;
    }


    // 车载储能装置类型过充
    private static byte calcSellVolHighestChargerl(float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVol>=8.19
        THEN Bit18=0x0
        ELSE IF vehBMSCellMaxVol>=N1
        THEN Bit18=0x1
        ELSE IF vehBMSCellMaxVol<N2
        THEN Bit18=0x0
        ELSE Bit18=Bit18
        N1=N2=3.8V;
        */
        if (vehBMSCellMaxVol >= 8.19) {
            return 0x00;
        } else if (vehBMSCellMaxVol >= 3.8) {
            return 0x01;
        } else {
            return 0x00;
        }
    }
}
