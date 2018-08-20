package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx04Engine;
import org.bson.Document;

public interface IMongoX04Engine {
    GBx04Engine makeGBx04Engine(Document bsonDoc);
}
