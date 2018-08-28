package com.incarcloud.saic.modes.OLD_BP34;

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
 * Created by dave on 18-8-27 下午4:11.
 */
public class OLD_BP34x02Motor extends OracleX implements IOracleX02Motor {
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

            int toq = rs.getInt("TMActuToq");
            int sta = rs.getInt("TMSta");

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
            int TMInvtrTem = rs.getInt("TMInvtrTem");
            if (TMInvtrTem == 215 || TMInvtrTem == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (TMInvtrTem + 40));
            }

            // 驱动电机转速
            int TMSpd = rs.getInt("TMSpd");
            if (TMSpd == 32728 || TMSpd == 32767 || TMSpd < -2000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(TMSpd + 2000);
            }

            // 驱动电机转矩
            float TMActuToq = rs.getFloat("TMActuToq");
            if (TMActuToq == 511.5f || TMActuToq == 511) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((TMActuToq + 2000) * 10);
            }

            // 驱动电机温度
            int TMSttrTem = rs.getInt("TMSttrTem");
            if (TMSttrTem == 215 || TMSttrTem == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (TMSttrTem + 40));
            }

            // 电机控制器输入电压
            m.setControllerInputVoltage(rs.getFloat("HVDCDCHVSideVol") * 10);

            // 电机控制器直流母线电流
            float BMSPackCrnt = rs.getFloat("BMSPackCrnt");
            if (BMSPackCrnt == 638.35f || BMSPackCrnt == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0) {
                m.setControllerDirectCurrent((BMSPackCrnt - 2 + 1000) * 10);
            } else if (toq < 0) {
                m.setControllerDirectCurrent((BMSPackCrnt + 1000) * 10);
            } else {
                m.setControllerDirectCurrent(0);
            }

            motor.getMotors().add(m);


            // ============================== 2号电机
            m = new Motor();
            // 驱动电机序号
            m.setMotorSeq((short) 1);

            toq = rs.getInt("ISGActuToq");
            sta = rs.getInt("ISGSta");

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
            int ISGInvtrTem = rs.getInt("ISGInvtrTem");
            if (ISGInvtrTem == 215 || ISGInvtrTem == 214) {
                m.setControllerTemperature((short) 0xFF);
            } else {
                m.setControllerTemperature((short) (ISGInvtrTem + 40));
            }

            // 驱动电机转速
            int ISGSpd = rs.getInt("ISGSpd");
            if (ISGSpd == 32728 || ISGSpd == 32767 || ISGSpd < -2000) {
                m.setSpeed(0xFF);
            } else {
                m.setSpeed(ISGSpd + 2000);
            }

            // 驱动电机转矩
            float ISGActuToq = rs.getFloat("ISGActuToq");
            if (ISGActuToq == 511.5f || ISGActuToq == 511) {
                m.setTorque(0xFF);
            } else {
                m.setTorque((ISGActuToq + 2000) * 10);
            }

            // 驱动电机温度
            int ISGSttrTem = rs.getInt("ISGSttrTem");
            if (ISGSttrTem == 215 || ISGSttrTem == 214) {
                m.setMotorTemperature((short) 0xFF);
            } else {
                m.setMotorTemperature((short) (ISGSttrTem + 40));
            }

            // 电机控制器输入电压
            m.setControllerInputVoltage(rs.getFloat("HVDCDCHVSideVol") * 10);

            // 电机控制器直流母线电流
            BMSPackCrnt = rs.getFloat("BMSPackCrnt");
            if (BMSPackCrnt == 638.35f || BMSPackCrnt == 638.375f) {
                m.setControllerDirectCurrent(0xFFFF);
            } else if (toq > 0 && ISGSpd > 100) {
                m.setControllerDirectCurrent((BMSPackCrnt - 1 + 1000) * 10);
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
