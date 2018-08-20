package com.incarcloud.saic.GB32960;

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
    private short speedKmHX10;
    public float getSpeedKmH(){
        if(speedKmHX10 == (short)0xFFFF) return -1.0f;
        else if(speedKmHX10 == (short)0xFFFE) return -2.0f;
        else return speedKmHX10/10.0f;
    }
    public void setSpeedKmH(float val){
        if(val >=0.0f && val <= 300.0f) speedKmHX10 = (short)Math.round(val*10);
        else if(val < 0.0f) speedKmHX10 = (short)0xFFFF;
        else speedKmHX10 = (short)0xFFFE;
    }

    // 累计里程 有效值范围：0～9999999（表示0km～999999.9km），最小计量单元：0.1km。
    //         “0xFF, 0xFF, 0xFF,0xFE”表示异常，“0xFF,0xFF,0xFF,0xFF”表示无效
    private int mileageKmX10;

    // 总电压 有效值范围：0～10000（表示0V～1000V），最小计量单元：0.1V，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    private short voltageX10;

    // 总电流 有效值范围： 0～20000（偏移量1000A，表示-1000A～+1000A），最小计量单元：0.1A，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    private short currentX10plus10k;

    // SOC 有效值范围：0～100（表示0%～100%），最小计量单元：1%，“0xFE”表示异常，“0xFF”表示无效。
    private byte soc;

    // DC-DC状态 直流斩波状态 0x01：工作；0x02：断开，“0xFE”表示异常，“0xFF”表示无效。
    private byte dcdcOnOff;

    // 挡位 bit5:0x0无驱动,0x1有驱动;bit4:0x0无制动0x1有制动;bit0-bit3:0x0空档,0x1~0x7档,0xD=R,0xE=D,0xF=P
    private byte carGear;

    // 绝缘电阻 有效范围0~60000（表示0KΩ~60000KΩ），最小计量单元：1KΩ。
    private int resistancekOhm;

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

        stream.writeShort(speedKmHX10);
        stream.writeInt(mileageKmX10);

        stream.writeShort(voltageX10);
        stream.writeShort(currentX10plus10k);

        stream.writeByte(soc);
        stream.writeByte(dcdcOnOff);
        stream.writeByte(carGear);

        stream.writeShort(resistancekOhm);

        stream.writeShort(0); // 预留2字节
    }
}
