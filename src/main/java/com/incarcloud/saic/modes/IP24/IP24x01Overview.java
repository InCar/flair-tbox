package com.incarcloud.saic.modes.IP24;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author xy
 */
public class IP24x01Overview extends MongoX implements IMongoX01Overview {
    @Override
    public GBx01Overview makeGBx01Overview(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        String vehPTReadyStr = bsonDoc.getString("vehPTReady");
        /*if (vehPTReadyStr == null) {
            return null;
        }*/
        int vehPTReady = Integer.parseInt(vehPTReadyStr);
        int vehKeySwitchStateIGN = Integer.parseInt(bsonDoc.getString("vehKeySwitchStateIGN"));
        int vehBMSBasicSta = Integer.parseInt(bsonDoc.getString("vehBMSBasicSta"));
        float vehBMSPackCurrent = Float.parseFloat((bsonDoc.getString("vehBMSPackCurrent")));

        int vehHEVSystemMode = Integer.parseInt(bsonDoc.getString("vehHEVSystemMode"));

        int vehSpdAvgDrvnV = Integer.parseInt(bsonDoc.getString("vehSpdAvgDrvnV"));
        float vehSpeed = Float.parseFloat(bsonDoc.getString("vehSpeed"));

        float vehOdo = Float.parseFloat(bsonDoc.getString("vehOdo"));

        float vehBMSPackVolt = Float.parseFloat(bsonDoc.getString("vehBMSPackVolt"));

        float vehBMSPackSOC = Float.parseFloat(bsonDoc.getString("vehBMSPackSOC"));

        int vehDCSta = Integer.parseInt(bsonDoc.getString("vehDCSta"));

        int vehTransInputTorqueV = Integer.parseInt(bsonDoc.getString("vehTransInputTorqueV"));
        float vehTransInputTorque = Float.parseFloat(bsonDoc.getString("vehTransInputTorque"));
        int vehBrakeLightSwitchV = Integer.parseInt(bsonDoc.getString("vehBrakeLightSwitchV"));
        int vehBrakeLightSwitch = Integer.parseInt(bsonDoc.getString("vehBrakeLightSwitch"));
        int vehEPBSysBrkLghtsReqd = Integer.parseInt(bsonDoc.getString("vehEPBSysBrkLghtsReqd"));
        int vehBrkSysBrkLghtsReqd = Integer.parseInt(bsonDoc.getString("vehBrkSysBrkLghtsReqd"));
        int vehGearPosV = Integer.parseInt(bsonDoc.getString("vehGearPosV"));
        int vehGearPos = Integer.parseInt(bsonDoc.getString("vehGearPos"));

        float vehBMSPtIsltnRstc = Float.parseFloat(bsonDoc.getString("vehBMSPtIsltnRstc"));

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);
        data.setVehicleStatus(calcVehicleStatus(vehPTReady, vehKeySwitchStateIGN));
        data.setChargingStatus(calcChargingStatus(vehBMSBasicSta, vehBMSPackCurrent));
        data.setPowerSource(calcRunningMode(vehHEVSystemMode));
        data.setSpeedKmH(calcSpeed(vehSpdAvgDrvnV, vehSpeed));
        data.setMileageKm(calcTotalMileage(vehOdo));
        data.setVoltage(calcTotalVoltage(vehBMSPackVolt));
        data.setCurrent(calcTotalCurrent(vehBMSPackCurrent));
        data.setSoc((byte) calcSOC(vehBMSPackSOC));
        data.setDcdcOnOff(calcDcdcStatus(vehDCSta));
        data.setBit5(calcGearsBit5(vehTransInputTorqueV, vehTransInputTorque));
        data.setBit4(calcGearsBit4(vehTransInputTorqueV, vehTransInputTorque, vehBrakeLightSwitchV,
                vehBrakeLightSwitch, vehEPBSysBrkLghtsReqd, vehBrkSysBrkLghtsReqd));
        data.setBit3(calcGearsBit3(vehGearPosV, vehGearPos));
        data.setResistancekOhm((int) calcInsulationResistance(vehBMSPtIsltnRstc));

        return data;
    }

    private static byte calcVehicleStatus(int vehPTReady, int vehKeySwitchStateIGN) {
        /*
        IF vehPTReady=1
        THEN 车辆状态=0x01
        ELSE IF vehKeySwitchStateIGN=0
        THEN 车辆状态=0x02
        ELSE 车辆状态=0x03
         */
        byte vehicleStatus;
        if (vehPTReady == 1) {
            vehicleStatus = 0x01;
        } else if (vehKeySwitchStateIGN == 0) {
            vehicleStatus = 0x02;
        } else {
            vehicleStatus = 0x03;
        }
        return vehicleStatus;
    }

    private static byte calcChargingStatus(int vehBMSBasicSta, float vehBMSPackCurrent) {
        /*
        IF vehBMSBasicSta=15
        THEN 充电状态=0xFF
        ELSE IF vehBMSBasicSta=6 || 7
        THEN 充电状态=0x01
        ELSE IF vehBMSBasicSta=3 &&
        vehBMSPackCurrent<0
        THEN 充电状态=0x02
        ELSE IF vehBMSBasicSta=9 || 10
        THEN 充电状态=0x04
        ELSE 充电状态=0x03
         */
        byte chargingStatus;
        if (vehBMSBasicSta == 15) {
            chargingStatus = (byte) 0xFF;
        } else if (vehBMSBasicSta == 6 || vehBMSBasicSta == 7) {
            chargingStatus = 0x01;
        } else if (vehBMSBasicSta == 3 && vehBMSPackCurrent < 0) {
            chargingStatus = 0x02;
        } else if ( vehBMSBasicSta == 9 || vehBMSBasicSta == 10) {
            chargingStatus = 0x04;
        } else {
            chargingStatus = 0x03;
        }
        return chargingStatus;
    }

    private static byte calcRunningMode(int vehHEVSystemMode) {
        /*
        IF vehHEVSystemMode=3 || 7 || 8
        THEN 运行模式=0x01
        ELSE IF vehHEVSystemMode=0 || 1 || 2 || 5 || 6
        THEN运行模式=0x02
        ELSE IF vehHEVSystemMode=4
        THEN运行模式=0x03
        ELSE运行模式=0xFF
         */
        byte runningMode;
        if (vehHEVSystemMode == 3 || vehHEVSystemMode == 7 || vehHEVSystemMode == 8) {
            runningMode = 0x01;
        } else if (vehHEVSystemMode == 0 || vehHEVSystemMode == 1 || vehHEVSystemMode == 2
                || vehHEVSystemMode == 5 || vehHEVSystemMode == 6) {
            runningMode = 0x02;
        } else if (vehHEVSystemMode == 4) {
            runningMode = 0x03;
        } else {
            runningMode = (byte) 0xFF;
        }
        return runningMode;
    }

    private static float calcSpeed(int vehSpdAvgDrvnV, float vehSpeed) {
        /*
        IF vehSpdAvgDrvnV=0
        THEN 车速=0xFF,0xFF
        ELSE 车速=vehSpeed
         */
        float speed;
        if (vehSpdAvgDrvnV == 0) {
            speed = 0xFFFF;
        } else {
            speed = vehSpeed;
        }
        return speed;
    }

    private static float calcTotalMileage(float vehOdo) {
        /*
        累计里程=vehOdo
         */
        return vehOdo;
    }

    private static float calcTotalVoltage(float vehBMSPackVolt) {
        /*
        IF vehBMSPackVolt=1023.5||1023.75
        THEN 总电压=0xFF,0xFF
        ELSE 总电压=vehBMSPackVolt
         */
        float totalVoltage;
        if (vehBMSPackVolt == 1023.5 || vehBMSPackVolt == 1023.75) {
            totalVoltage = 0xFFFF;
        } else {
            totalVoltage = vehBMSPackVolt;
        }
        return totalVoltage;
    }

    private static float calcTotalCurrent(float vehBMSPackCurrent) {
        /*
        IF vehBMSPackCurrent=638.35||638.375
        THEN 总电流=0xFF,0xFF
        ELSE 总电流= vehBMSPackCurrent
         */
        float totalCurrent;
        if (vehBMSPackCurrent == 638.35 || vehBMSPackCurrent == 638.375) {
            totalCurrent = 0xFFFF;
        } else {
            totalCurrent = vehBMSPackCurrent;
        }
        return totalCurrent;
    }

    private static float calcSOC(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC=102.2||102.3
        THEN SOC=0xFF
        ELSE SOC=vehBMSPackSOC
         */
        float soc;
        if (vehBMSPackSOC == 102.2 || vehBMSPackSOC == 102.3) {
            soc = 0xFF;
        } else {
            soc = vehBMSPackSOC;
        }
        return soc;
    }

    private static byte calcDcdcStatus(int vehDCSta) {
        /*
        IF vehDCSta=2
        THEN DC-DC 状态=0x01
        ELSE DC-DC 状态=0x02
         */
        byte dcdcStatus;
        if (vehDCSta == 2) {
            dcdcStatus = 0x01;
        } else {
            dcdcStatus = 0x02;
        }
        return dcdcStatus;
    }

    private static byte calcGearsBit5(int vehTransInputTorqueV, float vehTransInputTorque) {
        /*
        IF vehTransInputTorqueV=1 &&
         vehTransInputTorque>0.0002
        THEN Bit5=0x1
        ELSE Bit5=0x0
         */
        byte bit5;
        if (vehTransInputTorqueV == 1 && vehTransInputTorque > 0.0002) {
            bit5 = 0x01;
        } else {
            bit5 = 0x00;
        }
        return bit5;
    }

    private static byte calcGearsBit4(int vehTransInputTorqueV, float vehTransInputTorque,
                                      int vehBrakeLightSwitchV, int vehBrakeLightSwitch,
                                      int vehEPBSysBrkLghtsReqd, int vehBrkSysBrkLghtsReqd) {
        /*
        IF (vehTransInputTorqueV=1 && vehTransInputTorque<0.0002)
        || (vehBrakeLightSwitchV=1 && vehBrakeLightSwitch=1)
        || vehEPBSysBrkLghtsReqd=1 || vehBrkSysBrkLghtsReqd=1
        THEN Bit4=0x1
        ELSE Bit4=0x0
         */
        byte bit4;
        if ((vehTransInputTorqueV == 1 && vehTransInputTorque < 0.0002)
                || (vehBrakeLightSwitchV == 1 && vehBrakeLightSwitch == 1)
                || vehEPBSysBrkLghtsReqd ==1 || vehBrkSysBrkLghtsReqd == 1) {
            bit4 = 0x01;
        } else {
            bit4 = 0x00;
        }
        return bit4;
    }

    private static byte calcGearsBit3(int vehGearPosV, int vehGearPos) {
        /*
        IF vehGearPosV=1 && vehGearPos=8
        THEN Bit3~0=0xF
        ELSE IF vehGearPosV=1 && vehGearPos=5
        THEN Bit3~0=0xE
        ELSE IF vehGearPosV=1 && vehGearPos=7
        THEN Bit3~0=0xD
        ELSE Bit3~0=0x0
         */
        byte bit3;
        if (vehGearPosV == 1 && vehGearPos == 8) {
            bit3 = 0x0F;
        } else if (vehGearPosV == 1 && vehGearPos == 5) {
            bit3 = 0x0E;
        } else if (vehGearPosV == 1 && vehGearPos == 7) {
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
