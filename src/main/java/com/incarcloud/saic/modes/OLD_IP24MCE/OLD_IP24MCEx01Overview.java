package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx01Overview;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX01Overview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * @author xy
 */
public class OLD_IP24MCEx01Overview extends OracleX implements IOracleX01Overview {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24MCEx01Overview.class);

    public GBx01Overview makeGBx01Overview(ResultSet rs) {
        GBx01Overview data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int PTReady = rs.getInt("PTReady");
            int KeySwitchStateIGNdMSC = rs.getInt("KeySwitchStateIGNdMSC");
            int BMSBasicStatHSC2 = rs.getInt("BMSBasicStatHSC2");
            int HEVSystemModeMSC = rs.getInt("HEVSystemModeMSC");
            int DCStateHSC2 = rs.getInt("DCStateHSC2");
            int TMTorqueActualHSC2 = rs.getInt("TMTorqueActualHSC2");
            int GearShiftPosnValidMSC = rs.getInt("GearShiftPosnValidMSC");
            int GearShiftPosnMSC = rs.getInt("GearShiftPosnMSC");

            float VehicleSpeedMSC = rs.getFloat("VehicleSpeedMSC");
            float ODO_PrimaryMSC = rs.getFloat("ODO_PrimaryMSC");
            float BMSPackVoltHSC2 = rs.getFloat("BMSPackVoltHSC2");
            float BMSPackCurrentHSC2 = rs.getFloat("BMSPackCurrentHSC2");
            float BMSPackSOCHSC2 = rs.getFloat("BMSPackSOCHSC2");
            float BMSPTIsolationHSC2 = rs.getFloat("BMSPTIsolationHSC2");


            data = new GBx01Overview(vin, tmGMT8);
            data.setVehicleStatus(calcVehicleStatus(PTReady, KeySwitchStateIGNdMSC));
            data.setChargingStatus(calcChargingStatus(BMSBasicStatHSC2, BMSPackCurrentHSC2, BMSPackSOCHSC2));
            data.setPowerSource(calcRunningMode(HEVSystemModeMSC));
            data.setSpeedKmH(calcSpeed(VehicleSpeedMSC));
            data.setMileageKm(calcTotalMileage(ODO_PrimaryMSC));
            data.setVoltage(calcTotalVoltage(BMSPackVoltHSC2));
            data.setCurrent(calcTotalCurrent(BMSPackCurrentHSC2));
            data.setSoc((byte) calcSOC(BMSPackSOCHSC2));
            data.setDcdcOnOff(calcDcdcStatus(DCStateHSC2));
            data.setBit5(calcGearsBit5(TMTorqueActualHSC2));
            data.setBit4(calcGearsBit4(TMTorqueActualHSC2));
            data.setBit3(calcGearsBit3(GearShiftPosnValidMSC, GearShiftPosnMSC));
            data.setResistancekOhm((int) calcInsulationResistance(BMSPTIsolationHSC2));

        } catch (SQLException ex) {
            s_logger.error("OLD_IP24MCEx01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }

    private static byte calcVehicleStatus(int PTReady, int KeySwitchStateIGNdMSC) {
        /*
         IF PTReady=1
         THEN 车辆状态=0x01
         ELSE IF KeySwitchStateIGNdMSC=0
         THEN 车辆状态=0x02
         ELSE 车辆状态=0x03
         */
        if (PTReady == 1) {
            return 0x01;
        } else if (KeySwitchStateIGNdMSC == 0) {
            return 0x02;
        }
        return 0x03;
    }

    private static byte calcChargingStatus(int BMSBasicStatHSC2, float BMSPackCurrentHSC2, float BMSPackSOCHSC2) {
        /*
         IF BMSBasicStatHSC2=15
         THEN 充电状态=0xFF
         ELSE IF BMSBasicStatHSC2=6 || 7
         THEN 充电状态=0x01
         ELSE IF BMSBasicStatHSC2=3 &&
         BMSPackCurrentHSC2<0
         THEN 充电状态=0x02
         ELSE IF BMSBasicStatHSC2=9 || 10 || BMSPackSOCHSC2=100
         THEN 充电状态=0x04
         ELSE 充电状态=0x03
         */
        if (BMSBasicStatHSC2 == 15) {
            return (byte) 0xFF;
        } else if (BMSBasicStatHSC2 == 6 || BMSBasicStatHSC2 == 7) {
            return 0x01;
        } else if (BMSBasicStatHSC2 == 3 && BMSPackCurrentHSC2 < 0) {
            return 0x02;
        } else if (BMSBasicStatHSC2 == 9 || BMSBasicStatHSC2 == 10 || BMSPackSOCHSC2 == 100) {
            return 0x04;
        }
        return 0x03;
    }

    private static byte calcRunningMode(int HEVSystemModeMSC) {
        /*
        IF HEVSystemModeMSC=3 || 7 || 8
         THEN 运行模式=0x01
         ELSE IF HEVSystemModeMSC=0 || 1 || 2 || 5 || 6
         THEN运行模式=0x02
         ELSE IF HEVSystemModeMSC=4
         THEN运行模式=0x03
         ELSE运行模式=0xFF
         */
        if (HEVSystemModeMSC == 3 || HEVSystemModeMSC == 7 || HEVSystemModeMSC == 8) {
            return 0x01;
        } else if (HEVSystemModeMSC == 0 || HEVSystemModeMSC == 1 || HEVSystemModeMSC == 2
                || HEVSystemModeMSC == 5 || HEVSystemModeMSC == 6) {
            return 0x02;
        } else if (HEVSystemModeMSC == 4) {
            return 0x03;
        }
        return (byte) 0xFF;
    }

    private static float calcSpeed(float VehicleSpeedMSC) {
        /*
         车速=VehicleSpeedMSC
         */
        return VehicleSpeedMSC;
    }

    private static float calcTotalMileage(float ODO_PrimaryMSC) {
        /*
         累计里程=ODO_PrimaryMSC
         */
        return ODO_PrimaryMSC;
    }

    private static float calcTotalVoltage(float BMSPackVoltHSC2) {
        /*
         IF BMSPackVoltHSC2=1023.5 || 1023.75
         THEN 总电压=0xFF,0xFF
         ELSE 总电压=BMSPackVoltHSC2"
         */
        if (BMSPackVoltHSC2 == 1023.5 || BMSPackVoltHSC2 == 1023.75) {
            return 0xFFFF;
        }
        return BMSPackVoltHSC2;
    }

    private static float calcTotalCurrent(float BMSPackCurrentHSC2) {
        /*
         IF BMSPackCurrentHSC2=638.35 || 638.375
         THEN 总电流=0xFF,0xFF
         ELSE 总电流= BMSPackCurrentHSC2"
         */
        if (BMSPackCurrentHSC2 == 638.35 || BMSPackCurrentHSC2 == 638.375) {
            return 0xFFFF;
        }
        return BMSPackCurrentHSC2;
    }

    private static float calcSOC(float BMSPackSOCHSC2) {
        /*
         IF BMSPackSOCHSC2=102.2 || 102.3
         THEN SOC=0xFF
         ELSE SOC=BMSPackSOCHSC2"
         */
        if (BMSPackSOCHSC2 == 102.2 || BMSPackSOCHSC2 == 102.3) {
            return 0xFF;
        }
        return BMSPackSOCHSC2;
    }

    private static byte calcDcdcStatus(int DCStateHSC2) {
        /*
         IF DCStateHSC2=2
         THEN DC-DC 状态=0x01
         ELSE DC-DC 状态=0x02"
         */
        if (DCStateHSC2 == 2) {
            return 0x01;
        }
        return 0x02;
    }

    private static byte calcGearsBit5(int TMTorqueActualHSC2) {
        /*
         IF TMTorqueActualHSC2<511 && TMTorqueActualHSC2>0
         THEN Bit5=0x1
         ELSE Bit5=0x0
         */
        if (TMTorqueActualHSC2 < 511 && TMTorqueActualHSC2 > 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit4(int TMTorqueActualHSC2) {
        /*
         IF TMTorqueActualHSC2<511 && TMTorqueActualHSC2<0
         THEN Bit4=0x1
         ELSE Bit4=0x0
         */
        if (TMTorqueActualHSC2 < 511 && TMTorqueActualHSC2 < 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit3(int GearShiftPosnValidMSC, int GearShiftPosnMSC) {
        /*
         IF GearShiftPosnValidMSC=1 && GearShiftPosnMSC=8
         THEN Bit3~0=0xF
         ELSE IF GearShiftPosnValidMSC=1 && GearShiftPosnMSC=5
         THEN Bit3~0=0xE
         ELSE IF GearShiftPosnValidMSC=1 && GearShiftPosnMSC=7
         THEN Bit3~0=0xD
         ELSE Bit3~0=0x0
         */
        if (GearShiftPosnValidMSC == 1 && GearShiftPosnMSC == 8) {
            return 0x0F;
        } else if (GearShiftPosnValidMSC == 1 && GearShiftPosnMSC == 5) {
            return 0x0E;
        } else if (GearShiftPosnValidMSC == 1 && GearShiftPosnMSC == 7) {
            return 0x0D;
        }
        return 0x00;
    }

    private static float calcInsulationResistance(float BMSPTIsolationHSC2) {
        /*
         绝缘电阻=BMSPTIsolationHSC2
         */
        return BMSPTIsolationHSC2;
    }

}
