package com.incarcloud.saic.json;

import com.alibaba.fastjson.JSONObject;
import com.incarcloud.concurrent.LimitedSyncArgTask;
import com.incarcloud.lang.Action;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/10 16:20
 */
public class JsonReader {
    static Logger logger = LoggerFactory.getLogger(JsonReader.class);
    static String savePath = "/home/saic/json.txt"; // 数据存储路径
    static String path = "/saic/data/json";         // 读取数据路径
    static int maxTask = 96;                        // 线程数量
//    static String savePath = "D:\\gnsstime.txt"; // 数据存储路径
//    static String dataPath = "D:\\test";         // 读取数据路径
//    static int maxTask = 96;                        // 线程数量


    public static void mainJson() throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Long d2015 = fmt.parse("2015-01-01 00:00:00").getTime();
        Long d2016 = fmt.parse("2016-01-01 00:00:00").getTime();
        Long d2017 = fmt.parse("2017-01-01 00:00:00").getTime();
        Long d2018 = fmt.parse("2018-01-01 00:00:00").getTime();

        File fileSave = new File(savePath);
        if (!fileSave.exists()) {
            File dir = new File(fileSave.getParent());
            dir.mkdirs();
            try {
                fileSave.createNewFile();
            } catch (IOException var12) {
                var12.printStackTrace();
                logger.error(var12.getMessage());
            }
        }

        Action<File> taskDemo = (x) -> {
            readFileByLines(x, d2015, d2016, d2017, d2018);
            logger.info("run demo task for argument: {}", x);
        };
        LimitedSyncArgTask<File> syncArgTaskDemo = new LimitedSyncArgTask<>(taskDemo);
        syncArgTaskDemo.setMax(maxTask);
        File file = new File(path);
        File[] array = file.listFiles();
        if (array != null && array.length > 0) {
            for(int c = 0; c < array.length; ++c) {
                File filea = array[c];
                syncArgTaskDemo.submit(filea);
            }
        }

    }

    public static void readFileByLines(File file, Long d2015, Long d2016, Long d2017, Long d2018) {
        BufferedReader reader = null;

        try {
            int gnssTime2015 = 0;
            int gnssTime2016 = 0;
            int gnssTime2017 = 0;
            int gnssTime2018 = 0;
            int gnssTime2019 = 0;
            int error = 0;
            int pt2015 = 0;
            int pt2016 = 0;
            int pt2017 = 0;
            int pt2018 = 0;
            int pt2019 = 0;
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while((tempString = reader.readLine()) != null) {
                try {
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    JSONObject entity = JSONObject.parseObject(tempString);
                    Long pt = entity.getLong("pt");
                    Long gnsstime = fmt.parse(entity.getString("gnsstime")).getTime();
                    if (pt < 2015010100L) {
                        ++pt2015;
                    } else if (pt < 2016010100L && pt >= 2015010100L) {
                        ++pt2016;
                    } else if (pt < 2017010100L && pt >= 2016010100L) {
                        ++pt2017;
                    } else if (pt < 2018010100L && pt >= 2017010100L) {
                        ++pt2018;
                    } else if (pt >= 2018010100L) {
                        ++pt2019;
                    }

                    if (gnsstime < d2015) {
                        ++gnssTime2015;
                    } else if (gnsstime < d2016 && gnsstime >= d2015) {
                        ++gnssTime2016;
                    } else if (gnsstime < d2017 && gnsstime >= d2016) {
                        ++gnssTime2017;
                    } else if (gnsstime < d2018 && gnsstime >= d2017) {
                        ++gnssTime2018;
                    } else if (gnsstime >= d2018) {
                        ++gnssTime2019;
                    }
                } catch (Exception var32) {
                    System.out.println(tempString + "    " + var32);
                    logger.error(var32.getMessage());
                    ++error;
                }
            }

            reader.close();
            writer("INSERT INTO test(`vin`, `gnssTime2015`, `gnssTime2016`, `gnssTime2017`, `gnssTime2018`, `gnssTime2019`, `pt2015`, `pt2016`, `pt2017`, `pt2018`, `pt2019`, `error`) VALUES ('" + file.getName() + "'," + gnssTime2015 + "," + gnssTime2016 + "," + gnssTime2017 + "," + gnssTime2018 + "," + gnssTime2019 + "," + pt2015 + "," + pt2016 + "," + pt2017 + "," + pt2018 + "," + pt2019 + "," + error + ");", savePath);
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
