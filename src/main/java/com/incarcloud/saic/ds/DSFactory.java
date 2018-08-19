package com.incarcloud.saic.ds;

import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.mongo.SourceMGO;
import com.incarcloud.saic.ds.oracle.SourceORA;

/**
 * 数据源工厂
 */
public class DSFactory {
    public static SourceMGO create(MongoConfig cfg){
        return new SourceMGO(cfg);
    }

    public static SourceORA create(OracleConfig cfg){
        return new SourceORA(cfg);
    }
}
