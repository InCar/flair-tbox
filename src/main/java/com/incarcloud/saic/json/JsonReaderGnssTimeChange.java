package com.incarcloud.saic.json;

import com.alibaba.fastjson.JSONObject;
import com.incarcloud.concurrent.LimitedSyncArgTask;
import com.incarcloud.lang.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/11 16:21
 */
public class JsonReaderGnssTimeChange {
    static Logger logger = LoggerFactory.getLogger(JsonReaderGnssTimeChange.class);
    static String savePath = "/home/saic/gnsstimeChangeTime.txt"; // 数据存储路径
    static String dataPath = "/saic/data/json";         // 读取数据路径
    static int maxTask = 96;                        // 线程数量
//    static String savePath = "D:\\gnsstimeChangeTime.txt"; // 数据存储路径
//    static String dataPath = "D:\\test";         // 读取数据路径
//    static int maxTask = 64;                        // 线程数量


    public static void mainJson() {
        // 判断文件是否存在，不存在就创建
        File fileSave = new File(savePath);
        if (!fileSave.exists()) {
            File dir = new File(fileSave.getParent());
            dir.mkdirs();
            try {
                fileSave.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("~~~~~~~~~~~~~~~~~~" + e.getMessage());
            }
        }

        // 线程任务
        Action<File> taskDemo = (x) -> {
            readFileByLines(x);
            logger.info("run task for argument: {}", x);
        };
        // 任务集合
        LimitedSyncArgTask<File> syncArgTaskDemo = new LimitedSyncArgTask<>(taskDemo);
        syncArgTaskDemo.setMax(maxTask);
        File file = new File(dataPath);
        File[] array = file.listFiles();
        if (array != null && array.length > 0) {
            for(int c = 0; c < array.length; ++c) {
                File filea = array[c];
                syncArgTaskDemo.submit(filea);
            }
        }
    }

    public static void readFileByLines(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStringOne = reader.readLine();
            String tempStringTwo = null;
            Long changeNumber = 0l;
            Long error = 0l;
            StringBuffer gnsstime = new StringBuffer();
            while(tempStringOne != null && (tempStringTwo = reader.readLine()) != null) {
                try {
                    JSONObject entityOne = JSONObject.parseObject(tempStringOne);
                    JSONObject entityTwo = JSONObject.parseObject(tempStringTwo);
                    if(("1970-01-01 08:00:00".equals(entityOne.getString("gnsstime")) && !"1970-01-01 08:00:00".equals(entityTwo.getString("gnsstime")))
                    || (!"1970-01-01 08:00:00".equals(entityOne.getString("gnsstime")) && "1970-01-01 08:00:00".equals(entityTwo.getString("gnsstime")))) {
                        changeNumber ++;
                        String gt = entityTwo.getString("gnsstime") + "   ";
                        gnsstime.append(gt);
                    }
                    tempStringOne = tempStringTwo;
                } catch (Exception var32) {
                    logger.error(var32.getMessage());
                    error ++ ;
                }
            }
            reader.close();
            writer("INSERT INTO `incar`.`test_gnsstime_change`(`vin`, `change_num`, `error`, `gt`) VALUES ('"
                    + file.getName() + "',"  + changeNumber + "," + error + ",'" + gnsstime + "');", savePath);
            System.out.println(file.getName() + "完成");
            logger.info(file.getName() + "完成");
        } catch (IOException var33) {
            var33.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
        }
    }

    public static synchronized void writer(String content, String path) {
        content = content + "\r\n";

        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(content);
            writer.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }


}
