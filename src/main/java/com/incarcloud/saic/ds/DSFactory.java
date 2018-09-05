package com.incarcloud.saic.ds;

import com.incarcloud.saic.config.JsonConfig;
import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.json.SourceJSO;
import com.incarcloud.saic.ds.mongo.SourceMGO;
import com.incarcloud.saic.ds.oracle.SourceORA;

/**
 * 数据源工厂
 */
public class DSFactory {
    public final static String Mongo = "mongo";
    public final static String Oracle = "oracle";
    public final static String Json = "json";

    public static SourceMGO create(MongoConfig cfg){
        if(cfg == null) return null;
        return new SourceMGO(cfg);
    }

    public static SourceORA create(OracleConfig cfg){
        if(cfg == null) return null;
        return new SourceORA(cfg);
    }

    public static SourceJSO create(JsonConfig cfg){
        if(cfg == null) return null;
        return new SourceJSO(cfg);
    }
}
