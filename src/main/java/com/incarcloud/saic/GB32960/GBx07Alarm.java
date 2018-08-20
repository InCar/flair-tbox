package com.incarcloud.saic.GB32960;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * 国标GB32960 0x07报警数据
 */
public class GBx07Alarm extends GBData {
    public GBx07Alarm(String vin, ZonedDateTime tm){
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
