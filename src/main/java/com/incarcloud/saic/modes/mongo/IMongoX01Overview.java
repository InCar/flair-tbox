package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx01Overview;
import org.bson.Document;

public interface IMongoX01Overview{
    GBx01Overview makeGBx01Overview(Document bsonDoc);
}
