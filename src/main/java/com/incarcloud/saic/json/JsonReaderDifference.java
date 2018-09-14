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
import java.util.Date;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/11 09:35
 */
public class JsonReaderDifference {
    static Logger logger = LoggerFactory.getLogger(JsonReaderDifference.class);
//    static String savePath = "/home/saic/gnsstime.txt"; // 数据存储路径
//    static String dataPath = "/saic/data/json";         // 读取数据路径
//    static int maxTask = 64;                        // 线程数量
    static String savePath = "D:\\gnsstime.txt"; // 数据存储路径
    static String dataPath = "D:\\test";         // 读取数据路径
    static int maxTask = 96;                        // 线程数量


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
            Long interval0 = 0l;
            Long interval30 = 0l;
            Long interval60 = 0l;
            Long intervalOther = 0l;
            Long error = 0l;
            while(tempStringOne != null && (tempStringTwo = reader.readLine()) != null) {
                try {
                    JSONObject entityOne = JSONObject.parseObject(tempStringOne);
                    JSONObject entityTwo = JSONObject.parseObject(tempStringTwo);
                    if(!"1970-01-01 08:00:00".equals(entityOne.getString("gnsstime")) && !"1970-01-01 08:00:00".equals(entityTwo.getString("gnsstime"))) {
                        Date two =  entityTwo.getDate("gnsstime");
                        Date one = entityOne.getDate("gnsstime");
                        Long difference = (two.getTime() - one.getTime())/1000;
                        if(difference == 0){
                            interval0 ++ ;
                        } else if(difference == 30) {
                            interval30 ++ ;
                        } else if(difference == 60) {
                            interval60 ++;
                        } else {
                            intervalOther ++ ;
                        }
                        String vehspeed = entityOne.getString("vehspeed");
                        if(!vehspeed.equals("0")){
                            System.out.println(tempStringOne);
                        }
                    }
                    tempStringOne = tempStringTwo;
                } catch (Exception var32) {
                    logger.error(var32.getMessage());
                    error ++ ;
                }
            }
            reader.close();
            writer("INSERT INTO `incar`.`test_interval`(`vin`, `interval30`, `interval0`, `interval60`, `intervalOther`, `error`) VALUES ('"
                    + file.getName() + "',"  + interval30 + "," + interval0 + "," + interval60 + "," + intervalOther + ","  + error + ");", savePath);
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
