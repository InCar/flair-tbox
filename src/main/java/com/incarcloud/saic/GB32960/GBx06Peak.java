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
    private byte highBatteryId;
    /**
     * 最高电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte highBatteryCode;
    /**
     * 电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float highVoltage;
    /**
     * 最低电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte lowBatteryId;
    /**
     * 最低电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte lowBatteryCode;
    /**
     * 电池单体电压最低值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float lowVoltage;
    /**
     * 最高温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte highTemperatureId;
    /**
     * 最高温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte highProbeCode;
    /**
     * 最高温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte highTemperature;
    /**
     * 最低温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte lowTemperatureId;
    /**
     * 最低温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte lowProbeCode;
    /**
     * 最低温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte lowTemperature;

    public byte getHighBatteryId() {
        return highBatteryId;
    }

    public void setHighBatteryId(byte highBatteryId) {
        this.highBatteryId = highBatteryId;
    }

    public byte getHighBatteryCode() {
        return highBatteryCode;
    }

    public void setHighBatteryCode(byte highBatteryCode) {
        this.highBatteryCode = highBatteryCode;
    }

    public float getHighVoltage() {
        return highVoltage;
    }

    public void setHighVoltage(float highVoltage) {
        this.highVoltage = highVoltage;
    }

    public byte getLowBatteryId() {
        return lowBatteryId;
    }

    public void setLowBatteryId(byte lowBatteryId) {
        this.lowBatteryId = lowBatteryId;
    }

    public byte getLowBatteryCode() {
        return lowBatteryCode;
    }

    public void setLowBatteryCode(byte lowBatteryCode) {
        this.lowBatteryCode = lowBatteryCode;
    }

    public float getLowVoltage() {
        return lowVoltage;
    }

    public void setLowVoltage(float lowVoltage) {
        this.lowVoltage = lowVoltage;
    }

    public byte getHighTemperatureId() {
        return highTemperatureId;
    }

    public void setHighTemperatureId(byte highTemperatureId) {
        this.highTemperatureId = highTemperatureId;
    }

    public byte getHighProbeCode() {
        return highProbeCode;
    }

    public void setHighProbeCode(byte highProbeCode) {
        this.highProbeCode = highProbeCode;
    }

    public byte getHighTemperature() {
        return highTemperature;
    }

    public void setHighTemperature(byte highTemperature) {
        this.highTemperature = highTemperature;
    }

    public byte getLowTemperatureId() {
        return lowTemperatureId;
    }

    public void setLowTemperatureId(byte lowTemperatureId) {
        this.lowTemperatureId = lowTemperatureId;
    }

    public byte getLowProbeCode() {
        return lowProbeCode;
    }

    public void setLowProbeCode(byte lowProbeCode) {
        this.lowProbeCode = lowProbeCode;
    }

    public byte getLowTemperature() {
        return lowTemperature;
    }

    public void setLowTemperature(byte lowTemperature) {
        this.lowTemperature = lowTemperature;
    }

    @Override
    public int calcGBFrameSize(){
        return 14;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.write(0x06);

        stream.writeByte(highBatteryId);//最高电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highBatteryCode);//电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        stream.writeShort(FloatUtil.mul(highVoltage , 1000f).shortValue());//电池单体电压最高值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        stream.writeByte(lowBatteryId);//最低电压电池子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowBatteryCode);//最低电压电池单体代号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(FloatUtil.mul(lowVoltage,1000f).shortValue());//电池单体电压最低值，有效值范围：0～15000（表示0V～15V），最小计量单元：0.001V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
        stream.writeByte(highTemperatureId);//最高温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highProbeCode);//最高温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(highTemperature);//最高温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowTemperatureId);//最低温度子系统号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowProbeCode);//最低温度探针序号，有效值范围：1～250，“0xFE”表示异常，“0xFF”表示无效。
        stream.writeByte(lowTemperature);//最低温度值，有效值范围：0～250（数值偏移量40℃，表示-40℃～+210℃），最小计量单元：1℃，“0xFE”表示异常，“0xFF”表示无效。
    }
}
