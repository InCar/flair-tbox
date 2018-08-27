package com.incarcloud.saic.modes.OLD_BP34;

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
public class OLD_BP34x01Overview extends OracleX implements IOracleX01Overview {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_BP34x01Overview.class);

    @Override
    public GBx01Overview makeGBx01Overview(ResultSet rs) {
        GBx01Overview data = null;

        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);

            int EPTRdy = rs.getInt("EPTRdy");
            int PwrMdMstrRunCrkA = rs.getInt("PwrMdMstrRunCrkA");
            int BMSBscSta = rs.getInt("BMSBscSta");
            int ISGSpd = rs.getInt("ISGSpd");
            int HVDCDCSta = rs.getInt("HVDCDCSta");
            int TMActuToq = rs.getInt("TMActuToq");


            float BMSPackCrnt = rs.getFloat("BMSPackCrnt");
            float BMSPACKSOC = rs.getFloat("BMSPACKSOC");
            float VehSpdavgdrvn = rs.getFloat("VehSpdavgdrvn");
            float vehOdo = rs.getFloat("vehOdo");
            float BMSPackVol = rs.getFloat("BMSPackVol");
            float vehSpeed = rs.getFloat("vehSpeed");
            float BMSPtIsltnRstc = rs.getFloat("BMSPtIsltnRstc");


            data = new GBx01Overview(vin, tmGMT8);
            data.setVehicleStatus(calcVehicleStatus(EPTRdy, PwrMdMstrRunCrkA));
            data.setChargingStatus(calcChargingStatus(BMSBscSta, BMSPackCrnt, BMSPACKSOC));
            data.setPowerSource(calcRunningMode(ISGSpd));
            data.setSpeedKmH(calcSpeed(VehSpdavgdrvn));
            data.setMileageKm(calcTotalMileage(vehOdo));
            data.setVoltage(calcTotalVoltage(BMSPackVol));
            data.setCurrent(calcTotalCurrent(BMSPackCrnt));
            data.setSoc((byte) calcSOC(BMSPACKSOC));
            data.setDcdcOnOff(calcDcdcStatus(HVDCDCSta));
            data.setBit5(calcGearsBit5(TMActuToq));
            data.setBit4(calcGearsBit4(TMActuToq));
            data.setBit3(calcGearsBit3(vehSpeed));
            data.setResistancekOhm((int) calcInsulationResistance(BMSPtIsltnRstc));

        } catch (SQLException ex) {
            s_logger.error("OLD_BP34x01Overview.makeGBx01Overview() failed, {}", Helper.printStackTrace(ex));
        }

        return data;
    }


    private static byte calcVehicleStatus(int EPTRdy, int PwrMdMstrRunCrkA) {
        /*
        IF EPTRdy=0x1
        THEN 车辆状态=0x01
        ELSE IF PwrMdMstrRunCrkA=0x0
        THEN车辆状态=0x02
        ELSE 车辆状态=0x03"
         */
        if (EPTRdy == 1) {
            return 0x01;
        } else if (PwrMdMstrRunCrkA == 0) {
            return 0x02;
        }
        return 0x03;
    }

    private static byte calcChargingStatus(int BMSBscSta, float BMSPackCrnt, float BMSPACKSOC) {
        /*
         IF BMSBscSta=0xF
         THEN充电状态=0xFF
         ELSE IF BMSBscSta=0x6 || 0x7 || 0x12
         THEN 充电状态=0x01
         ELSE IF BMSBscSta=0x3 &&
         BMSPackCrnt<0
         THEN 充电状态=0x02
         ELSE IF BMSBscSta=0x9 || 0xA || 0x13 || (BMSPACKSOC=100)
         THEN 充电状态=0x04
         ELSE 充电状态=0x03"
         */
        if (BMSBscSta == 0x0F) {
            return (byte) 0xFF;
        } else if (BMSBscSta == 6 || BMSBscSta == 7 || BMSBscSta == 0x12) {
            return 0x01;
        } else if (BMSBscSta == 3 && BMSPackCrnt < 0) {
            return 0x02;
        } else if (BMSBscSta == 9 || BMSBscSta == 0x0A || BMSBscSta == 0x13 || BMSPACKSOC == 100) {
            return 0x04;
        }
        return 0x03;
    }

    private static byte calcRunningMode(int ISGSpd) {
        /*
        IF ISGSpd<100
        THEN 运行模式=0x01
        ELSE 运行模式=0x02"
         */
        if (ISGSpd < 100) {
            return 0x01;
        }
        return 0x02;
    }

    private static float calcSpeed(float VehSpdavgdrvn) {
        /*
         车速=VehSpdavgdrvn
         */
        return VehSpdavgdrvn;
    }

    private static float calcTotalMileage(float vehOdo) {
        /*
         累计里程=vehOdo
         */
        return vehOdo;
    }

    private static float calcTotalVoltage(float BMSPackVol) {
        /*
        IF BMSPackVol=1023.75 || 1023.5
        THEN 总电压=0xFF,0xFF
        ELSE总电压= BMSPackVol
         */
        if (BMSPackVol == 1023.75 || BMSPackVol == 1023.5) {
            return 0xFFFF;
        }
        return BMSPackVol;
    }

    private static float calcTotalCurrent(float BMSPackCrnt) {
        /*
        IF BMSPackCrnt=638.375 || 638.35
        THEN 总电流=0xFF,0xFF
        ELSE总电流= BMSPackCrnt"
         */
        if (BMSPackCrnt == 638.375 || BMSPackCrnt == 638.35) {
            return 0xFFFF;
        }
        return BMSPackCrnt;
    }

    private static float calcSOC(float BMSPACKSOC) {
        /*
         "IF BMSPACKSOC=127 || 127.5
          THEN SOC=0xFF
          ELSE SOC=BMSPACKSOC
         */
        if (BMSPACKSOC == 127 || BMSPACKSOC == 127.5) {
            return 0xFF;
        }
        return BMSPACKSOC;
    }

    private static byte calcDcdcStatus(int HVDCDCSta) {
        /*
        IF HVDCDCSta=0x2
        THEN DC-DC状态=0x01
        ELSE DC-DC状态=0x02"
         */
        if (HVDCDCSta == 2) {
            return 0x01;
        }
        return 0x02;
    }

    private static byte calcGearsBit5(int TMActuToq) {
        /*
        IF TMActuToq<511&& TMActuToq>0
        THEN Bit5=0x1
        ELSE Bit5=0x0
         */
        if (TMActuToq < 511 && TMActuToq > 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit4(int TMActuToq) {
        /*
        IF TMActuToq<511 && TMActuToq<0
        THEN Bit4=0x1
        ELSE Bit4=0x0
         */
        if (TMActuToq < 511 && TMActuToq < 0) {
            return 0x01;
        }
        return 0x00;
    }

    private static byte calcGearsBit3(float vehSpeed) {
        /*
        IF vehSpeed>0
        THEN Bit3~0=0xF
        ELSE Bit3~0=0xE"
         */
        if (vehSpeed > 0) {
            return 0x0F;
        }
        return 0x0E;
    }

    private static float calcInsulationResistance(float BMSPtIsltnRstc) {
        /*
         绝缘电阻=BMSPtIsltnRstc
         */
        return BMSPtIsltnRstc;
    }
}
