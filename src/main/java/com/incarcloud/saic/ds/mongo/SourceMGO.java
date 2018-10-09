package com.incarcloud.saic.ds.mongo;

import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * MongoDB数据源
 */
public class SourceMGO implements ISource2017 {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
        MongoDatabase database = client.getDatabase(cfg.getDatabase());
        MongoCollection<Document> docs = database.getCollection(cfg.getCollection(date));
        // JSON： 处理导入mongo的JSON数据
//        MongoCollection<Document> docs = database.getCollection("jso");

        Bson filterByVin = Filters.eq("vin", vin);

        // counting first
        Document docTotal = docs.aggregate(
                Arrays.asList(
                        Aggregates.match(filterByVin),
                        Aggregates.count()
                )).first();

        if(docTotal != null){
            int total = docTotal.get("count", Integer.class);
            s_logger.debug("fetching {} {} {}", vin, date.format(s_fmt), total);
//            String dateStr = date.format(s_fmt);
            try {
                // 如果DataWalk的onBegin方法失败,就没有必要取数据了,直接结束
                if(dataWalk.onBegin(total)) {
                    // 检索数据,因为mongo已经是按天存储在collection中,所以这里不需要时间过滤条件
                    MongoIterable<Document> fx = docs.find(Filters.eq("vin", vin));
                    // JSON： 处理导入mongo的JSON数据
//                    MongoIterable<Document> fx = docs.find(Filters.and(Filters.eq("vin", vin),
//                            Filters.gte("gnsstime",dateStr+" 00:00:00"),
//                            Filters.lte("gnsstime",dateStr+" 23:59:59")));

                    long idx = 0;
                    for (Document doc : fx) {
                        // 如果DataWalk的onData方法返回false,跳出循环
                        if (!dataWalk.onData(doc, idx)) break;
                        idx++;
                    }
                }
            }
            catch (Exception ex){
                dataWalk.onFailed(ex);
                return;
            }

            dataWalk.onFinished();
        }
        else{
            // no data
            s_logger.debug("fetching {} {} 0", vin, date.format(s_fmt));
        }
    }
}
