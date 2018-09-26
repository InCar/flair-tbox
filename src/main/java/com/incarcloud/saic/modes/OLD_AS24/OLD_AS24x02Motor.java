package com.incarcloud.saic.modes.OLD_AS24;

import com.incarcloud.saic.GB32960.GBx02Motor;
import com.incarcloud.saic.GB32960.Motor;
import com.incarcloud.saic.modes.MongoX;
import com.incarcloud.saic.modes.mongo.IMongoX02Motor;
import org.bson.Document;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Created by dave on 18-8-21 下午2:07.
 */
public class OLD_AS24x02Motor extends MongoX implements IMongoX02Motor {


    @Override
    public GBx02Motor makeGBx02Motor(Document bsonDoc) {
        String vin = super.getVin(bsonDoc);
        ZonedDateTime tmGMT8 = super.getZonedDateTimeGMT8(bsonDoc);
        GBx02Motor motor = new GBx02Motor(vin, tmGMT8);
        motor.setMotors(new ArrayList<>());

        // ============================== 1号电机
        Motor m = new Motor();
        // 驱动电机序号
        m.setMotorSeq((short) 1);

        float torque = parseFloatWithDef(bsonDoc, "TMActuToq");
        float sta = parseFloatWithDef(bsonDoc, "TMSta");

        // 驱动电机状态
        if (torque == 511 || torque == 512) {
            m.setMotorStatus((short) 0xFF);
        } else if (sta == 3) {
            m.setMotorStatus((short) 0x04);
        } else if (sta == 6 || sta == 7 || sta == 8) {
            if (torque >= 0) {
                m.setMotorStatus((short) 0x01);
            } else {
                m.setMotorStatus((short) 0x02);
            }
        } else {
            m.setMotorStatus((short) 0x03);
        }

        // 驱动电机控制器温度
        String tem = bsonDoc.getString("vehTMInvtrTem");
        if (StringUtils.isEmpty(tem) || "NULL".equalsIgnoreCase(tem)) {
            m.setControllerTemperature((short) 0xFF);
        } else {
            m.setControllerTemperature(Short.parseShort(tem));
        }

        // 驱动电机转速
        String speed = bsonDoc.getString("vehTMSpd");
        if (StringUtils.isEmpty(speed) || "NULL".equalsIgnoreCase(speed)) {
            m.setSpeed(0xFF);
        } else {
            m.setSpeed((int)Float.parseFloat(speed));
        }

        // 驱动电机转矩
        if (torque == 511.5f || torque == 511) {
            m.setTorque(0xFF);
        } else {
            m.setTorque(torque);
        }

        // 驱动电机温度
        String stem = bsonDoc.getString("vehTMSttrTem");
        if (StringUtils.isEmpty(stem) || "NULL".equalsIgnoreCase(speed)) {
            m.setMotorTemperature((short) 0xFF);
        } else {
            m.setMotorTemperature(Short.parseShort(stem));
        }

        // 电机控制器输入电压
        m.setControllerInputVoltage(Float.parseFloat(bsonDoc.getString("vehHVDCDCHVSideVol")));


        // 电机控制器直流母线电流
        float crnt = parseFloatWithDef(bsonDoc, "BMSPackCrnt");
        if (crnt == 638.35f || crnt == 638.375f) {
            m.setControllerDirectCurrent(0xFFFF);
        } else if (torque > 0) {
            m.setControllerDirectCurrent(crnt - 2);
        } else if (torque < 0) {
            m.setControllerDirectCurrent(crnt);
        } else {
            m.setControllerDirectCurrent(0);
        }

        motor.getMotors().add(m);

        // ============================ 2号电机

        m = new Motor();
        // 驱动电机序号
        m.setMotorSeq((short) 2);

        // 驱动电机状态
        torque = Float.parseFloat(bsonDoc.getString("vehISGActuToq"));
        sta = Float.parseFloat(bsonDoc.getString("vehISGSta"));
        if (torque == 511 || torque == 512) {
            m.setMotorStatus((short) 0xFF);
        } else if (sta == 3) {
            m.setMotorStatus((short) 0x04);
        } else if (sta == 6 || sta == 7 || sta == 8) {
            if (torque >= 0) {
                m.setMotorStatus((short) 0x01);
            } else {
                m.setMotorStatus((short) 0x02);
            }
        } else {
            m.setMotorStatus((short) 0x03);
        }

        // 驱动电机控制器温度
        int item = parseIntWithDef(bsonDoc, "vehISGInvtrTem");
        if (item == 215 || item == 214) {
            m.setControllerTemperature((short) 0xFF);
        } else {
            m.setControllerTemperature((short) item);
        }

        // 驱动电机转速
        int ispeed = parseIntWithDef(bsonDoc, "vehISGSpd");
        if (ispeed == 32728 || ispeed == 32767 || ispeed < -20000) {
            m.setSpeed(0xFF);
        } else {
            m.setSpeed(ispeed);
        }

        // 驱动电机转矩
        if (torque == 511.5f || torque == 511) {
            m.setTorque(0xFF);
        } else {
            m.setTorque(torque);
        }

        // 驱动电机温度
        int tem2 = parseIntWithDef(bsonDoc, "vehISGSttrTem");
        if (tem2 == 215 || tem2 == 214) {
            m.setMotorTemperature((short) 0xFF);
        } else {
            m.setMotorTemperature((short) tem2);
        }

        // 电机控制器输入电压
        m.setControllerInputVoltage(Float.parseFloat(bsonDoc.getString("vehHVDCDCHVSideVol")));

        // 电机控制器直流母线电流
        crnt = parseFloatWithDef(bsonDoc, "vehBMSPackCrnt");
        if (crnt == 638.35f || crnt == 638.375f) {
            m.setControllerDirectCurrent(0xFFFF);
        } else if (torque > 0 && ispeed > 100) {
            m.setControllerDirectCurrent(crnt - 1);
        } else {
            m.setControllerDirectCurrent(0);
        }

        motor.getMotors().add(m);
        return motor;
    }
}
