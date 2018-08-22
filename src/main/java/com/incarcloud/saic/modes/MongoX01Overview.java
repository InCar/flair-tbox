package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author xy
 */
public class MongoX01Overview extends MongoX implements IMongoX01Overview {

    @Override
    public GBx01Overview makeGBx01Overview(Document bsonDoc){
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int vehEPTRdy = Integer.parseInt(bsonDoc.getString("vehEPTRdy"));
        int vehSysPwrMod = Integer.parseInt(bsonDoc.getString("vehSysPwrMod"));
        int vehBMSBscSta = Integer.parseInt(bsonDoc.getString("vehBMSBscSta"));
        float vehBMSPackCrnt = Float.parseFloat((bsonDoc.getString("vehBMSPackCrnt")));

        int vehElecVehSysMd = Integer.parseInt(bsonDoc.getString("vehElecVehSysMd"));

        int vehSpdAvgDrvnV = Integer.parseInt(bsonDoc.getString("vehSpdAvgDrvnV"));
        float vehSpeed = Float.parseFloat(bsonDoc.getString("vehSpeed"));

        int vehOdoV = Integer.parseInt(bsonDoc.getString("vehOdoV"));
        float vehOdo = Float.parseFloat(bsonDoc.getString("vehOdo"));

        int vehBMSPackVolV = Integer.parseInt(bsonDoc.getString("vehBMSPackVolV"));
        float vehBMSPackVol = Float.parseFloat(bsonDoc.getString("vehBMSPackVol"));

        int vehBMSPackCrntV = Integer.parseInt(bsonDoc.getString("vehBMSPackCrntV"));

        int vehBMSPackSOCV = Integer.parseInt(bsonDoc.getString("vehBMSPackSOCV"));
        float vehBMSPackSOC = Float.parseFloat(bsonDoc.getString("vehBMSPackSOC"));

        int vehHVDCDCSta = Integer.parseInt(bsonDoc.getString("vehHVDCDCSta"));

        int vehEPTTrInptShaftToqV = Integer.parseInt(bsonDoc.getString("vehEPTTrInptShaftToqV"));
        float vehEPTTrInptShaftToq = Float.parseFloat(bsonDoc.getString("vehEPTTrInptShaftToq"));
        int vehEPTBrkPdlDscrtInptStsV = Integer.parseInt(bsonDoc.getString("vehEPTBrkPdlDscrtInptStsV"));
        int vehEPTBrkPdlDscrtInptSts = Integer.parseInt(bsonDoc.getString("vehEPTBrkPdlDscrtInptSts"));
        int vehBrkSysBrkLghtsReqd = Integer.parseInt(bsonDoc.getString("vehBrkSysBrkLghtsReqd"));
        int vehEPBSysBrkLghtsReqd = Integer.parseInt(bsonDoc.getString("vehEPBSysBrkLghtsReqd"));
        int vehTrShftLvrPosV = Integer.parseInt(bsonDoc.getString("vehTrShftLvrPosV"));
        int vehGearPos = Integer.parseInt(bsonDoc.getString("vehGearPos"));

        float vehBMSPtIsltnRstc = Float.parseFloat(bsonDoc.getString("vehBMSPtIsltnRstc"));

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);
        data.setVehicleStatus(calcVehicleStatus(vehEPTRdy, vehSysPwrMod));
        data.setChargingStatus(calcChargingStatus(vehBMSBscSta, vehBMSPackCrnt));
        data.setPowerSource(calcRunningMode(vehElecVehSysMd));
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

    private static byte calcChargingStatus(int vehBMSBscSta, float vehBMSPackCrnt){
        /*
        IF vehBMSBscSta=6 || 7 || 12
        THEN 充电状态=0x01
        ELSE IF vehBMSBscSta=3 &&
        vehBMSPackCrnt<0
        THEN 充电状态=0x02
        ELSE IF vehBMSBscSta=9 || 10 || 13
        THEN 充电状态=0x04
        ELSE 充电状态=0x03
         */
        byte chargingStatus;
        if(vehBMSBscSta == 6 || vehBMSBscSta == 7 || vehBMSBscSta == 12)
            chargingStatus = 0x01;
        else if(vehBMSBscSta == 3 && vehBMSPackCrnt < 0)
            chargingStatus = 0x02;
        else if(vehBMSBscSta == 9 || vehBMSBscSta == 10 || vehBMSBscSta == 13)
            chargingStatus = 0x04;
        else
            chargingStatus = 0x03;

        return chargingStatus;
    }

    /**
     * 运行模式
     * @param vehElecVehSysMd
     * @return
     */
    private static byte calcRunningMode(int vehElecVehSysMd) {
        /*
        IF vehElecVehSysMd=3 || 7 || 8
        THEN 运行模式=0x01
        ELSE IF vehElecVehSysMd=0 || 1 || 2 || 5 || 6
        THEN 运行模式=0x02
        ELSE IF vehElecVehSysMd=4
        THEN 运行模式=0x03
        ELSE 运行模式=0xFF
         */
        byte runningMode;
        if (vehElecVehSysMd == 3 || vehElecVehSysMd == 7 || vehElecVehSysMd == 8) {
            runningMode = 0x01;
        } else if (vehElecVehSysMd == 0 || vehElecVehSysMd == 1 || vehElecVehSysMd == 2
                || vehElecVehSysMd == 5 || vehElecVehSysMd == 6) {
            runningMode = 0x02;
        } else if (vehElecVehSysMd == 4) {
            runningMode = 0x03;
        } else {
            runningMode = (byte) 0xFF;
        }
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
