package com.incarcloud.saic.modes;

import java.util.List;

/**
 * 映射算法工厂
 */
public class ModeFactory {
    public static final String DS_MONGO = "MongoDB";
    public static final String DS_ORACLE = "Oracle";

    public static Mode create(String mode){
        Mode obj = null;
        String ds = checkDS(mode);
        if(ds == DS_MONGO)
            obj = new ModeMongo(mode, s_gbSwitches);
        else if(ds == DS_ORACLE)
            obj = new ModeOracle(mode, s_gbSwitches);
        return obj;
    }

    // 检查对应的数据源
    public static String checkDS(String mode){
        String ds;
        switch (mode){
            case "AS24":
            case "AS26":
            case "IP24":
            case "IP34":
            case "OLD-AS24":
            case "IP32P":
                ds = DS_MONGO;
                break;
            case "OLD-IP24MCE":
            case "OLD-IP24":
            case "OLD-BP34":
                ds = DS_ORACLE;
                break;
            default:
                throw new UnsupportedOperationException(mode);
        }
        return ds;
    }

    // 测试用途,只测试某些类型的数据
    private static final boolean[] s_gbSwitches = new boolean[8];
    public static void switchOnOffGB(List<String> listGB){
        if(listGB.size() == 0){
            for(int i=0;i<s_gbSwitches.length;i++)
                s_gbSwitches[i] = true;
        }
        else{
            listGB.forEach(x->{
                if(x.equalsIgnoreCase("x01Overview")) s_gbSwitches[0x01] = true;
                else if(x.equalsIgnoreCase("x02Motor")) s_gbSwitches[0x02] = true;
                else if(x.equalsIgnoreCase("x04Engine")) s_gbSwitches[0x04] = true;
                else if(x.equalsIgnoreCase("x05Position")) s_gbSwitches[0x05] = true;
                else if(x.equalsIgnoreCase("x06Peak")) s_gbSwitches[0x06] = true;
                else if(x.equalsIgnoreCase("x07Alarm")) s_gbSwitches[0x07] = true;
            });
        }
    }
}
