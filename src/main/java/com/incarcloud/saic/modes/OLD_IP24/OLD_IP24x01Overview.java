package com.incarcloud.saic.modes.OLD_IP24;

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
public class OLD_IP24x01Overview extends OracleX implements IOracleX01Overview {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24x01Overview.class);

    @Override
    public GBx01Overview makeGBx01Overview(ResultSet rs) {
        GBx01Overview data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int PTReady = rs.getInt("PTReady");
            int KeySwitchStateIGN = rs.getInt("KeySwitchStateIGN");
            int BMSBasicStat = rs.getInt("BMSBasicStat");
            int HEVSystemMode = rs.getInt("HEVSystemMode");
            int DCState = rs.getInt("DCState");
            int TMTorqueActual = rs.getInt("TMTorqueActual");
            int GearShiftPosnValid = rs.getInt("GearShiftPosnValid");
            int GearShiftPosn = rs.getInt("GearShiftPosn");

            float VehicleSpeedHSC = rs.getFloat("VehicleSpeedHSC");
            float ODO_Primary = rs.getFloat("ODO_Primary");
            float BMSPackVolt = rs.getFloat("BMSPackVolt");
            float BMSPackCurrent = rs.getFloat("BMSPackCurrent");
            float BMSPackSOC = rs.getFloat("BMSPackSOC");
            float BMSPTIsolation = rs.getFloat("BMSPTIsolation");


            data = new GBx01Overview(vin, tmGMT8);
            data.setVehicleStatus(calcVehicleStatus(PTReady, KeySwitchStateIGN));
            data.setChargingStatus(calcChargingStatus(BMSBasicStat, BMSPackCurrent, BMSPackSOC));
            data.setPowerSource(calcRunningMode(HEVSystemMode));
            data.setSpeedKmH(calcSpeed(VehicleSpeedHSC));
            data.setMileageKm(calcTotalMileage(ODO_Primary));
            data.setVoltage(calcTotalVoltage(BMSPackVolt));
            data.setCurrent(calcTotalCurrent(BMSPackCurrent));
            data.setSoc((byte) calcSOC(BMSPackSOC));
            data.setDcdcOnOff(calcDcdcStatus(DCState));
            data.setBit5(calcGearsBit5(TMTorqueActual));
            data.setBit4(calcGearsBit4(TMTorqueActual));
            data.setBit3(calcGearsBit3(GearShiftPosnValid, GearShiftPosn));
            data.setResistancekOhm((int) calcInsulationResistance(BMSPTIsolation));

        } catch (SQLException ex) {
            s_logger.error("OLD_IP24x01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }

    private static byte calcVehicleStatus(int PTReady, int KeySwitchStateIGN) {
        /*
         IF PTReady=1
         THEN 车辆状态=0x01
         ELSE IF KeySwitchStateIGN=0
         THEN 车辆状态=0x02
         ELSE 车辆状态=0x03
         */
        if (PTReady == 1) {
            return 0x01;
        } else if (KeySwitchStateIGN == 0) {
            return 0x02;
        }
        return 0x03;
    }

    private static byte calcChargingStatus(int BMSBasicStat, float BMSPackCurrent, float BMSPackSOC) {
        /*
         IF BMSBasicStat=15
         THEN 充电状态=0xFF
         ELSE IF BMSBasicStat=6 || 7
         THEN 充电状态=0x01
         ELSE IF BMSBasicStat=3 &&
         BMSPackCurrent<0
         THEN 充电状态=0x02
         ELSE IF BMSBasicStat=9 || 10 || BMSPackSOC=100
         THEN 充电状态=0x04
         ELSE 充电状态=0x03
         */
        if (BMSBasicStat == 15) {
            return (byte) 0xFF;
        } else if (BMSBasicStat == 6 || BMSBasicStat == 7) {
            return 0x01;
        } else if (BMSBasicStat == 3 && BMSPackCurrent < 0) {
            return 0x02;
        } else if (BMSBasicStat == 9 || BMSBasicStat == 10 || BMSPackSOC == 100) {
            return 0x04;
        }
        return 0x03;
    }

    private static byte calcRunningMode(int HEVSystemMode) {
        /*
        IF HEVSystemMode=3 || 7 || 8
         THEN 运行模式=0x01
         ELSE IF HEVSystemMode=0 || 1 || 2 || 5 || 6
         THEN运行模式=0x02
         ELSE IF HEVSystemMode=4
         THEN运行模式=0x03
         ELSE运行模式=0xFF
         */
        if (HEVSystemMode == 3 || HEVSystemMode == 7 || HEVSystemMode == 8) {
            return 0x01;
        } else if (HEVSystemMode == 0 || HEVSystemMode == 1 || HEVSystemMode == 2
                || HEVSystemMode == 5 || HEVSystemMode == 6) {
            return 0x02;
        } else if (HEVSystemMode == 4) {
            return 0x03;
        }
        return (byte) 0xFF;
    }

    private static float calcSpeed(float VehicleSpeedHSC) {
        /*
         车速=VehicleSpeedHSC
         */
        return VehicleSpeedHSC;
    }

    private static float calcTotalMileage(float ODO_Primary) {
        /*
         累计里程=ODO_Primary
         */
        return ODO_Primary;
    }

    private static float calcTotalVoltage(float BMSPackVolt) {
        /*
         IF BMSPackVolt=1023.5 || 1023.75
         THEN 总电压=0xFF,0xFF
         ELSE 总电压=BMSPackVolt"
         */
        if (BMSPackVolt == 1023.5 || BMSPackVolt == 1023.75) {
            return 0xFFFF;
        }
        return BMSPackVolt;
    }

    private static float calcTotalCurrent(float BMSPackCurrent) {
        /*
         IF BMSPackCurrent=638.35 || 638.375
         THEN 总电流=0xFF,0xFF
         ELSE 总电流= BMSPackCurrent"
         */
        if (BMSPackCurrent == 638.35 || BMSPackCurrent == 638.375) {
            return 0xFFFF;
        }
        return BMSPackCurrent;
    }

    private static float calcSOC(float BMSPackSOC) {
        /*
         IF BMSPackSOC=102.2 || 102.3
         THEN SOC=0xFF
         ELSE SOC=BMSPackSOC"
         */
        if (BMSPackSOC == 102.2 || BMSPackSOC == 102.3) {
            return 0xFF;
        }
        return BMSPackSOC;
    }

    private static byte calcDcdcStatus(int DCState) {
        /*
         IF DCState=2
         THEN DC-DC 状态=0x01
         ELSE DC-DC 状态=0x02"
         */
        if (DCState == 2) {
            return 0x01;
        }
        return 0x02;
    }

    private static byte calcGearsBit5(int TMTorqueActual) {
        /*
         IF TMTorqueActual<511 && TMTorqueActual>0
         THEN Bit5=0x1
         ELSE Bit5=0x0
         */
        if (TMTorqueActual < 511 && TMTorqueActual > 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit4(int TMTorqueActual) {
        /*
         IF TMTorqueActual<511 && TMTorqueActual<0
         THEN Bit4=0x1
         ELSE Bit4=0x0
         */
        if (TMTorqueActual < 511 && TMTorqueActual < 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit3(int GearShiftPosnValid, int GearShiftPosn) {
        /*
         IF GearShiftPosnValid=1 && GearShiftPosn=8
         THEN Bit3~0=0xF
         ELSE IF GearShiftPosnValid=1 && GearShiftPosn=5
         THEN Bit3~0=0xE
         ELSE IF GearShiftPosnValid=1 && GearShiftPosn=7
         THEN Bit3~0=0xD
         ELSE Bit3~0=0x0
         */
        if (GearShiftPosnValid == 1 && GearShiftPosn == 8) {
            return 0x0F;
        } else if (GearShiftPosnValid == 1 && GearShiftPosn == 5) {
            return 0x0E;
        } else if (GearShiftPosnValid == 1 && GearShiftPosn == 7) {
            return 0x0D;
        }
        return 0x00;
    }

    private static float calcInsulationResistance(float BMSPTIsolation) {
        /*
         绝缘电阻=BMSPTIsolation
         */
        return BMSPTIsolation;
    }
}
