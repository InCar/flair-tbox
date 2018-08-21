package com.incarcloud.saic.GB32960;

import com.incarcloud.saic.utils.FloatUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x04发动机
 */
public class GBx04Engine extends GBData {
    public GBx04Engine(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    /**
     * 发动机状态，0x01：启动状态；0x02：关闭状态，“0xFE”表示异常，“0xFF”表示无效。
     */
    private byte status;

    /**
     * 曲轴转速，有效范围：0～60000(表示0rpm～60000rpm)，最小计量单元：1rpm，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private short speed;

    /**
     * 燃料消耗率，有效值范围：0～60000（表示0L/100km～600L/100km），最小计量单元：0.01L/100km，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
     */
    private float rate;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public int calcGBFrameSize(){
        return 5;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.writeByte(0x04);

        stream.writeByte(status);//发动机状态，0x01：启动状态；0x02：关闭状态，“0xFE”表示异常，“0xFF”表示无效。

        stream.writeShort(speed);//曲轴转速，有效范围：0～60000(表示0rpm～60000rpm)，最小计量单元：1rpm，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。

        float rate = this.rate;
        stream.writeShort(FloatUtil.mul(rate ,100f).shortValue());//燃料消耗率，有效值范围：0～60000（表示0L/100km～600L/100km），最小计量单元：0.01L/100km，“0xFF,0xFE”表示异常，“0xFF,0xFF”表示无效。
    }
}
