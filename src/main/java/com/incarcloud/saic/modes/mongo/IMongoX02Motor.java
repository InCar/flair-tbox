package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx02Motor;
import org.bson.Document;

public interface IMongoX02Motor {
    GBx02Motor makeGBx01Overview(Document bsonDoc);
}