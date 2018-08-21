package com.incarcloud.saic.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoConfig {
    private static final Pattern s_rgxArgDate = Pattern.compile("\\{\\s*date\\s*:\\s*(\\S+)\\s*\\}");

    private List<String> hosts = new ArrayList<>();
    public List<String> getHosts(){ return hosts; }

    private String source;
    public String getSource(){ return source; }
    public void setSource(String val){ source = val; }

    private String user;
    public String getUser(){ return user; }
    public void setUser(String val){ user = val; }

    private char[] pwd;
    public char[] getPwd(){ return pwd; }
    public void setPwd(char[] val){ pwd = val; }

    private String database;
    public String getDatabase(){ return database; }
    public void setDatabase(String val){ database = val; }

    private String collection;
    public String getCollection(){ return collection; }
    public void setCollection(String val){ collection = val; }
    public String getCollection(LocalDate date){
        // 转换date参数
        Matcher m = s_rgxArgDate.matcher(collection);
        if(m.find()){
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(m.group(1));
            String xDate = date.format(fmt);
            String replaced = String.format("%s%s%s",
                    collection.substring(0, m.start()),
                    xDate,
                    collection.substring(m.end()));
            return replaced;
        }
        else{
            return collection;
        }
    }

    public MongoClient createClient(){
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applicationName("saic2017");
        builder.applyToClusterSettings(x->x.hosts(getHostAddresses()));
        if(user != null && user.length() > 0){
            builder.credential(MongoCredential.createCredential(user, source, pwd));
        }
        return MongoClients.create(builder.build());
    }

    private List<ServerAddress> getHostAddresses(){
        List<ServerAddress> listHosts = new ArrayList<>();
        for(String host : hosts){
            String[] iport = host.split(":", 2);
            if(iport.length == 2)
                listHosts.add(new ServerAddress(iport[0], Integer.parseInt(iport[1])));
            else
                listHosts.add(new ServerAddress(iport[0]));
        }
        return listHosts;
    }
}
