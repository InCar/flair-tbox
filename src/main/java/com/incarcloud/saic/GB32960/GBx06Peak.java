package com.incarcloud.saic.GB32960;

import com.incarcloud.saic.utils.FloatUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x06极值数据
 */
public class GBx06Peak extends GBData {
    public GBx06Peak(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    /**
     * 最高电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short highBatteryId;
    /**
     * 最高电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short highBatteryCode;
    /**
     * 电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float highVoltage;
    /**
     * 最低电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short lowBatteryId;
    /**
     * 最低电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short lowBatteryCode;
    /**
     * 电池单体电压最低值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float lowVoltage;
    /**
     * 最高温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short highTemperatureId;
    /**
     * 最高温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short highProbeCode;
    /**
     * 最高温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short highTemperature;
    /**
     * 最低温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short lowTemperatureId;
    /**
     * 最低温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short lowProbeCode;
    /**
     * 最低温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private short lowTemperature;

    public short getHighBatteryId() {
        return highBatteryId;
    }

    public void setHighBatteryId(short highBatteryId) {
        this.highBatteryId = highBatteryId;
    }

    public short getHighBatteryCode() {
        return highBatteryCode;
    }

    public void setHighBatteryCode(short highBatteryCode) {
        this.highBatteryCode = highBatteryCode;
    }

    public float getHighVoltage() {
        return highVoltage;
    }

    public void setHighVoltage(float highVoltage) {
        this.highVoltage = highVoltage;
    }

    public short getLowBatteryId() {
        return lowBatteryId;
    }

    public void setLowBatteryId(short lowBatteryId) {
        this.lowBatteryId = lowBatteryId;
    }

    public short getLowBatteryCode() {
        return lowBatteryCode;
    }

    public void setLowBatteryCode(short lowBatteryCode) {
        this.lowBatteryCode = lowBatteryCode;
    }

    public float getLowVoltage() {
        return lowVoltage;
    }

    public void setLowVoltage(float lowVoltage) {
        this.lowVoltage = lowVoltage;
    }

    public short getHighTemperatureId() {
        return highTemperatureId;
    }

    public void setHighTemperatureId(short highTemperatureId) {
        this.highTemperatureId = highTemperatureId;
    }

    public short getHighProbeCode() {
        return highProbeCode;
    }

    public void setHighProbeCode(short highProbeCode) {
        this.highProbeCode = highProbeCode;
    }

    public short getHighTemperature() {
        return highTemperature;
    }

    public void setHighTemperature(short highTemperature) {
        this.highTemperature = highTemperature;
    }

    public short getLowTemperatureId() {
        return lowTemperatureId;
    }

    public void setLowTemperatureId(short lowTemperatureId) {
        this.lowTemperatureId = lowTemperatureId;
    }

    public short getLowProbeCode() {
        return lowProbeCode;
    }

    public void setLowProbeCode(short lowProbeCode) {
        this.lowProbeCode = lowProbeCode;
    }

    public short getLowTemperature() {
        return lowTemperature;
    }

    public void setLowTemperature(short lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    @Override
    public int calcGBFrameSize(){
        return 15;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.write(0x06);

        stream.writeByte(highBatteryId);//最高电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highBatteryCode);//电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        if(highVoltage != 0xFFFF && highVoltage != 0xFFFE){
            stream.writeShort(FloatUtil.mul(highVoltage , 1000f).shortValue());//电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        }else{
            stream.writeShort((short)highVoltage);
        }
        stream.writeByte(lowBatteryId);//最低电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowBatteryCode);//最低电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        if(lowVoltage != 0xFFFF && lowVoltage != 0xFFFE){
            stream.writeShort(FloatUtil.mul(lowVoltage,1000f).shortValue());//电池单体电压最低值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        }else{
            stream.writeShort((short)lowVoltage);
        }
        stream.writeByte(highTemperatureId);//最高温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highProbeCode);//最高温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highTemperature);//最高温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowTemperatureId);//最低温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowProbeCode);//最低温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowTemperature);//最低温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
    }
}
