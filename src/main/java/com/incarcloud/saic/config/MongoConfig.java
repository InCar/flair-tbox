package com.incarcloud.saic.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.ArrayList;
import java.util.List;

public class MongoConfig {
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

    public MongoClient createClient(){
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
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
