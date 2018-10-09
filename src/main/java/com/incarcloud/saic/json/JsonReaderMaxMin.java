package com.incarcloud.saic.json;

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
 * @create_date 2018/9/11 09:35
 * @Description 做统计报表，用于转换文件到mysql中
 */
public class JsonReaderMaxMin {
    static Logger logger = LoggerFactory.getLogger(JsonReaderMaxMin.class);
    static String source = "oracle";
    static String year = "2017";
    static String savePath = "F:\\2018 - incar\\saic-2017\\build\\libs\\" + year + source +".txt"; // 数据存储路径
    static String dataPath = "F:\\2018 - incar\\saic-2017\\build\\libs\\" + year + source; // 读取数据路径
    static int maxTask = 4;  // 线程数量

    public static void createFile() {
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
            readFileByLines(x,year,source);
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

    public static void readFileByLines(File file, String year, String source) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStringOne = reader.readLine();
            int error = 0 ;
            StringBuffer sb = new StringBuffer();
            while((tempStringOne = reader.readLine()) != null) {
                try {
                    if(tempStringOne.length() >10) {
                        String[] strings = tempStringOne.split("/");
                        String vin = strings[strings.length -1 ];
                        String name = file.getName();
                        String month = name.substring(0,2);
                        String day = name.substring(2,4);
                        String date = year + "-"+month+"-"+day;
                        sb.append(" ('").append(date).append("','").append(vin).append("','").append(source).append("'),");
                    }
                } catch (Exception var32) {
                    logger.error(var32.getMessage());
                    error ++ ;
                }
            }
            if(sb.length() > 3) {
                String values = sb.substring(0, sb.length() - 1) + ";";
                reader.close();
                writer("INSERT INTO `incar`.`vin`(`dateStr`, `vin`, `source`) VALUES " + values, savePath);
                System.out.println(file.getName() + "完成");
                logger.info(file.getName() + "完成");
            }
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
