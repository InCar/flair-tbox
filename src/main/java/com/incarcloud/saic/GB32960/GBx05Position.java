package com.incarcloud.saic.GB32960;

import com.incarcloud.saic.utils.DoubleUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x05位置数据
 */
public class GBx05Position extends GBData{
    public GBx05Position(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    /**
     * 定位状态，状态位定义见表15。
     */
    private byte positionStatus;
    /**
     * 经度，以度为单位的纬度值乘以10的6次方，精确到百万分之一度。
     */
    private double longitude;
    /**
     * 纬度，以度为单位的纬度值乘以10的6次方，精确到百万分之一度。
     */
    private double latitude;

    public byte getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(byte positionStatus) {
        this.positionStatus = positionStatus;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int calcGBFrameSize(){
        return 9;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        stream.writeByte(0x05);

        stream.writeByte(positionStatus);//定位状态，状态位定义见表15。
        if(longitude != 0xFFFFFFFF && longitude != 0xFFFFFFFE){
            stream.writeInt(DoubleUtil.mul(longitude,1000000d).intValue());//经度，以度为单位的纬度值乘以10的6次方，精确到百万分之一度。
        }else{
            stream.writeInt((int)longitude);
        }
        if(latitude != 0xFFFFFFFF && latitude != 0xFFFFFFFE){
            stream.writeInt(DoubleUtil.mul(latitude,1000000d).intValue());//纬度，以度为单位的纬度值乘以10的6次方，精确到百万分之一度。
        }else{
            stream.writeInt((int)latitude);
        }
    }
}
