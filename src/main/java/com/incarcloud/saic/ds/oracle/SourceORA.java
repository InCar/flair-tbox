package com.incarcloud.saic.ds.oracle;

import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;

import java.time.LocalDate;

/**
 * Oracle数据源
 */
public class SourceORA implements ISource2017 {
    public SourceORA(OracleConfig cfg){
        // TODO: oracle的实现
        throw new RuntimeException("NotImplementation");
    }

    public void init(){

    }

    public void clean(){

    }

    public void fetch(String vin, LocalDate date, IDataWalk dataWalk){
        // TODO: oracle的实现
    }
}
