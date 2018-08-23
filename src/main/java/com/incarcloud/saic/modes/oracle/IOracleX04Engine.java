package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx04Engine;

import java.sql.ResultSet;

public interface IOracleX04Engine {
    GBx04Engine makeGBx04Engine(ResultSet rs);
}
