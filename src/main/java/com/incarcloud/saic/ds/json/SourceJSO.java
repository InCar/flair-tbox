package com.incarcloud.saic.ds.json;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.incarcloud.saic.config.JsonConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import com.incarcloud.saic.json.DocumentJson;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SourceJSO implements ISource2017 {

    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger s_logger = LoggerFactory.getLogger(SourceJSO.class);


    private final JsonConfig cfg;

    public SourceJSO(JsonConfig cfg) {
        this.cfg = cfg;
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void fetch(String vin, LocalDate date, IDataWalk dataWalk) {
        BufferedReader reader = null;
        String localDateString = date.format(s_fmt);
        String tempString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-DD-mm hh:MM:ss");

            Date locaDate = dateFormat.parse(localDateString + " 23:59:59");
            reader = new BufferedReader(new FileReader(new File(cfg.getDir() + "/" + vin)), 50*1024*1024);
            List<DocumentJson> documentList = new ArrayList<DocumentJson>();

            // 遍历读取数据
            while ((tempString = reader.readLine()) != null) {
                JSONObject jsonObject = JSONObject.parseObject(tempString);
                String gnsstimeS = jsonObject.getString("gnsstime");
                Date gnsstimeData = dateFormat.parse(gnsstimeS);

                // 如果读取到大于目标时间的数据则后面的数据跳过
                if ((gnsstimeData.getTime() - locaDate.getTime()) > 0) {
                    break;
                }
                // 读取到等于目标时间的数据则解析
                if (gnsstimeS.contains(localDateString)) {
                    DocumentJson document1 = DocumentJson.parse(tempString);
                    documentList.add(document1);
                }
            }
            if (documentList.size() > 0) {
                s_logger.debug("fetching {} {} {}", vin, date.format(s_fmt), documentList.size());
            }
            try {
                // 如果DataWalk的onBegin方法失败,就没有必要取数据了,直接结束
                if (dataWalk.onBegin(documentList.size())) {
                    long idx = 0;
                    for (Document doc : documentList) {
                        // 如果DataWalk的onData方法返回false,跳出循环
                        if (!dataWalk.onData(doc, idx)) break;
                        idx++;
                    }
                }
            } catch (Exception ex) {
                dataWalk.onFailed(ex);
                return;
            }
            dataWalk.onFinished();
            reader.close();
        } catch (FileNotFoundException e) {
//            s_logger.debug("fetching {} {} 该VIN码不存在于JSON数据中",vin,i);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
//            s_logger.debug("fetching {} {} JSON 解析出错",vin, tempString);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
