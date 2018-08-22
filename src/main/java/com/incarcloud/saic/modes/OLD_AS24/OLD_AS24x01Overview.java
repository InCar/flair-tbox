package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX01Overview;
import org.bson.Document;

import java.time.ZonedDateTime;

/**
 * @author xy
 */
public class OLD_AS24x01Overview extends MongoX implements IMongoX01Overview {

    @Override
    public GBx01Overview makeGBx01Overview(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);

        int workModel = Integer.parseInt(bsonDoc.getString("workModel"));

        float vehSpeed = Float.parseFloat(bsonDoc.getString("vehSpeed"));

        float vehOdo = Float.parseFloat(bsonDoc.getString("vehOdo"));

        float vehBMSPackVol = Float.parseFloat(bsonDoc.getString("vehBMSPackVol"));

        float vehBMSPackCrnt = Float.parseFloat((bsonDoc.getString("vehBMSPackCrnt")));

        float vehBMSPackSOC = Float.parseFloat(bsonDoc.getString("vehBMSPackSOC"));

        int vehHVDCDCSta = Integer.parseInt(bsonDoc.getString("vehHVDCDCSta"));

        int TMActuToq = Integer.parseInt(bsonDoc.getString("TMActuToq"));

        float vehBMSPtIsltnRstc = Float.parseFloat(bsonDoc.getString("vehBMSPtIsltnRstc"));

        GBx01Overview data = new GBx01Overview(vin, tmGMT8);
        data.setVehicleStatus(calcVehicleStatus(workModel));
        data.setChargingStatus(calcChargingStatus(workModel));
        data.setPowerSource(calcRunningMode());
        data.setSpeedKmH(calcSpeed(vehSpeed));
        data.setMileageKm(calcTotalMileage(vehOdo));
        data.setVoltage(calcTotalVoltage(vehBMSPackVol));
        data.setCurrent(calcTotalCurrent(vehBMSPackCrnt));
        data.setSoc((byte) calcSOC(vehBMSPackSOC));
        data.setDcdcOnOff(calcDcdcStatus(vehHVDCDCSta));
        data.setBit5(calcGearsBit5(TMActuToq));
        data.setBit4(calcGearsBit4(TMActuToq));
        data.setBit3(calcGearsBit3(vehSpeed));
        data.setResistancekOhm((int) calcInsulationResistance(vehBMSPtIsltnRstc));

        return data;
    }


    private static byte calcVehicleStatus(int workModel) {
        /*
        IF workModel=0x1
        THEN 车辆状态=0x01
        if  workModel=0x9
        then 车辆状态=0x02
        ELSE 车辆状态=0x03
         */
        if (workModel == 1) {
            return 0x01;
        } else if (workModel == 9) {
            return 0x02;
        } else {
            return 0x03;
        }
    }

    private static byte calcChargingStatus(int workModel) {
        /*
        IF workModel=0x2
        THEN 车辆状态=0x01
        ELSE 车辆状态=0x03
         */
        if (workModel == 2) {
            return 0x01;
        } else {
            return 0x03;
        }
    }

    private static byte calcRunningMode() {
        /*
        运行模式=02
         */
        return 0x02;
    }

    private static float calcSpeed(float vehSpeed) {
        /*
        车速=vehSpeed
         */
        return vehSpeed;
    }

    private static float calcTotalMileage(float vehOdo) {
        /*
      累计里程=vehOdo
         */
        return vehOdo;
    }

    private static float calcTotalVoltage(float vehBMSPackVol) {
        /*
        IF vehBMSPackVol=1023.75 || 1023.5
        THEN 总电压=0xFF,0xFF
        ELSE总电压= vehBMSPackVol
         */
        if (vehBMSPackVol == 1023.75 || vehBMSPackVol == 1023.5) {
            return 0xFFFF;
        } else {
            return vehBMSPackVol;
        }
    }

    private static float calcTotalCurrent(float vehBMSPackCrnt) {
        /*
        IF vehBMSPackCrnt=638.375 || 638.35 THEN 总电流=0xFF,0xFF
        ELSE总电流= vehBMSPackCrnt
         */
        if (vehBMSPackCrnt == 638.375 || vehBMSPackCrnt == 638.35) {
            return 0xFFFF;
        } else {
            return vehBMSPackCrnt;
        }
    }

    private static float calcSOC(float vehBMSPackSOC) {
        /*
        IF vehBMSPackSOC=102 || 102.3
        THEN SOC=0xFF
        ELSE SOC=vehBMSPackSOC
         */
        if (vehBMSPackSOC == 102 || vehBMSPackSOC == 102.3) {
            return 0xFF;
        } else {
            return vehBMSPackSOC;
        }
    }

    private static byte calcDcdcStatus(int vehHVDCDCSta) {
        /*
        IF vehHVDCDCSta=0x2
        THEN DC-DC状态=0x01
        ELSE DC-DC状态=0x02
         */
        if (vehHVDCDCSta == 2) {
            return 0x01;
        } else {
            return 0x02;
        }
    }

    private static byte calcGearsBit5(int TMActuToq) {
        /*
        IF TMActuToq<511&& TMActuToq>0
        THEN Bit5=0x1
        ELSE Bit5=0x0
         */
        if (TMActuToq < 511 && TMActuToq > 0) {
            return 0x01;
        } else {
            return 0x00;
        }
    }

    private static byte calcGearsBit4(int TMActuToq) {
        /*
        IF TMActuToq<511 && TMActuToq<0
        THEN Bit4=0x1
        ELSE Bit4=0x0
         */
        if (TMActuToq < 511 && TMActuToq < 0) {
            return 0x01;
        } else {
            return 0x00;
        }
    }

    private static byte calcGearsBit3(float vehSpeed) {
        /*
        IF vehSpeed>0
        THEN Bit3~0=0xF
        ELSE Bit3~0=0xE
         */
        if (vehSpeed > 0) {
            return 0x0F;
        } else {
            return 0x0E;
        }
    }

    private static float calcInsulationResistance(float vehBMSPtIsltnRstc) {
        /*
        绝缘电阻=vehBMSPtIsltnRstc
         */
        return vehBMSPtIsltnRstc;
    }
}
