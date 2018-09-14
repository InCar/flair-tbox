package com.incarcloud.saic.json;

import org.bson.Document;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.json.JsonReader;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.bson.assertions.Assertions.notNull;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/14 15:33
 */
public class DocumentJson extends Document {
    private Document _docReal;
    private static SimpleDateFormat simpleDateFormat = new  SimpleDateFormat("yyyy-mm-dd hh:MM:ss");

    DocumentJson(){
        super();
    }

    DocumentJson(Document src){
        this._docReal = src;
    }

    @Override
    public String getString(final Object key) {
        if("tboxTime".equals(key)){
            try {
                String time = (String) get("gnsstime");
                Date lon = simpleDateFormat.parse(time);
                return lon.getTime()+"";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (String) get(key);
    }

    public static DocumentJson parse(final String json) {
        return parses(json, new DocumentCodecJson());
    }


    public static DocumentJson parses(final String json, final Decoder<DocumentJson> decoder) {
        notNull("codec", decoder);
        org.bson.json.JsonReader bsonReader = new JsonReader(json);
        return decoder.decode(bsonReader, DecoderContext.builder().build());
    }


}
