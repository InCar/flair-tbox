package com.incarcloud.saic.ds.mongo;

import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * MongoDB数据源
 */
public class SourceMGO implements ISource2017 {
    private final Logger s_logger = LoggerFactory.getLogger(SourceMGO.class);

    private final MongoConfig cfg;
    private final MongoClient client;

    public SourceMGO(MongoConfig cfg){
        this.cfg = cfg;
        client = cfg.createClient();
    }

    public void init(){

    }

    public void clean(){
        client.close();
    }

    public void fetch(String vin, LocalDate date, IDataWalk dataWalk){
        try{
            MongoDatabase database = client.getDatabase(cfg.getDatabase());
            MongoCollection<Document> docs = database.getCollection(cfg.getCollection(date));

            dataWalk.onBegin();

            // TODO: 按上汽数据格式修订 排序要按时间序,这里临时先用mode试一下功能
            docs.find(Filters.eq("vin", vin))
                .sort(Sorts.ascending("mode"))
                .forEach((Block<Document>)dataWalk::onData);

            dataWalk.onFinished();
        }catch (Exception ex){
            s_logger.error("Fetch data failed : {}", ex);
            dataWalk.onFailed();
        }
    }
}
