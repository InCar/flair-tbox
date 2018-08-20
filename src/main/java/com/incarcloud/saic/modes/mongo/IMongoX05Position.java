package com.incarcloud.saic.modes.mongo;

import com.incarcloud.saic.GB32960.GBx05Position;
import org.bson.Document;

public interface IMongoX05Position {
    GBx05Position makeGBx05Position(Document bsonDoc);
}
