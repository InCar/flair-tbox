package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx07Alarm;

import java.sql.ResultSet;

public interface IOracleX07Alarm {
    GBx07Alarm makeGBx07Alarm(ResultSet rs);
}
