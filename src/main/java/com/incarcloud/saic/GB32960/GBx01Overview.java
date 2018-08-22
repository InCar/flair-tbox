package com.incarcloud.saic.GB32960;

import com.incarcloud.saic.utils.FloatUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x01整车数据
 */
public class GBx01Overview extends GBData {
    public GBx01Overview(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    // 车辆启动状态 READY(ready)“0x01”，熄火(keyoff)“0x02”，其他“0x03”，“0xFE”表示异常，“0xFF”表示无效。
    private byte vehicleStatus;
    public byte getVehicleStatus(){ return vehicleStatus; }
    public void setVehicleStatus(byte val){ vehicleStatus = val; }

    // 充电状态 0x01：停车充电；0x02：行驶充电；0x03：未充电状态，0x04：充电完成；“0xFE”表示异常，“0xFF”表示无效。
    private byte chargingStatus;
    public byte getChargingStatus(){ return chargingStatus; }
    public void setChargingStatus(byte val){ chargingStatus = val; }

    // 运行模式 驱动能量来源0x01: 纯电；0x02：混动；0x03：燃油；0xFE表示异常；0xFF表示无效。
    private byte powerSource;
    public byte getPowerSource(){ return powerSource; }
    public void setPowerSource(byte val){ powerSource = val; }

    // 车速 有效值范围：0～2200（表示0 km/h～220 km/h），最小计量单元：0.1km/h，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    private float speedKmH;
    public float getSpeedKmH(){
        return speedKmH;
    }
    public void setSpeedKmH(float val){
        speedKmH = val;
    }

    // 累计里程 有效值范围：0～9999999（表示0km～999999.9km），最小计量单元：0.1km。
    //         “0xFF, 0xFF, 0xFF,0xFE”表示异常，“0xFF,0xFF,0xFF,0xFF”表示无效
    private float mileageKm;
    public float getMileageKm(){
        return mileageKm;
    }
    public void setMileageKm(float val){
        mileageKm = val;
    }

    // 总电压 有效值范围：0～10000（表示0V～1000V），最小计量单元：0.1V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    private float voltage;
    public float getVoltage() {
        return voltage;
    }
    public void setVoltage(float val) {
        voltage = val;
    }

    // 总电流 有效值范围： 0～20000（偏移量1000A，表示-1000A～+1000A），最小计量单元：0.1A，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    private float current;
    public float getCurrent() {
        return current;
    }
    public void setCurrent(float val) {
        current = val;
    }

    // SOC 有效值范围：0～100（表示0%～100%），最小计量单元：1%，“0xFE”表示异常，“0xFF”表示无效。
    private byte soc;
    public byte getSoc() {
        return soc;
    }
    public void setSoc(byte val) {
        soc = val;
    }

    // DC-DC状态 直流斩波状态 0x01：工作；0x02：断开，“0xFE”表示异常，“0xFF”表示无效。
    private byte dcdcOnOff;
    public byte getDcdcOnOff() {
        return dcdcOnOff;
    }
    public void setDcdcOnOff(byte val) {
        dcdcOnOff = val;
    }

    // 挡位 bit5:0x0无驱动,0x1有驱动;bit4:0x0无制动0x1有制动;bit0-bit3:0x0空档,0x1~0x7档,0xD=R,0xE=D,0xF=P
    private byte bit5;
    private byte bit4;
    private byte bit3;

    public byte getBit5() {
        return bit5;
    }

    public void setBit5(byte bit5) {
        this.bit5 = bit5;
    }

    public byte getBit4() {
        return bit4;
    }

    public void setBit4(byte bit4) {
        this.bit4 = bit4;
    }

    public byte getBit3() {
        return bit3;
    }

    public void setBit3(byte bit3) {
        this.bit3 = bit3;
    }

    // 绝缘电阻 有效范围0~60000（表示0KΩ~60000KΩ），最小计量单元：1KΩ。
    private int resistancekOhm;
    public int getResistancekOhm() {
        return resistancekOhm;
    }
    public void setResistancekOhm(int resistancekOhm) {
        this.resistancekOhm = resistancekOhm;
    }

    @Override
    public int calcGBFrameSize(){
        // 整车数据固定21字节
        return 21;
    }

    // 写入GB32960数据帧
    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.writeByte(0x01);

        stream.writeByte(vehicleStatus);
        stream.writeByte(chargingStatus);
        stream.writeByte(powerSource);

        if(speedKmH != 0xFFFF && speedKmH != 0xFFFE){
            stream.writeShort(FloatUtil.mul(speedKmH, 10f).shortValue());
        }else{
            stream.writeShort((short)speedKmH);
        }
        if(mileageKm != 0xFFFFFFFF && mileageKm != 0xFFFFFFFE){
            stream.writeInt(FloatUtil.mul(mileageKm, 10f).intValue());
        }else{
            stream.writeInt((int)mileageKm);
        }

        if(voltage != 0xFFFF && voltage != 0xFFFE){
            stream.writeShort(FloatUtil.mul(voltage, 10f).shortValue());
        }else{
            stream.writeShort((short)voltage);
        }
        if(current != 0xFFFF && current != 0xFFFE){
            stream.writeShort(FloatUtil.mul(current, 10f).shortValue() + 10000);
        }else{
            stream.writeShort((short)current);
        }

        stream.writeByte(soc);
        stream.writeByte(dcdcOnOff);

        String low = Integer.toBinaryString(bit3);
        StringBuilder value = new StringBuilder();
        value.append("00" + bit5 + bit4);
        for(int i = 0;i < 4 - low.length();i ++){
            value.append("0");
        }
        value.append(low);
        byte carGear = Byte.parseByte(value.toString(), 2);
        stream.writeByte(carGear);

        stream.writeShort(resistancekOhm);

        stream.writeShort(0); // 预留2字节
    }
}
