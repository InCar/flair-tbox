package com.incarcloud.saic.modes.OLD_IP24MCE;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx02Motor;
import com.incarcloud.saic.GB32960.Motor;
import com.incarcloud.saic.modes.OLD_BP34.OLD_BP34x01Overview;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX02Motor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Created by dave on 18-8-28 下午1:48.
 */
public class OLD_IP24MCEx02Motor extends OracleX implements IOracleX02Motor {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_BP34x01Overview.class);

    @Override
    public GBx02Motor makeGBx02Motor(ResultSet rs) {
        try {
            String vin = super.getVin(rs);
            ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(rs);
            GBx02Motor motor = new GBx02Motor(vin, tmGMT8);
            motor.setMotors(new ArrayList<>());

            // ============================== 1号电机
            Motor m = new Motor();
            // 驱动电机序号
            m.setMotorSeq((short) 1);

            float toq = rs.getFloat("TMTorqueActualHSC2");
            int sta = rs.getInt("TMStateHSC2");

            // 驱动电机状态
            if (toq == 511 || toq == 512) {
                m.setMotorStatus((short) 0xFF);
            } else if (sta == 3) {
                m.setMotorStatus((short) 0x04);
            } else if (sta == 6 || sta == 7 || sta == 8) {
                if (toq >= 0) {
                    m.setMotorStatus((short) 0x01);
                } else {
                    m.setMotorStatus((short) 0x02);
                }
            } else {
                m.setMotorStatus((short) 0x03);
            }

            // 驱动电机控制器温度
            int TMInvtTempHSC2 = rs.getInt("TMInvtTempHSC2");
            if (TMInvtTempHSC2 == 215 || TMInvtTempHSC2 == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (TMInvtTempHSC2 + 40));
            }

            // 驱动电机转速
            int TMSpeedHSC2 = rs.getInt("TMSpeedHSC2");
            if (TMSpeedHSC2 == 32678 || TMSpeedHSC2 == 32677 || TMSpeedHSC2 < -20000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(TMSpeedHSC2 + 20000);
            }

            // 驱动电机转矩
            if (toq == 511.5f || toq == 511f) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((toq + 2000) * 10);
            }

            // 驱动电机温度
            int TMTempHSC2 = rs.getInt("TMTempHSC2");
            if (TMTempHSC2 == 215 || TMTempHSC2 == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (TMTempHSC2 + 40));
            }

            // 电机控制器输入电压
            float DCVoltHVHSC2 = rs.getFloat("DCVoltHVHSC2");
            if (DCVoltHVHSC2 == 1023 || DCVoltHVHSC2 == 1022) {
                m.setControllerInputVoltage(0xFFFF);
            } else {
                m.setControllerInputVoltage((DCVoltHVHSC2 - 1) * 10);
            }

            // 电机控制器直流母线电流
            float BMSPackCurrentHSC2 = rs.getFloat("BMSPackCurrentHSC2");
            if (BMSPackCurrentHSC2 == 638.35f || BMSPackCurrentHSC2 == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0) {
                m.setControllerDirectCurrent((BMSPackCurrentHSC2 - 2 + 1000) * 10);
            } else if (toq < 0) {
                m.setControllerDirectCurrent((BMSPackCurrentHSC2 + 1000) * 10);
            } else {
                m.setControllerDirectCurrent(0);
            }

            motor.getMotors().add(m);


            // ============================== 2号电机
            m = new Motor();
            // 驱动电机序号
            m.setMotorSeq((short) 2);

            toq = rs.getInt("ISGTorqueActualHSC2");
            sta = rs.getInt("ISGStateHSC2");

            // 驱动电机状态
            if (toq == 511 || toq == 512) {
                m.setMotorStatus((short) 0xFF);
            } else if (sta == 3) {
                m.setMotorStatus((short) 0x04);
            } else if (sta == 6 || sta == 7 || sta == 8) {
                if (toq >= 0) {
                    m.setMotorStatus((short) 0x01);
                } else {
                    m.setMotorStatus((short) 0x02);
                }
            } else {
                m.setMotorStatus((short) 0x03);
            }

            // 驱动电机控制器温度
            int ISGInvtTempHSC2 = rs.getInt("ISGInvtTempHSC2");
            if (ISGInvtTempHSC2 == 215 || ISGInvtTempHSC2 == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (ISGInvtTempHSC2 + 40));
            }

            // 驱动电机转速
            int ISGSpeedHSC2 = rs.getInt("ISGSpeedHSC2");
            if (ISGSpeedHSC2 == 32678 || ISGSpeedHSC2 == 32677 || ISGSpeedHSC2 < -20000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(ISGSpeedHSC2 + 20000);
            }

            // 驱动电机转矩
            if (toq == 511.5f || toq == 511f) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((toq + 2000) * 10);
            }

            // 驱动电机温度
            int ISGTempHSC2 = rs.getInt("ISGTempHSC2");
            if (ISGTempHSC2 == 215 || ISGTempHSC2 == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (ISGTempHSC2 + 40));
            }

            // 电机控制器输入电压
            if (DCVoltHVHSC2 == 1023 || DCVoltHVHSC2 == 1022) {
                m.setControllerInputVoltage(0xFFFF);
            } else {
                m.setControllerInputVoltage((DCVoltHVHSC2 - 1) * 10);
            }

            // 电机控制器直流母线电流
            if (BMSPackCurrentHSC2 == 638.35f || BMSPackCurrentHSC2 == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0 && ISGSpeedHSC2 > 100) {
                m.setControllerDirectCurrent((BMSPackCurrentHSC2 - 1 + 1000) * 10);
            } else {
                m.setControllerDirectCurrent(0);
            }

            motor.getMotors().add(m);
            return motor;
        } catch (SQLException ex) {
            s_logger.error("get value failed, {}", Helper.printStackTrace(ex));
        }
        return null;
    }
}
