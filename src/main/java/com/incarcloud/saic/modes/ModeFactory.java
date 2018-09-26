package com.incarcloud.saic.modes;

import com.incarcloud.saic.ds.DSFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 映射算法工厂
 */
public class ModeFactory {
    public static Mode create(String mode){
        Mode obj = null;

        String ds = checkDS();
        if (ds.equals(DSFactory.Mongo))
            obj = new ModeMongo(mode, s_gbSwitches);
        else if (ds.equals(DSFactory.Oracle))
            obj = new ModeOracle(mode, s_gbSwitches);
        else if(ds.equals(DSFactory.Json))
            obj = new ModeJson(mode, s_gbSwitches);
        return obj;
    }

    // 检查对应的数据源
    private static final List<String> dataSources = new ArrayList<>();
    public static void setDataSources(List<String> listSrc){
        dataSources.clear();
        dataSources.addAll(listSrc);
    }

    public static String checkDS(){
        // 强制只允许单一数据源
        if(dataSources.size() != 1)
            throw new RuntimeException("Only single data source is allowed!");

        return dataSources.get(0);
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
