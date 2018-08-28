package com.incarcloud.saic.GB32960;

import com.incarcloud.saic.utils.FloatUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 国标GB32960 0x02驱动电机
 */
public class GBx02Motor extends GBData {
    public GBx02Motor(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    private List<Motor> motors;

    public List<Motor> getMotors() {
        return motors;
    }
    public void setMotors(List<Motor> motors) {
        this.motors = motors;
    }

    @Override
    public int calcGBFrameSize(){
        return 12 * motors.size() + 2;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException{
        stream.writeByte(0x02);
        stream.writeByte(motors.size());

        for(Motor motor : motors){
            short motorSeq = motor.getMotorSeq();
            stream.writeByte(motorSeq);//驱动电机序号，有效值范围1~253

            short motorStatus = motor.getMotorStatus();
            stream.writeByte(motorStatus);//驱动电机状态：0x01-耗电，0x02-发电，0x03-关闭状态，0x04-准备状态，0xFF表示异常，0xFE表示无效

            short controllerTemperature = motor.getControllerTemperature();
            if(controllerTemperature != 0xFF && controllerTemperature != 0xFE){
                stream.writeByte(controllerTemperature + 40);//驱动电机控制器温度，有效值范围：0～250 （数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
            }else{
                stream.writeByte(controllerTemperature);
            }

            int speed = motor.getSpeed();
            if(speed != 0xFFFF && speed != 0xFFFE){
                stream.writeShort(speed + 20000);//驱动电机转速，有效值范围：0～65531（数值偏移量20000表示-20000 r/min～45531r/min），最小计量单元：1r/min，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
            }else{
                stream.writeShort(speed);
            }

            float torque = motor.getTorque();
            if(torque != 0xFFFF && torque != 0xFFFE){
                stream.writeShort(FloatUtil.mul(torque, 10f).shortValue() + 20000);//驱动电机转矩，有效值范围：0～65531（数值偏移量20000表示-2000N*m～4553.1N*m），最小计量单元：0.1N*m，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
            }else{
                stream.writeShort((short)torque);
            }

            short motorTemperature = motor.getMotorTemperature();
            if(motorTemperature != 0xFF && motorTemperature != 0xFE){
                stream.writeByte(motorTemperature + 40);//驱动电机温度，有效值范围：0～250 （数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
            }else{
                stream.writeByte(motorTemperature);
            }

            float controllerInputVoltage = motor.getControllerInputVoltage();
            if(controllerInputVoltage != 0xFFFF && controllerInputVoltage != 0xFFFE){
                stream.writeShort(FloatUtil.mul(controllerInputVoltage,10f).shortValue());//电机控制器输入电压，有效值范围：0～60000（表示0V～6000V），最小计量单元：0.1V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
            }else{
                stream.writeShort((short)controllerInputVoltage);
            }

            float controllerDirectCurrent = motor.getControllerDirectCurrent();
            if(controllerDirectCurrent != 0xFFFF && controllerDirectCurrent != 0xFFFE){
                stream.writeShort((FloatUtil.mul(controllerDirectCurrent, 10f).shortValue() + 10000));//电机控制器直流母线电流，有效值范围： 0～20000（数值偏移量1000A，表示-1000A～+1000A），最小计量单元：0.1A，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
            }else{
                stream.writeShort((short)controllerDirectCurrent);
            }
        }
    }

}
