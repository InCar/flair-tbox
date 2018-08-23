package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx06Peak;

import java.sql.ResultSet;

public interface IOracleX06Peak {
    GBx06Peak makeGBx06Peak(ResultSet rs);
}
