package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx05Position;

import java.sql.ResultSet;

public interface IOracleX05Position {
    GBx05Position makeGBx05Position(ResultSet rs);
}
