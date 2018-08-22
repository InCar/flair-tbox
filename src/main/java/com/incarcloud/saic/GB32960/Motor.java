package com.incarcloud.saic.GB32960;

/**
 * 国标GB32960 0x02驱动电机
 */
public class Motor{
    /**
     * 驱动电机序号，有效值范围1~253
     */
    private short motorSeq;
    /**
     * 驱动电机状态：0x01-耗电，0x02-发电，0x03-关闭状态，0x04-准备状态，0xFF表示异常，0xFE表示无效
     */
    private short motorStatus;
    /**
     * 驱动电机控制器温度，有效值范围：0～250 （数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short controllerTemperature;
    /**
     * 驱动电机转速，有效值范围：0～65531（数值偏移量20000表示-20000 r/min～45531r/min），最小计量单元：1r/min，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private int speed;
    /**
     * 驱动电机转矩，有效值范围：0～65531（数值偏移量20000表示-2000N*m～4553.1N*m），最小计量单元：0.1N*m，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float torque;
    /**
     * 驱动电机温度，有效值范围：0～250 （数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short motorTemperature;
    /**
     * 电机控制器输入电压，有效值范围：0～60000（表示0V～6000V），最小计量单元：0.1V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float controllerInputVoltage;
    /**
     * 电机控制器直流母线电流，有效值范围： 0～20000（数值偏移量1000A，表示-1000A～+1000A），最小计量单元：0.1A，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float controllerDirectCurrent;

    public short getMotorSeq() {
        return motorSeq;
    }

    public void setMotorSeq(short motorSeq) {
        this.motorSeq = motorSeq;
    }

    public short getMotorStatus() {
        return motorStatus;
    }

    public void setMotorStatus(short motorStatus) {
        this.motorStatus = motorStatus;
    }

    public short getControllerTemperature() {
        return controllerTemperature;
    }

    public void setControllerTemperature(short controllerTemperature) {
        this.controllerTemperature = controllerTemperature;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getTorque() {
        return torque;
    }

    public void setTorque(float torque) {
        this.torque = torque;
    }

    public short getMotorTemperature() {
        return motorTemperature;
    }

    public void setMotorTemperature(short motorTemperature) {
        this.motorTemperature = motorTemperature;
    }

    public float getControllerInputVoltage() {
        return controllerInputVoltage;
    }

    public void setControllerInputVoltage(float controllerInputVoltage) {
        this.controllerInputVoltage = controllerInputVoltage;
    }

    public float getControllerDirectCurrent() {
        return controllerDirectCurrent;
    }

    public void setControllerDirectCurrent(float controllerDirectCurrent) {
        this.controllerDirectCurrent = controllerDirectCurrent;
    }
}
