package com.incarcloud.saic.ds.json;

import com.incarcloud.saic.config.JsonConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;

import java.time.LocalDate;

public class SourceJSO implements ISource2017 {
    public SourceJSO(JsonConfig cfg){
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void fetch(String vin, LocalDate date, IDataWalk dataWalk) {

    }
}
