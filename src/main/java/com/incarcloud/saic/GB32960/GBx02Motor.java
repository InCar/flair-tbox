package com.incarcloud.saic.GB32960;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x02驱动电机
 */
public class GBx02Motor extends GBData {
    public GBx02Motor(String vin, ZonedDateTime tm){
        super(vin, tm);
    }

    @Override
    public int calcGBFrameSize(){
        // TODO: ...
        return 0;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException{
        // TODO: ...
    }
}
