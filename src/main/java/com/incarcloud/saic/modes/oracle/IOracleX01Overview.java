package com.incarcloud.saic.modes.oracle;

import com.incarcloud.saic.GB32960.GBx01Overview;
import java.sql.ResultSet;

public interface IOracleX01Overview {
    GBx01Overview makeGBx01Overview(ResultSet rs);
}
