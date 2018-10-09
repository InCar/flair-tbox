package com.incarcloud.saic.modes;

import com.incarcloud.saic.modes.mongo.IMongoX00ConfRate;
import org.bson.Document;

public class MongoX00ConfRate extends MongoX implements IMongoX00ConfRate {

    @Override
    public float calc(Document bsonDoc){
        return 0.0f;
    }
}
