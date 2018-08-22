package com.incarcloud.saic.GB32960;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 国标GB32960 0x07报警数据
 */
public class GBx07Alarm extends GBData {
    public GBx07Alarm(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    /**
     * 通用报警标志
     */
    private byte tempPlusHigherl;//温度差异报警
    private byte tempratureHighestl;//电池高温报警
    private byte totalVolHighestl;//车载储能装置过压
    private byte totalVolLowestl;//车载储能装置欠压
    private byte socLowerl;//soc低
    private byte sellVolHighestL;//电池过压
    private byte sellVolLowestl;//电压欠压
    private byte socHigherAlarm;//SOC过高报警
    private byte socJumpAlarm;//SOC跳变报警
    private byte batterySysDismatch;//可充电储能系统不匹配报警
    private byte volPlusBiggerl;//电池单体一致性差
    private byte insuLowl;//绝缘报警
    private byte dcdcTempAlarm;//DC_DC温度报警
    private byte icuBrakeSysErr;//制动系统报警
    private byte dcdcStatusAlarm;//DC_DC状态报警
    private byte isMotorControlerTempHigh;//驱动电机控制器温度
    private byte isLockHigh;//高压互锁状态报警
    private byte isMotorTempHigh;//驱动电机温度
    private byte sellVolHighestChargerl;//车载储能装置过充

    /**
     * 最高报警等级，为当前发生的故障中的最高等级值，有效值范围：0～3，“0”表示无故障；“1”表示1级故障，指代不影响车辆正常行驶的故障；“2”表示2级故障，指代影响车辆性能，需驾驶员限制行驶的故障；“3”表示3级故障，为最高级别故障，指代驾驶员应立即停车处理或请求救援的故障；具体等级对应的故障内容由厂商自行定义；“0xFE”表示异常，“0xFF”表示无效。
     */
    private short maxLevel;

    /**
     * 可充电储能子系统故障总数N1，N1个可充电储能子系统故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short deviceFaultCount;
    /**
     * 可充电储能子系统故障代码列表，扩展性数据，由厂商自行定义，可充电储能子系统故障个数等于可充电储能子系统故障总数N1。
     */
    private List<Integer> deviceFaultCodeList;

    /**
     * 驱动电机故障总数N2，N2个驱动电机故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short motorFaultCount;
    /**
     * 驱动电机故障代码列表，厂商自行定义，驱动电机故障个数等于驱动电机故障总数N2。
     */
    private List<Integer> motorFaultCodeList;

    /**
     * 发动机故障总数N3，N3个驱动电机故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short engineFaultCount;
    /**
     * 发动机故障列表，厂商自行定义，发动机故障个数等于驱动电机故障总数N3。
     */
    private List<Integer> engineFaultCodeList;

    /**
     * 其他故障总数N4，N4个其他故障，有效值范围：0～252，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short otherFaultCount;
    /**
     * 其他故障代码列表，厂商自行定义，故障个数等于故障总数N4。
     */
    private List<Integer> otherFaultCodeList;

    public byte getTempPlusHigherl() {
        return tempPlusHigherl;
    }

    public void setTempPlusHigherl(byte tempPlusHigherl) {
        this.tempPlusHigherl = tempPlusHigherl;
    }

    public byte getTempratureHighestl() {
        return tempratureHighestl;
    }

    public void setTempratureHighestl(byte tempratureHighestl) {
        this.tempratureHighestl = tempratureHighestl;
    }

    public byte getTotalVolHighestl() {
        return totalVolHighestl;
    }

    public void setTotalVolHighestl(byte totalVolHighestl) {
        this.totalVolHighestl = totalVolHighestl;
    }

    public byte getTotalVolLowestl() {
        return totalVolLowestl;
    }

    public void setTotalVolLowestl(byte totalVolLowestl) {
        this.totalVolLowestl = totalVolLowestl;
    }

    public byte getSocLowerl() {
        return socLowerl;
    }

    public void setSocLowerl(byte socLowerl) {
        this.socLowerl = socLowerl;
    }

    public byte getSellVolHighestL() {
        return sellVolHighestL;
    }

    public void setSellVolHighestL(byte sellVolHighestL) {
        this.sellVolHighestL = sellVolHighestL;
    }

    public byte getSellVolLowestl() {
        return sellVolLowestl;
    }

    public void setSellVolLowestl(byte sellVolLowestl) {
        this.sellVolLowestl = sellVolLowestl;
    }

    public byte getSocHigherAlarm() {
        return socHigherAlarm;
    }

    public void setSocHigherAlarm(byte socHigherAlarm) {
        this.socHigherAlarm = socHigherAlarm;
    }

    public byte getSocJumpAlarm() {
        return socJumpAlarm;
    }

    public void setSocJumpAlarm(byte socJumpAlarm) {
        this.socJumpAlarm = socJumpAlarm;
    }

    public byte getBatterySysDismatch() {
        return batterySysDismatch;
    }

    public void setBatterySysDismatch(byte batterySysDismatch) {
        this.batterySysDismatch = batterySysDismatch;
    }

    public byte getVolPlusBiggerl() {
        return volPlusBiggerl;
    }

    public void setVolPlusBiggerl(byte volPlusBiggerl) {
        this.volPlusBiggerl = volPlusBiggerl;
    }

    public byte getInsuLowl() {
        return insuLowl;
    }

    public void setInsuLowl(byte insuLowl) {
        this.insuLowl = insuLowl;
    }

    public byte getDcdcTempAlarm() {
        return dcdcTempAlarm;
    }

    public void setDcdcTempAlarm(byte dcdcTempAlarm) {
        this.dcdcTempAlarm = dcdcTempAlarm;
    }

    public byte getIcuBrakeSysErr() {
        return icuBrakeSysErr;
    }

    public void setIcuBrakeSysErr(byte icuBrakeSysErr) {
        this.icuBrakeSysErr = icuBrakeSysErr;
    }

    public byte getDcdcStatusAlarm() {
        return dcdcStatusAlarm;
    }

    public void setDcdcStatusAlarm(byte dcdcStatusAlarm) {
        this.dcdcStatusAlarm = dcdcStatusAlarm;
    }

    public byte getIsMotorControlerTempHigh() {
        return isMotorControlerTempHigh;
    }

    public void setIsMotorControlerTempHigh(byte isMotorControlerTempHigh) {
        this.isMotorControlerTempHigh = isMotorControlerTempHigh;
    }

    public byte getIsLockHigh() {
        return isLockHigh;
    }

    public void setIsLockHigh(byte isLockHigh) {
        this.isLockHigh = isLockHigh;
    }

    public byte getIsMotorTempHigh() {
        return isMotorTempHigh;
    }

    public void setIsMotorTempHigh(byte isMotorTempHigh) {
        this.isMotorTempHigh = isMotorTempHigh;
    }

    public byte getSellVolHighestChargerl() {
        return sellVolHighestChargerl;
    }

    public void setSellVolHighestChargerl(byte sellVolHighestChargerl) {
        this.sellVolHighestChargerl = sellVolHighestChargerl;
    }

    public short getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(short maxLevel) {
        this.maxLevel = maxLevel;
    }

    public short getDeviceFaultCount() {
        return deviceFaultCount;
    }

    public void setDeviceFaultCount(short deviceFaultCount) {
        this.deviceFaultCount = deviceFaultCount;
    }

    public List<Integer> getDeviceFaultCodeList() {
        return deviceFaultCodeList;
    }

    public void setDeviceFaultCodeList(List<Integer> deviceFaultCodeList) {
        this.deviceFaultCodeList = deviceFaultCodeList;
    }

    public short getMotorFaultCount() {
        return motorFaultCount;
    }

    public void setMotorFaultCount(short motorFaultCount) {
        this.motorFaultCount = motorFaultCount;
    }

    public List<Integer> getMotorFaultCodeList() {
        return motorFaultCodeList;
    }

    public void setMotorFaultCodeList(List<Integer> motorFaultCodeList) {
        this.motorFaultCodeList = motorFaultCodeList;
    }

    public short getEngineFaultCount() {
        return engineFaultCount;
    }

    public void setEngineFaultCount(short engineFaultCount) {
        this.engineFaultCount = engineFaultCount;
    }

    public List<Integer> getEngineFaultCodeList() {
        return engineFaultCodeList;
    }

    public void setEngineFaultCodeList(List<Integer> engineFaultCodeList) {
        this.engineFaultCodeList = engineFaultCodeList;
    }

    public short getOtherFaultCount() {
        return otherFaultCount;
    }

    public void setOtherFaultCount(short otherFaultCount) {
        this.otherFaultCount = otherFaultCount;
    }

    public List<Integer> getOtherFaultCodeList() {
        return otherFaultCodeList;
    }

    public void setOtherFaultCodeList(List<Integer> otherFaultCodeList) {
        this.otherFaultCodeList = otherFaultCodeList;
    }

    @Override
    public int calcGBFrameSize(){
        return 9 + 4 * (deviceFaultCount + motorFaultCount + engineFaultCount + otherFaultCount);
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.writeByte(0x07);

        Byte byte0 = 0;
        Integer byte1 = Integer.parseInt("00000" + sellVolHighestChargerl + isMotorTempHigh + isLockHigh, 2);
        Integer byte2 = Integer.parseInt("" + isMotorControlerTempHigh + dcdcStatusAlarm + icuBrakeSysErr + dcdcTempAlarm + insuLowl + volPlusBiggerl + batterySysDismatch + socJumpAlarm, 2);
        Integer byte3 = Integer.parseInt("" + socHigherAlarm + sellVolLowestl + sellVolHighestL + socLowerl + totalVolLowestl + totalVolHighestl + tempratureHighestl + tempPlusHigherl, 2);

        stream.writeByte(maxLevel);
        stream.writeByte(byte0);
        stream.writeByte(byte1);
        stream.writeByte(byte2);
        stream.writeByte(byte3);
        stream.writeByte(deviceFaultCount);
        if(deviceFaultCount != 0xFF && deviceFaultCount != 0xFE && deviceFaultCount > 0){
            for(int deviceFaultCode : deviceFaultCodeList){
                stream.writeInt(deviceFaultCode);
            }
        }
        stream.writeByte(motorFaultCount);
        if(motorFaultCount != 0xFF && motorFaultCount != 0xFE && motorFaultCount > 0){
            for(int motorFaultCode : motorFaultCodeList){
                stream.writeInt(motorFaultCode);
            }
        }
        stream.writeByte(engineFaultCount);
        if(engineFaultCount != 0xFF && engineFaultCount != 0xFE && engineFaultCount > 0){
            for(int engineFaultCode : engineFaultCodeList){
                stream.writeInt(engineFaultCode);
            }
        }
        stream.writeByte(otherFaultCount);
        if(otherFaultCount != 0xFF && otherFaultCount != 0xFE && otherFaultCount > 0){
            for(int otherFaultCode : otherFaultCodeList){
                stream.writeInt(otherFaultCode);
            }
        }
    }
}
