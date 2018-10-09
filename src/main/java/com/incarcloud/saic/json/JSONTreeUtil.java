package com.incarcloud.saic.json;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/13 10:22
 * @Description 文件处理工具类
 */
public class JSONTreeUtil {

    static Logger logger = LoggerFactory.getLogger(JSONTreeUtil.class);

    // 读取文件指定行。
    static String readAppointedLineNumber(File sourceFile, int lineNumber) throws IOException {
        LineNumberReader reader = null;
        try {
            FileReader in = new FileReader(sourceFile);
            reader = new LineNumberReader(in);
            String s = "";
            if (lineNumber <= 0 || lineNumber > getTotalLines(sourceFile)) {
                System.out.println("不在文件的行数范围(1至总行数)之内。");
                System.exit(0);
            }
            int lines = 0;
            while (s != null) {
                lines++;
                s = reader.readLine();
                if ((lines - lineNumber) == 0) {
                    return s;
                }
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
        }
        return "";
    }

    // 文件内容的总行数。
    public static int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }

    // 随机读取文件内容
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            //将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            //一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            //将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    // 创建文件
    public static  void createFile( File writerFile) {
        if (!writerFile.exists()) {
            File dir = new File(writerFile.getParent());
            dir.mkdirs();
            try {
                writerFile.createNewFile();
            } catch (IOException var12) {
                var12.printStackTrace();
                logger.error(var12.getMessage());
            }
        }
    }
}
