package com.incarcloud.saic.t2017;

import com.incarcloud.lang.Action;
import com.incarcloud.saic.config.MongoConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作逻辑
 */
public class TaskWork implements Action<TaskArg> {
    private static final Logger s_logger = LoggerFactory.getLogger(TaskWork.class);

    private MongoConfig cfgMongo = null;
    private MongoClient client = null;

    public TaskWork(){
    }

    // 初始化
    public void init(MongoConfig cfg){
        cfgMongo = cfg;
        client = cfgMongo.createClient();
    }

    // 清理
    public void clean(){
        if(client != null)
            client.close();
    }

    @Override
    public void run(TaskArg arg){
        try{
            MongoDatabase database = client.getDatabase(cfgMongo.getDatabase());
            MongoCollection<Document> vins = database.getCollection(cfgMongo.getCollection());

            for(Document doc : vins.find()){
                // TODO: process data fetched
                // s_logger.info("{}", doc.toJson());
            }
        }catch (Exception ex){
            s_logger.error("TaskWork run failed: {}", ex);
        }
    }
}
