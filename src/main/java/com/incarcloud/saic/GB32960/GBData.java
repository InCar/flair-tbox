package com.incarcloud.saic.GB32960;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class GBData {
    private static final DateTimeFormatter s_fmtTm = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId s_zoneGMT8 = ZoneId.of("+8");

    protected final String vin;
    protected final ZonedDateTime tmGMT8;

    public GBData(String vin, ZonedDateTime tm){
        assert(vin != null && vin.length() == 17 && tm != null);

        this.vin = vin;
        this.tmGMT8 = tm.withZoneSameInstant(s_zoneGMT8);
    }

    public String getVin(){ return vin; }
    public ZonedDateTime getTmGMT8(){ return tmGMT8; }
    public String getTmGMT8AsString(){ return tmGMT8.format(s_fmtTm); }

    // 计算数据帧尺寸,1字节标识符+数据帧内容
    public abstract int calcGBFrameSize();
    // 写入GB32960数据帧
    public abstract void fillGBFrame(DataOutputStream stream) throws IOException;

    // 拼装GB32960实时数据包
    public static byte[] makeGBPackage(String vin, String tm, List<GBData> listGBData) throws IOException{

        final int headerSize = 24+1; // 24字节包头+1字节BCC校验
        int size = 6; // 计算尺寸需求 +6字节实时数据时间戳
        for(GBData data : listGBData){
            size += data.calcGBFrameSize();
        }

        // 缓冲区
        ByteArrayOutputStream stream = new ByteArrayOutputStream(headerSize+size);
        DataOutputStream ds = new DataOutputStream(stream);

        // 写入包头
        ds.writeShort(0x2323);
        ds.writeByte(0x02); // 实时数据
        ds.writeByte(0xFE); // 非应答命令包

        ds.write(vin.getBytes(StandardCharsets.US_ASCII));

        ds.writeByte(0x00); // 不加密
        ds.writeShort(size);

        for(GBData data : listGBData){
            data.fillGBFrame(ds);
        }

        ds.writeByte(0x00); // BCC占位符
        ds.close();

        // 计算BCC
        byte[] buf = stream.toByteArray();
        byte bcc = (byte)0x00;
        for(int i=2;i<buf.length-2;i++){
            bcc = (byte)(bcc^buf[i]);
        }
        buf[buf.length-1] = bcc;

        stream.close();

        return buf;
    }
}
