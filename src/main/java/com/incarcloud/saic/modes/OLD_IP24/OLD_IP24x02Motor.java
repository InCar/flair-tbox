package com.incarcloud.saic.modes.OLD_IP24;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.saic.GB32960.GBx02Motor;
import com.incarcloud.saic.GB32960.Motor;
import com.incarcloud.saic.modes.OracleX;
import com.incarcloud.saic.modes.oracle.IOracleX02Motor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Created by dave on 18-8-28 上午10:00.
 */
public class OLD_IP24x02Motor extends OracleX implements IOracleX02Motor {
    private static final Logger s_logger = LoggerFactory.getLogger(OLD_IP24x02Motor.class);

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

            float toq = rs.getFloat("TMTorqueActual");
            int sta = rs.getInt("TMState");

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
            int TMInvtTemp = rs.getInt("TMInvtTemp");
            if (TMInvtTemp == 215 || TMInvtTemp == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (TMInvtTemp + 40));
            }

            // 驱动电机转速
            int TMSpeed = rs.getInt("TMSpeed");
            if (TMSpeed == 32678 || TMSpeed == 32677 || TMSpeed < -20000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(TMSpeed + 20000);
            }

            // 驱动电机转矩
            if (toq == 511.5f || toq == 511f) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((toq + 2000) * 10);
            }

            // 驱动电机温度
            int TMTemp = rs.getInt("TMTemp");
            if (TMTemp == 215 || TMTemp == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (TMTemp + 40));
            }

            // 电机控制器输入电压
            float DCVoltHV = rs.getFloat("DCVoltHV");
            if (DCVoltHV == 1023 || DCVoltHV == 1022) {
                m.setControllerInputVoltage(0xFFFF);
            } else {
                m.setControllerInputVoltage((DCVoltHV - 1) * 10);
            }

            // 电机控制器直流母线电流
            float BMSPackCurrent = rs.getFloat("BMSPackCurrent");
            if (BMSPackCurrent == 638.35f || BMSPackCurrent == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0) {
                m.setControllerDirectCurrent((BMSPackCurrent - 2 + 1000) * 10);
            } else if (toq < 0) {
                m.setControllerDirectCurrent((BMSPackCurrent + 1000) * 10);
            } else {
                m.setControllerDirectCurrent(0);
            }

            motor.getMotors().add(m);


            // ============================== 2号电机
            m = new Motor();
            // 驱动电机序号
            m.setMotorSeq((short) 2);

            toq = rs.getInt("ISGTorqueActual");
            sta = rs.getInt("ISGState");

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
            int ISGInvtTemp = rs.getInt("ISGInvtTemp");
            if (ISGInvtTemp == 215 || ISGInvtTemp == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (ISGInvtTemp + 40));
            }

            // 驱动电机转速
            int ISGSpeed = rs.getInt("ISGSpeed");
            if (ISGSpeed == 32678 || ISGSpeed == 32677 || ISGSpeed < -20000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(ISGSpeed + 20000);
            }

            // 驱动电机转矩
            if (toq == 511.5f || toq == 511) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((toq + 2000) * 10);
            }

            // 驱动电机温度
            int ISGTemp = rs.getInt("ISGTemp");
            if (ISGTemp == 215 || ISGTemp == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (ISGTemp + 40));
            }

            // 电机控制器输入电压
            if (DCVoltHV == 1023 || DCVoltHV == 1022) {
                m.setControllerInputVoltage(0xFFFF);
            } else {
                m.setControllerInputVoltage((DCVoltHV - 1) * 10);
            }

            // 电机控制器直流母线电流
            if (BMSPackCurrent == 638.3f || BMSPackCurrent == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0 && ISGSpeed > 100) {
                m.setControllerDirectCurrent((BMSPackCurrent - 1 + 1000) * 10);
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
