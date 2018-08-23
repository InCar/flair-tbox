package com.incarcloud.saic.modes.AS26;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author xy
 */
public class AS26x01Overview extends MongoX implements IMongoX01Overview {

    @Override
    public GBx01Overview makeGBx01Overview(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEPTRdy = parseIntWithDef(bsonDoc,"vehEPTRdy");
        int vehSysPwrMod = parseIntWithDef(bsonDoc,"vehSysPwrMod");
        int vehBMSBscSta = parseIntWithDef(bsonDoc,"vehBMSBscSta");
        float vehBMSPackCrnt = parseFloatWithDef(bsonDoc,"vehBMSPackCrnt");

        int vehSpdAvgDrvnV = parseIntWithDef(bsonDoc,"vehSpdAvgDrvnV");
        float vehSpeed = parseFloatWithDef(bsonDoc,"vehSpeed");

        int vehOdoV = parseIntWithDef(bsonDoc,"vehOdoV");
        float vehOdo = parseFloatWithDef(bsonDoc,"vehOdo");

        int vehBMSPackVolV = parseIntWithDef(bsonDoc,"vehBMSPackVolV");
        float vehBMSPackVol = parseFloatWithDef(bsonDoc,"vehBMSPackVol");

        int vehBMSPackCrntV = parseIntWithDef(bsonDoc,"vehBMSPackCrntV");

        int vehBMSPackSOCV = parseIntWithDef(bsonDoc,"vehBMSPackSOCV");
        float vehBMSPackSOC = parseFloatWithDef(bsonDoc,"vehBMSPackSOC");

        int vehHVDCDCSta = parseIntWithDef(bsonDoc,"vehHVDCDCSta");

        int vehEPTTrInptShaftToqV = parseIntWithDef(bsonDoc,"vehEPTTrInptShaftToqV");
        float vehEPTTrInptShaftToq = parseFloatWithDef(bsonDoc,"vehEPTTrInptShaftToq");
        int vehEPTBrkPdlDscrtInptStsV = parseIntWithDef(bsonDoc,"vehEPTBrkPdlDscrtInptStsV");
        int vehEPTBrkPdlDscrtInptSts = parseIntWithDef(bsonDoc,"vehEPTBrkPdlDscrtInptSts");
        int vehBrkSysBrkLghtsReqd = parseIntWithDef(bsonDoc,"vehBrkSysBrkLghtsReqd");
        int vehEPBSysBrkLghtsReqd = parseIntWithDef(bsonDoc,"vehEPBSysBrkLghtsReqd");
        int vehTrShftLvrPosV = parseIntWithDef(bsonDoc,"vehTrShftLvrPosV");
        int vehGearPos = parseIntWithDef(bsonDoc,"vehGearPos");

        float vehBMSPtIsltnRstc = parseFloatWithDef(bsonDoc,"vehBMSPtIsltnRstc");

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);
        data.setVehicleStatus(calcVehicleStatus(vehEPTRdy, vehSysPwrMod));
        data.setChargingStatus(calcChargingStatus(vehBMSBscSta, vehBMSPackCrntV, vehBMSPackCrnt, vehBMSPackSOCV, vehBMSPackSOC));
        data.setPowerSource(calcRunningMode());
        data.setSpeedKmH(calcSpeed(vehSpdAvgDrvnV, vehSpeed));
        data.setMileageKm(calcTotalMileage(vehOdoV, vehOdo));
        data.setVoltage(calcTotalVoltage(vehBMSPackVolV, vehBMSPackVol));
        data.setCurrent(calcTotalCurrent(vehBMSPackCrntV, vehBMSPackCrnt));
        data.setSoc((byte) calcSOC(vehBMSPackSOCV, vehBMSPackSOC));
        data.setDcdcOnOff(calcDcdcStatus(vehHVDCDCSta));
        data.setBit5(calcGearsBit5(vehEPTTrInptShaftToqV, vehEPTTrInptShaftToq));
        data.setBit4(calcGearsBit4(vehEPTTrInptShaftToqV, vehEPTTrInptShaftToq, vehEPTBrkPdlDscrtInptStsV,
                vehEPTBrkPdlDscrtInptSts, vehBrkSysBrkLghtsReqd, vehEPBSysBrkLghtsReqd));
        data.setBit3(calcGearsBit3(vehTrShftLvrPosV, vehGearPos));
        data.setResistancekOhm((int) calcInsulationResistance(vehBMSPtIsltnRstc));

        return data;
    }

    private static byte calcVehicleStatus(int vehEPTRdy, int vehSysPwrMod){
        /*
        IF vehEPTRdy=1
        THEN 车辆状态=0x01
        ELSE IF vehSysPwrMod=0
        THEN 车辆状态=0x02
        ELSE 车辆状态=0x03
        */
        byte vehicleStatus;
        if(vehEPTRdy == 1) vehicleStatus = 0x01;
        else if(vehSysPwrMod == 0) vehicleStatus = 0x02;
        else vehicleStatus = 0x03;

        return vehicleStatus;
    }

    private static byte calcChargingStatus(int vehBMSBscSta, int vehBMSPackCrntV, float vehBMSPackCrnt,
                                           int vehBMSPackSOCV, float vehBMSPackSOC){
        /*
        IF vehBMSBscSta=15
        THEN 充电状态=0xFF
        ELSE IF vehBMSBscSta=6 || 7 || 12
        THEN 充电状态=0x01
        ELSE IF vehBMSBscSta=3 && vehBMSPackCrntV= 0&& vehBMSPackCrnt<0
        THEN 充电状态=0x02
        ELSE IF vehBMSBscSta=9 || 10 || 13|| (vehBMSPackSOCV=0 && vehBMSPackSOC=100)
        THEN 充电状态=0x04
        ELSE 充电状态=0x03
         */
        byte chargingStatus;
        if (vehBMSBscSta == 15) {
            chargingStatus = (byte) 0xFF;
        } else if (vehBMSBscSta == 6 || vehBMSBscSta == 7 || vehBMSBscSta == 12) {
            chargingStatus = 0x01;
        } else if (vehBMSBscSta == 3 && vehBMSPackCrntV == 0 && vehBMSPackCrnt < 0) {
            chargingStatus = 0x02;
        } else if (vehBMSBscSta == 9 || vehBMSBscSta == 10 || vehBMSBscSta == 13
                || (vehBMSPackSOCV == 0 && vehBMSPackSOC == 100)) {
            chargingStatus = 0x04;
        } else {
            chargingStatus = 0x03;
        }

        return chargingStatus;
    }

    /**
     * 运行模式
     * 注: 所有有混动 & 电动的情况, 全部按电动的逻辑走
     * @return
     */
    private static byte calcRunningMode() {
        /*
        混动：
        IF vehElecVehSysMd=3 || 7 || 8
        THEN 运行模式=0x01
        ELSE IF vehElecVehSysMd=0 || 1 || 2 || 5 || 6
        THEN 运行模式=0x02
        ELSE IF vehElecVehSysMd=4
        THEN 运行模式=0x03
        ELSE 运行模式=0xFF
        电动：
        运行模式=0x01
         */
        byte runningMode = 0x01;
        return runningMode;
    }

    /**
     * 车速
     * @param vehSpdAvgDrvnV
     * @param vehSpeed
     * @return
     */
    private static float calcSpeed(int vehSpdAvgDrvnV, float vehSpeed) {
        /*
        沿用原bigdata格式（0.1km/h）：
        IF vehSpdAvgDrvnV=1
        THEN 车速=0xFF,0xFF
        ELSE 车速=vehSpeed
         */
        float speed;
        if (vehSpdAvgDrvnV == 1) {
            speed = 0xFFFF;
        } else {
            speed = vehSpeed;
        }
        return speed;
    }

    /**
     * 累计里程
     * @param vehOdoV
     * @param vehOdo
     * @return
     */
    private static float calcTotalMileage(int vehOdoV, float vehOdo) {
        /*
        沿用原bigdata格式（1km）：
        IF vehOdoV=1
        THEN 累加里程=0xFF,0xFF,0xFF,0xFF
        ELSE 累加里程=vehOdo
         */
        float totalMileage;
        if (vehOdoV == 1) {
            totalMileage = 0xFFFFFFFF;
        } else {
            totalMileage = vehOdo;
        }
        return totalMileage;
    }

    /**
     * 总电压
     * @param vehBMSPackVolV
     * @param vehBMSPackVol
     * @return
     */
    private static float calcTotalVoltage(int vehBMSPackVolV, float vehBMSPackVol) {
        /*
        IF vehBMSPackVolV=1
        THEN 总电压=0xFF,0xFF
        ELSE 总电压=vehBMSPackVol
         */
        float totalVoltage;
        if (vehBMSPackVolV == 1) {
            totalVoltage = 0xFFFF;
        } else {
            totalVoltage = vehBMSPackVol;
        }
        return totalVoltage;
    }

    /**
     * 总电流
     * @param vehBMSPackCrntV
     * @param vehBMSPackCrnt
     * @return
     */
    private static float calcTotalCurrent(int vehBMSPackCrntV, float vehBMSPackCrnt) {
        /*
        IF vehBMSPackCrntV=1
        THEN 总电流=0xFF,0xFF
        ELSE 总电流= vehBMSPackCrnt
         */
        float totalCurrent;
        if (vehBMSPackCrntV == 1) {
            totalCurrent = 0xFFFF;
        } else {
            totalCurrent =  vehBMSPackCrnt;
        }
        return totalCurrent;
    }

    /**
     * SOC
     * @param vehBMSPackSOCV
     * @param vehBMSPackSOC
     * @return
     */
    private static float calcSOC(int vehBMSPackSOCV, float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOCV=1
        THEN SOC=0xFF
        ELSE SOC=vehBMSPackSOC
         */
        float soc;
        if (vehBMSPackSOCV == 1) {
            soc = 0xFF;
        } else {
            soc = vehBMSPackSOC;
        }
        return soc;
    }

    /**
     * DC-DC状态
     * @param vehHVDCDCSta
     * @return
     */
    private static byte calcDcdcStatus(int vehHVDCDCSta) {
        /*
        IF vehHVDCDCSta=2
        THEN DC-DC 状态=0x01
        ELSE DC-DC 状态=0x02
         */
        byte dcdcStatus;
        if (vehHVDCDCSta == 2) {
            dcdcStatus = 0x01;
        } else {
            dcdcStatus = 0x02;
        }
        return dcdcStatus;
    }

    /**
     * 档位bit5
     * @param vehEPTTrInptShaftToqV
     * @param vehEPTTrInptShaftToq
     * @return
     */
    private static byte calcGearsBit5(int vehEPTTrInptShaftToqV, float vehEPTTrInptShaftToq) {
        /*
        IF vehEPTTrInptShaftToqV=0 && vehEPTTrInptShaftToq>0
        THEN Bit5=0x1
        ELSE Bit5=0x0
         */
        byte bit5;
        if (vehEPTTrInptShaftToqV == 0 && vehEPTTrInptShaftToq > 0) {
            bit5 = 0x01;
        } else {
            bit5 = 0x00;
        }
        return bit5;
    }

    /**
     * 档位bit4
     * @param vehEPTTrInptShaftToqV
     * @param vehEPTTrInptShaftToq
     * @param vehEPTBrkPdlDscrtInptStsV
     * @param vehEPTBrkPdlDscrtInptSts
     * @param vehBrkSysBrkLghtsReqd
     * @param vehEPBSysBrkLghtsReqd
     * @return
     */
    private static byte calcGearsBit4(int vehEPTTrInptShaftToqV, float vehEPTTrInptShaftToq,
                                      int vehEPTBrkPdlDscrtInptStsV, int vehEPTBrkPdlDscrtInptSts,
                                      int vehBrkSysBrkLghtsReqd, int vehEPBSysBrkLghtsReqd) {
        /*
        IF (vehEPTTrInptShaftToqV=0 && vehEPTTrInptShaftToq<0) || (vehEPTBrkPdlDscrtInptStsV=0&&vehEPTBrkPdlDscrtInptSts=1) || vehBrkSysBrkLghtsReqd=1 || vehEPBSysBrkLghtsReqd=1
        THEN Bit4=0x1
        ELSE Bit4=0x0
         */
        byte bit4;
        if ((vehEPTTrInptShaftToqV == 0 && vehEPTTrInptShaftToq < 0)
                || (vehEPTBrkPdlDscrtInptStsV ==0 && vehEPTBrkPdlDscrtInptSts == 1)
                || vehBrkSysBrkLghtsReqd ==1 || vehEPBSysBrkLghtsReqd ==1) {
            bit4 = 0x01;
        } else {
            bit4 = 0x00;
        }
        return bit4;
    }

    /**
     * 档位bit3~0
     * @param vehTrShftLvrPosV
     * @param vehGearPos
     * @return
     */
    private static byte calcGearsBit3(int vehTrShftLvrPosV, int vehGearPos) {
        /*
        IF vehTrShftLvrPosV=0 && vehGearPos=1
        THEN Bit3~0=0xF
        ELSE IF vehTrShftLvrPosV=0 && vehGearPos=4
        THEN Bit3~0=0xE
        ELSE IF vehTrShftLvrPosV=0 && vehGearPos=2
        THEN Bit3~0=0xD
        ELSE Bit3~0=0x0
         */
        byte bit3;
        if (vehTrShftLvrPosV == 0 && vehGearPos == 1) {
            bit3 = 0x0F;
        } else if (vehTrShftLvrPosV == 0 && vehGearPos == 4) {
            bit3 = 0x0E;
        } else if ((vehTrShftLvrPosV == 0 && vehGearPos == 2)) {
            bit3 = 0x0D;
        } else {
            bit3 = 0x00;
        }
        return bit3;
    }

    private static float calcInsulationResistance(float vehBMSPtIsltnRstc) {
        /*
        绝缘电阻=vehBMSPtIsltnRstc
         */
        return vehBMSPtIsltnRstc;
    }


}
