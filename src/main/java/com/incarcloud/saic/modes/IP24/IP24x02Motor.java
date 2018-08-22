package com.incarcloud.saic.modes.IP24;

import com.incarcloud.saic.GB32960.GBx02Motor;
import com.incarcloud.saic.GB32960.Motor;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX02Motor;
import org.bson.Document;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by dave on 18-8-22 上午11:12.
 */
public class IP24x02Motor extends MongoX implements IMongoX02Motor {

    @Override
    public GBx02Motor makeGBx02Motor(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        GBx02Motor motor = new GBx02Motor(vin, tmGMT8);
        motor.setMotors(new ArrayList<>());
        Stream.of("TM", "ISG", "SAM").forEach(code -> {
            Motor m = getMotor(bsonDoc, code);
            if (m != null) {
                motor.getMotors().add(m);
            }
        });
        return motor;
    }

    /**
     *
     * @param bsonDoc
     * @param code TM或ISG或SAM
     * @return
     */
    private static Motor getMotor(Document bsonDoc, String code) {
        String prefix = "veh" + code;
        if (bsonDoc.getString(prefix + "InvtrCrntV") == null) {
            return null;
        }

        Motor m = new Motor();
        // 驱动电机序号
        if ("TM".equals(code)) {
            m.setMotorSeq((byte) 1);
        } else if ("ISG".equals(code)) {
            m.setMotorSeq((byte) 2);
        } else if ("SAM".equals(code)) {
            m.setMotorSeq((byte) 3);
        } else {
            throw new RuntimeException("无效的code: " + code);
        }

        // 驱动电机状态
        float crnt = Float.parseFloat(bsonDoc.getString(prefix + "InvtrCrnt"));
        if (crnt == 1023 || crnt == 1024) {
            m.setMotorStatus((byte) 0xFF);
        } else {
            int sta = Integer.parseInt(bsonDoc.getString(prefix + "Sta"));
            if (sta == 3) {
                m.setMotorStatus((byte) 0x04);
            } else if (sta == 6 || sta == 7 || sta == 8) {
                if (crnt >= 0) {
                    m.setMotorStatus((byte) 0x01);
                } else {
                    m.setMotorStatus((byte) 0x02);
                }
            } else {
                m.setMotorStatus((byte) 0x03);
            }
        }

        // 驱动电机控制器温度
        int tem = Integer.parseInt(bsonDoc.getString(prefix + "InvtrTem"));
        if (tem == 215 || tem == 214) {
            m.setControllerTemperature((byte) 0xff);
        } else {
            m.setControllerTemperature((byte) tem);
        }

        // 驱动电机转速
        int speed = Integer.parseInt(bsonDoc.getString(prefix + "Spd"));
        m.setSpeed(speed == 32678 || speed == 32677 || speed < -20000 ? (byte) 0xFF : (byte) speed);

        // 驱动电机转矩
        float toq = Float.parseFloat(bsonDoc.getString(prefix + "ActuToq"));
        if (toq == 511.5 || toq == 511) {
            m.setTorque(0xFF);
        } else {
            m.setTorque(toq);
        }

        // 驱动电机温度
        byte mtem = (byte) Integer.parseInt(bsonDoc.getString(prefix + "SttrTem"));
        m.setMotorTemperature(mtem == (byte) 215 || mtem == (byte) 214 ? (byte) 0xFF : mtem);

        // 电机控制器输入电压
        float volthv = Float.parseFloat(bsonDoc.getString("vehDCVoltHV"));
        if (volthv == 1023 || volthv == 1022) {
            m.setControllerInputVoltage(0xFFFF);
        } else {
            m.setControllerInputVoltage(volthv);
        }

        // 电机控制器直流母线电流
        if (crnt == 1023 || crnt == 1024 || crnt < -1000) {
            m.setControllerDirectCurrent(0xFFFF);
        } else {
            m.setControllerDirectCurrent(crnt);
        }

        return m;
    }
}
