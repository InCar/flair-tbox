package com.incarcloud.saic.utils;

/**
 * DataPack工具类(readXXX方法均是调用ByteBuf的readXXX方法，会移动ByteBuf的读指针)
 *
 * @author Aaric, created on 2017-06-29T09:39.
 * @since 2.0
 */
public class DataPackUtil {

    /**
     * byte[]转字符串
     * @param bArray
     * @return
     */
    public static String bytes2hex(byte[] bArray) {
        //字节数据转16进制字符串
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return getSpaceHex(sb.toString());
    }

    public static String getSpaceHex(String str){
        //将不带空格的16进制字符串加上空格
        String re = "";
        String regex = "(.{2})";
        re = str.replaceAll (regex, "$1 ");
        return re;
    }

}
