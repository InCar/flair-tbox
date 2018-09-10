package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX07Alarm;
import org.bson.Document;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/9 10:17
 */
public class OLD_AS24x07Alarm extends MongoX implements IMongoX07Alarm {
    @Override
    public GBx07Alarm makeGBx07Alarm(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        float vehBMSCellMaxTem = parseFloatWithDef(bsonDoc, "vehBMSCellMaxTem");
        float vehBMSCellMinTem = parseFloatWithDef(bsonDoc, "vehBMSCellMinTem");
        float vehBMSPackVol = parseFloatWithDef(bsonDoc, "vehBMSPackVol");
        float vehBMSPackSOC = parseFloatWithDef(bsonDoc, "vehBMSPackSOC");
        float vehBMSCellMaxVol = parseFloatWithDef(bsonDoc, "vehBMSCellMaxVol");
        float vehBMSCellMinVol = parseFloatWithDef(bsonDoc, "vehBMSCellMinVol");
        float vehBMSPtIsltnRstc = parseFloatWithDef(bsonDoc, "vehBMSPtIsltnRstc");
        int vehHVDCDCTem = parseIntWithDef(bsonDoc, "vehHVDCDCTem");
        int vehHVDCDCSta = parseIntWithDef(bsonDoc, "vehHVDCDCSta");
        int vehTMInvtrTem = parseIntWithDef(bsonDoc, "vehTMInvtrTem");
        int vehISGInvtrTem = parseIntWithDef(bsonDoc, "vehISGInvtrTem") == 0 ? 10 : parseIntWithDef(bsonDoc, "vehISGInvtrTem"); //
        int vehSAMInvtrTem = parseIntWithDef(bsonDoc, "vehSAMInvtrTem") == 0 ? 10 : parseIntWithDef(bsonDoc, "vehSAMInvtrTem");//
        int vehTMSttrTem = parseIntWithDef(bsonDoc, "vehTMSttrTem");
        int vehISGSttrTem = parseIntWithDef(bsonDoc, "vehISGSttrTem") == 0 ? 10 : parseIntWithDef(bsonDoc, "vehISGSttrTem"); //
        int vehSAMSttrTem = parseIntWithDef(bsonDoc, "vehSAMSttrTem") == 0 ? 10 : parseIntWithDef(bsonDoc, "vehSAMSttrTem"); //

        GBx07Alarm data = new GBx07Alarm(vin, tmGMT8);
        data.setTempPlusHigherl(calcTempPlusHigherl(vehBMSCellMinTem, vehBMSCellMaxTem));
        data.setTempratureHighestl(calcTempratureHighestl(vehBMSCellMaxTem));
        data.setTotalVolHighestl(calcTotalVolHighestl(vehBMSPackVol));
        data.setTotalVolLowestl(calcTotalVolLowestl(vehBMSPackVol));
        data.setSocLowerl(calcSocLowerl(vehBMSPackSOC));
        data.setSellVolHighestL(calcSellVolHighestL(vehBMSCellMaxVol));
        data.setSellVolLowestl(calcSellVolLowestl(vehBMSCellMinVol));
        data.setSocHigherAlarm(calcSocHigherAlarm(vehBMSPackSOC));
        data.setSocJumpAlarm(calcSocJumpAlarm(vehBMSPackSOC));
        data.setBatterySysDismatch(calcBatterySysDismatch());
        data.setVolPlusBiggerl(calcVolPlusBiggerl(vehBMSCellMaxVol, vehBMSCellMinVol));
        data.setInsuLowl(calcInsuLowl(vehBMSPtIsltnRstc));
        data.setDcdcTempAlarm(calcDcdcTempAlarm(vehHVDCDCTem));
        data.setIcuBrakeSysErr(calcIcuBrakeSysErr());
        data.setDcdcStatusAlarm(calcDcdcStatusAlarm(vehHVDCDCSta));
        data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(vehTMInvtrTem, vehISGInvtrTem, vehSAMInvtrTem));
        data.setIsLockHigh(calcIsLockHigh());
        data.setIsMotorTempHigh(calcIsMotorTempHigh(vehTMSttrTem, vehISGSttrTem, vehSAMSttrTem));
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
        IF (vehBMSCellMaxTem-vehBMSCellMinTem)>=N
        THEN Bit0=1
        ELSE Bit0=0
        N=20 ℃
        连续2S满足才报警
         */
        if ((vehBMSCellMaxTem - vehBMSCellMinTem) >= 20) {
            return 0x01;
        }
        return 0x00;
    }

    // 电池高温报警
    private static byte calcTempratureHighestl(float vehBMSCellMaxTem) {
        /*
        IF  vehBMSCellMaxTem>=N
        THEN Bit1=1
        ELSE Bit1=0
        N=70 ℃
        连续2S满足才报警
        */
        if (vehBMSCellMaxTem >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包过压报警
    private static byte calcTotalVolHighestl(float vehBMSPackVol) {
        /*
        IF vehBMSPackVol>=N
        THEN Bit2=1
        ELSE Bit2=0
        AS24= 391V;
        连续2S满足才报警
        */
        if (vehBMSPackVol >= 391) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包欠压报警
    private static byte calcTotalVolLowestl(float vehBMSPackVol) {
        /*
        IF vheBMSPackVol<=N
        THEN Bit3=1
        ELSE Bit3=0
        AS24= 171V;
        连续2S满足才报警
        */
        if (vehBMSPackVol <= 171) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC低报警
    private static byte calcSocLowerl(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC<=N
        THEN Bit4=1
        ELSE Bit4=0
        N=0 %
        连续2S满足才报警
        */
        if (vehBMSPackSOC <= 0) {
            return 0x01;
        }
        return 0x00;
    }

    // 单体可充电储能子系统过压报警
    private static byte calcSellVolHighestL(float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVol>=N
        THEN Bit5=1
        ELSE Bit5=0

        N=4.35 V
        连续2S满足才报警
        */
        if (vehBMSCellMaxVol >= 4.35) {
            return 0x01;
        }
        return 0x00;
    }

    // 单体可充电储能子系统欠压报警
    private static byte calcSellVolLowestl(float vehBMSCellMinVol) {
        /*
        IF vehBMSCellMinVol<=N
        THEN Bit6=1
        ELSE Bit6=0

        N=1.9 V
        连续2S满足才报警
        */
        if (vehBMSCellMinVol <= 1.9) {
            return 0x01;
        }
        return 0x00;
    }


    // SOC过高报警
    private static byte calcSocHigherAlarm(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC>=N
        THEN Bit7=1
        ELSE Bit7=0

        N=100 %
        连续2S满足才报警
        */
        if (vehBMSPackSOC >= 100) {
            return 0x01;
        }
        return 0x00;
    }

    // SOC跳变报警
    private static byte calcSocJumpAlarm(float vehBMSPackSOC) {
        /*
        IF  (|vehBMSPackSOC(n+1)- vehBMSPackSOC(n ))>=N
        THEN Bit8=1
        ELSE Bit8=0

        N=15 %
        满足条件1次即报警，不需要2次
        */
        if (Math.abs(vehBMSPackSOC * (15 + 1) - vehBMSPackSOC * 15) > 15) {
            return 0x01;
        }
        return 0x00;
    }

    // 可充电储能子系统包不匹配报警
    private static byte calcBatterySysDismatch() {
        /*
        Bit9=0
        注：针对可换电池包方案，不适用公司现有车型。
        */
        return 0x00;
    }

    // 可充电储能子系统单体一致性差报警
    private static byte calcVolPlusBiggerl(float vehBMSCellMaxVol, float vehBMSCellMinVol) {
        /*
        IF (vehBMSCellMaxVol- vehBMSCellMinVol)>=N
        THEN Bit10=1
        ELSE Bit10=0

        N=0.3 V
        连续2S满足才报警
        */
        if ((vehBMSCellMaxVol - vehBMSCellMinVol) >= 0.3) {
            return 0x01;
        }
        return 0x00;
    }

    // 绝缘报警
    private static byte calcInsuLowl(float vehBMSPtIsltnRstc) {
        /*
        IF  vehBMSPtIsltnRstc<=N
        THEN Bit11=1
        ELSE Bit11=0

        N=250 kohm
        连续2S满足才报警
        */
        if (vehBMSPtIsltnRstc <= 250) {
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

        AS24=  87C;
        连续2S满足才报警
        */
        if (vehHVDCDCTem >= 87) {
            return 0x01;
        }
        return 0x00;
    }

    // 制动系统报警
    private static byte calcIcuBrakeSysErr() {
        return 0x00;
    }

    // DC-DC状态报警
    private static byte calcDcdcStatusAlarm(int vehHVDCDCSta) {
        /*
        IF vehHVDCDCSta=5
        THEN Bit14=1
        ELSE Bit14=0
        */
        if (vehHVDCDCSta == 5) {
            return 0x01;
        }
        return 0x00;
    }

    // 驱动电机控制器温度报警
    private static byte calcIsMotorControlerTempHigh(int vehTMInvtrTem, int vehISGInvtrTem, int vehSAMInvtrTem) {
        /*
        IF vehTMInvtrTem>=N || vehISGInvtrTem>=N || vehSAMInvtrTem>=N
        THEN Bit15=1
        ELSE Bit15=0
        N=142 ℃
        连续2S满足才报警
        */
        int n = 142;
        if (vehTMInvtrTem >= n || vehISGInvtrTem >= n || vehSAMInvtrTem >= n) {
            return 0x01;
        }
        return 0x00;
    }


    // 高压互锁状态报警
    private static byte calcIsLockHigh() {
        return 0x00;
    }

    // 驱动电机温度报警
    private static byte calcIsMotorTempHigh(int vehTMSttrTem, int vehISGSttrTem, int vehSAMSttrTem) {
        /*
        IF vehTMSttrTem>=N ||
        vehISGSttrTem>=N ||
        vehSAMSttrTem>=N
        THEN Bit17=1
        ELSE Bit17=0

        AS24= 186C;
        连续2S满足才报警
        */
        int n = 175;
        if (vehTMSttrTem >= n || vehISGSttrTem >= n || vehSAMSttrTem >= n) {
            return 0x01;
        }
        return 0x00;
    }


    // 车载储能装置类型过充
    private static byte calcSellVolHighestChargerl(float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVol>=N1
        THEN Bit18=0x1
        ELSE IF vehBMSCellMaxVol<=N2

        N1=4.5V;
        N2= 4.2V;
        */
        if (vehBMSCellMaxVol >= 4.5) {
            return 0x01;
        } else if (vehBMSCellMaxVol <= 4.2) {
            return 0x00;
        } else {
            return 0x00;
        }
    }


}
