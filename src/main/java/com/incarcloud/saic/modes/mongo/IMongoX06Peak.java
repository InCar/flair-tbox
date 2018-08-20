package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx06Peak;
import org.bson.Document;

public interface IMongoX06Peak {
    GBx06Peak makeGBx06Peak(Document bsonDoc);
}
