package com.incarcloud.saic.modes.AS24;

import com.incarcloud.saic.GB32960.GBx07Alarm;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX07Alarm;
import org.bson.Document;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AS24x07Alarm extends MongoX implements IMongoX07Alarm {
    @Override
    public GBx07Alarm makeGBx07Alarm(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehBMSCellMaxTemV = parseIntWithDef(bsonDoc, "vehBMSCellMaxTemV");
        int vehBMSCellMinTemV = parseIntWithDef(bsonDoc, "vehBMSCellMinTemV");
        float vehBMSCellMaxTem = parseFloatWithDef(bsonDoc, "vehBMSCellMaxTem");
        float vehBMSCellMinTem = parseFloatWithDef(bsonDoc, "vehBMSCellMinTem");

        int vehBMSPackVolV = parseIntWithDef(bsonDoc, "vehBMSPackVolV");
        float vehBMSPackVol = parseFloatWithDef(bsonDoc, "vehBMSPackVol");

        int vehBMSPackSOCV = parseIntWithDef(bsonDoc, "vehBMSPackSOCV");
        float vehBMSPackSOC = parseFloatWithDef(bsonDoc, "vehBMSPackSOC");

        int vehBMSCellMaxVolV = parseIntWithDef(bsonDoc, "vehBMSCellMaxVolV");
        float vehBMSCellMaxVol = parseFloatWithDef(bsonDoc, "vehBMSCellMaxVol");

        int vehBMSCellMinVolV = parseIntWithDef(bsonDoc, "vehBMSCellMinVolV");
        float vehBMSCellMinVol = parseFloatWithDef(bsonDoc, "vehBMSCellMinVol");

        int vehBMSPtIsltnRstcV = parseIntWithDef(bsonDoc, "vehBMSPtIsltnRstcV");
        float vehBMSPtIsltnRstc = parseFloatWithDef(bsonDoc, "vehBMSPtIsltnRstc");
        int vehHVDCDCTem = parseIntWithDef(bsonDoc, "vehHVDCDCTem");
        int vehBrkFludLvlLow = parseIntWithDef(bsonDoc, "vehBrkFludLvlLow");
        int vehBrkSysRedBrkTlltReq = parseIntWithDef(bsonDoc, "vehBrkSysRedBrkTlltReq");
        int vehABSF = parseIntWithDef(bsonDoc, "vehABSF");
        int vehVSESts = parseIntWithDef(bsonDoc, "vehVSESts");
        int vehIbstrWrnngIO = parseIntWithDef(bsonDoc, "vehIbstrWrnngIO");
        int vehHVDCDCSta = parseIntWithDef(bsonDoc, "vehHVDCDCSta");
        int vehTMInvtrTem = parseIntWithDef(bsonDoc, "vehTMInvtrTem");
        int vehISGInvtrTem = parseIntWithDef(bsonDoc, "vehISGInvtrTem");
        int vehSAMInvtrTem = parseIntWithDef(bsonDoc, "vehSAMInvtrTem");
        int vehBMSHVILClsd = parseIntWithDef(bsonDoc, "aaaaa");
        int vehTMSttrTem = parseIntWithDef(bsonDoc, "vehTMSttrTem");
        int vehISGSttrTem = parseIntWithDef(bsonDoc, "vehISGSttrTem");
        int vehSAMSttrTem = parseIntWithDef(bsonDoc, "vehSAMSttrTem");

        GBx07Alarm data = new GBx07Alarm(vin, tmGMT8);
        data.setTempPlusHigherl(calcTempPlusHigherl(vehBMSCellMinTemV, vehBMSCellMaxTemV, vehBMSCellMinTem, vehBMSCellMaxTem));
        data.setTempratureHighestl(calcTempratureHighestl(vehBMSCellMaxTemV, vehBMSCellMaxTem));
        data.setTotalVolHighestl(calcTotalVolHighestl(vehBMSPackVolV, vehBMSPackVol));
        data.setTotalVolLowestl(calcTotalVolLowestl(vehBMSPackVolV, vehBMSPackVol));
        data.setSocLowerl(calcSocLowerl(vehBMSPackSOCV, vehBMSPackSOC));
        data.setSellVolHighestL(calcSellVolHighestL(vehBMSCellMaxVolV, vehBMSCellMaxVol));
        data.setSellVolLowestl(calcSellVolLowestl(vehBMSCellMinVolV, vehBMSCellMinVol));
        data.setSocHigherAlarm(calcSocHigherAlarm(vehBMSPackSOCV, vehBMSPackSOC));
        data.setSocJumpAlarm(calcSocJumpAlarm(vehBMSPackSOCV, vehBMSPackSOC));
        data.setBatterySysDismatch(calcBatterySysDismatch());
        data.setVolPlusBiggerl(calcVolPlusBiggerl(vehBMSCellMaxVolV, vehBMSCellMinVolV, vehBMSCellMaxVol, vehBMSCellMinVol));
        data.setInsuLowl(calcInsuLowl(vehBMSPtIsltnRstcV, vehBMSPtIsltnRstc));
        data.setDcdcTempAlarm(calcDcdcTempAlarm(vehHVDCDCTem));
        data.setIcuBrakeSysErr(calcIcuBrakeSysErr(vehBrkFludLvlLow, vehBrkSysRedBrkTlltReq, vehABSF, vehVSESts, vehIbstrWrnngIO));
        data.setDcdcStatusAlarm(calcDcdcStatusAlarm(vehHVDCDCSta));
        data.setIsMotorControlerTempHigh(calcIsMotorControlerTempHigh(vehTMInvtrTem, vehISGInvtrTem, vehSAMInvtrTem));
        data.setIsLockHigh(calcIsLockHigh(vehBMSHVILClsd));
        data.setIsMotorTempHigh(calcIsMotorTempHigh(vehTMSttrTem, vehISGSttrTem, vehSAMSttrTem));
        data.setSellVolHighestChargerl(calcSellVolHighestChargerl(vehBMSCellMaxVolV, vehBMSCellMaxVol));

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


    private static byte calcTempPlusHigherl(int vehBMSCellMinTemV, int vehBMSCellMaxTemV,
                                            float vehBMSCellMinTem, float vehBMSCellMaxTem) {
        /*
         * IF vehBMSCellMaxTemV=0 && vehBMSCellMinTemv=0 && (vehBMSCellMaxTem-vehBMSCellMinTem)>=N
         * THEN Bit0=1
         * ELSE Bit0=0
         *
         * N=20 ℃
         * 连续2S满足才报警
         */

        if (vehBMSCellMaxTemV == 0 && vehBMSCellMinTemV == 0)
            return (byte) (vehBMSCellMaxTem - vehBMSCellMinTem >= 20.0f ? 0x01 : 0x00);
        return 0x00;
    }

    private static byte calcTempratureHighestl(int vehBMSCellMaxTemV, float vehBMSCellMaxTem) {
        /*
        IF vehBMSCellMaxTemV=0 && vehBMSCellMaxTem>=N
        THEN Bit1=1
        ELSE Bit1=0

        N=70 ℃
        连续2S满足才报警
        */
        if (vehBMSCellMaxTemV == 0 && vehBMSCellMaxTem >= 70) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcTotalVolHighestl(int vehBMSPackVolV, float vehBMSPackVol) {
        /*
        IF vehBMSPackVolV=0 &&  vehBMSPackVol>=N
        THEN Bit2=1
        ELSE Bit2=0

        AS24= 391V;
        连续2S满足才报警
        */
        if (vehBMSPackVolV == 0 && vehBMSPackVol >= 391) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcTotalVolLowestl(int vehBMSPackVolV, float vehBMSPackVol) {
        /*
        IF vehBMSPackVolV=0 && vehBMSPackVol<=N
        THEN Bit3=1
        ELSE Bit3=0

        AS24= 171V;
        连续2S满足才报警
        */
        if (vehBMSPackVolV == 0 && vehBMSPackVol <= 171) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSocLowerl(int vehBMSPackSOCV, float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOCV=0 && vehBMSPackSOC<=N
        THEN Bit4=1
        ELSE Bit4=0

        N=0 %
        连续2S满足才报警
        */
        if (vehBMSPackSOCV == 0 && vehBMSPackSOC <= 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSellVolHighestL(int vehBMSCellMaxVolV, float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVolV=0 && vehBMSCellMaxVol>=N
        THEN Bit5=1
        ELSE Bit5=0

        N=4.35 V
        连续2S满足才报警
        */
        if (vehBMSCellMaxVolV == 0 && vehBMSCellMaxVol >= 4.35) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSellVolLowestl(int vehBMSCellMinVolV, float vehBMSCellMinVol) {
        /*
        IF vehBMSCellMinVolV=0 && vehBMSCellMinVol<=N
        THEN Bit6=1
        ELSE Bit6=0

        N=1.9 V
        连续2S满足才报警
        */
        if (vehBMSCellMinVolV == 0 && vehBMSCellMinVol <= 1.9) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSocHigherAlarm(int vehBMSPackSOCV, float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOCV=0 && vehBMSPackSOC>=N
        THEN Bit7=1
        ELSE Bit7=0

        N=100 %
        连续2S满足才报警
        */
        if (vehBMSPackSOCV == 0 && vehBMSPackSOC >= 100) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSocJumpAlarm(int vehBMSPackSOCV, float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOCV=0 && (|vehBMSPackSOC(n+1)- vehBMSPackSOC(n )|)>=N
        THEN Bit8=1
        ELSE Bit8=0

        N=15 %
        满足条件1次即报警，不需要2次
        */
        if (vehBMSPackSOCV == 0 && Math.abs(vehBMSPackSOC * (15 + 1) - vehBMSPackSOC * 15) >= 15) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcBatterySysDismatch() {
        /*
        Bit9=0
        注：针对可换电池包方案，不适用公司现有车型。
        */
        return 0x00;
    }

    private static byte calcVolPlusBiggerl(int vehBMSCellMaxVolV, int vehBMSCellMinVolV,
                                           float vehBMSCellMaxVol, float vehBMSCellMinVol) {
        /*
        IF vehBMSCellMaxVolV=0 && vehBMSCellMinVolV=0 && (vehBMSCellMaxVol- vehBMSCellMinVol)>=N
        THEN Bit10=1
        ELSE Bit10=0

        N=0.3 V
        连续2S满足才报警
        */
        if (vehBMSCellMaxVolV == 0 && vehBMSCellMinVolV == 0
                && (vehBMSCellMaxVol - vehBMSCellMinVol >= 0.3)) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcInsuLowl(int vehBMSPtIsltnRstcV, float vehBMSPtIsltnRstc) {
        /*
        IF vehBMSPtIsltnRstcV=0 && vehBMSPtIsltnRstc<=N
        THEN Bit11=1
        ELSE Bit11=0

        N=250 kohm
        连续2S满足才报警
        */
        if (vehBMSPtIsltnRstcV == 0 && vehBMSPtIsltnRstc <= 250) {
            return 0x01;
        }
        return 0x00;
    }

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

    private static byte calcIcuBrakeSysErr(int vehBrkFludLvlLow, int vehBrkSysRedBrkTlltReq,
                                           int vehABSF, int vehVSESts, int vehIbstrWrnngIO) {
        /*
        IF vehBrkFludLvlLow=1 || vehBrkSysRedBrkTlltReq=1 ||
        vehABSF=1 ||
        vehVSESts=2 ||
        vehIbstrWrnngIO=0x1
        THEN Bit13=0x1
        ELSE Bit13=0x0

        制动液位低/EBD故障/ABS故障/VSE故障/iBoost故障
        连续2S满足才报警
        */
        if (vehBrkFludLvlLow ==1 || vehBrkSysRedBrkTlltReq ==1
                || vehABSF ==1 || vehVSESts ==2 || vehIbstrWrnngIO == 1) {
            return 0x01;
        }
        return 0x00;
    }

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

    private static byte calcIsMotorTempHigh(int vehTMSttrTem, int vehISGSttrTem, int vehSAMSttrTem) {
        /*
        IF vehTMSttrTem>=N |
        vehISGSttrTem>=N ||
        vehSAMSttrTem>=N
        THEN Bit17=1
        ELSE Bit17=0

        AS24= 186C;
        连续2S满足才报警
        */
        int n = 186;
        if (vehTMSttrTem >= n || vehISGSttrTem >= n || vehSAMSttrTem >= n) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcSellVolHighestChargerl(int vehBMSCellMaxVolV, float vehBMSCellMaxVol) {
        /*
        IF vehBMSCellMaxVolV=1
        THEN Bit18=0x0
        ELSE IF vehBMSCellMaxVol>=N1
        THEN Bit18=0x1
        ELSE IF vehBMSCellMaxVol<=N2
        THEN Bit18=0x0
        ELSE Bit18=Bit18

        N1=4.5V;
        N2= 4.2V;
        */
        if (vehBMSCellMaxVolV == 1) {
            return 0x00;
        } else if (vehBMSCellMaxVol >= 4.5) {
            return 0x01;
        } else if (vehBMSCellMaxVol <= 4.2) {
            return 0x00;
        } else {
            return 0x00;
        }
    }


}
