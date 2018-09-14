package com.incarcloud.saic.modes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModeJson extends ModeMongo{
    private static final Logger s_logger = LoggerFactory.getLogger(ModeJson.class);

    ModeJson(String mode, boolean[] gbSwitches){
        super(mode, gbSwitches);
    }
}
