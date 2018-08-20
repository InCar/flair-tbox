package com.incarcloud.saic.GB32960;

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

    @Override
    public int calcGBFrameSize(){
        // TODO: ...
        return 0;
    }

    @Override
    public void fillGBFrame(DataOutputStream stream) throws IOException {
        // TODO: ...
    }
}
