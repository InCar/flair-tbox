package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx02Motor;

import java.sql.ResultSet;

public interface IOracleX02Motor {
    GBx02Motor makeGBx02Motor(ResultSet rs);
}