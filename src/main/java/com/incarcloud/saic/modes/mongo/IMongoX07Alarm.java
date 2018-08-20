package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx07Alarm;
import org.bson.Document;

public interface IMongoX07Alarm {
    GBx07Alarm makeGBx07Alarm(Document bsonDoc);
}
