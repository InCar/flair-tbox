package com.incarcloud.saic;

import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.config.SAIC2017Config;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class App implements CommandLineRunner{
    private static final Logger s_logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private ApplicationContext _ctx;

    @Override
    public void run(String... args)throws Exception{
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        s_logger.info("appver: {}", (new GitVer()).getVersion());
        s_logger.info("Active log4j config file: {}", config.getName());

        SAIC2017Config cfg = _ctx.getBean(SAIC2017Config.class);
        MongoConfig cfgMongo = cfg.getMongo();
        String strMongo = String.format("mongodb://%s:%s@%s:27017",
                cfgMongo.getUser(), cfgMongo.getPwd(), cfg.getMongo().getHost());

        MongoClient client = MongoClients.create(strMongo);
        try{
            MongoDatabase database = client.getDatabase("s100");
            MongoCollection<Document> vins = database.getCollection("vins");

            for(Document doc : vins.find()){
                s_logger.info("{}", doc.toJson());
            }
        }finally {
            client.close();
        }

        SpringApplication.exit(_ctx, ()->0);
    }
}
