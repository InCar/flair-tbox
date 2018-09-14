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
 */
public class JSONTreeUtil {
    static Logger logger = LoggerFactory.getLogger(JsonReader.class);

    // 读取文件
    public static TreeMap<String, Integer> getTreeMap(File readerFile, File writerFile) {
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        LineNumberReader raf = null;
        BufferedWriter bw = null;

        createFile(writerFile);

        try {
            raf = new LineNumberReader(new FileReader(readerFile));
            String tempString = null;
            while ((tempString = raf.readLine()) != null) {
                try {
                    raf.getLineNumber();
                    JSONObject entity = JSONObject.parseObject(tempString);
                    String date = entity.getString("gnsstime");
                    treeMap.put(date, raf.getLineNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            raf.close();

            bw = new BufferedWriter(new FileWriter(writerFile));

            Iterator titer = treeMap.entrySet().iterator();
            while (titer.hasNext()) {
                Map.Entry ent = (Map.Entry) titer.next();
                String keyt = ent.getKey().toString();
                String valuet = ent.getValue().toString();

                String temp = readAppointedLineNumber(readerFile, Integer.valueOf(valuet));
                System.out.println(temp);

                bw.write(temp + "\t\n");
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
        }
        return null;
    }

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


    public static void change(File readerFile, File writerFile) {
        TreeMap<String, JsonExcursion> treeMap = new TreeMap<>();
        LineNumberReader raf = null;
        BufferedWriter bw = null;
        //  创建文件
        createFile(writerFile);

        int offset = 0;

        try {
            raf = new LineNumberReader(new FileReader(readerFile));
            String tempString = null;
            while ((tempString = raf.readLine()) != null) {
                try {
                    int len = tempString.length();
                    JSONObject entity = JSONObject.parseObject(tempString);
                    String date = entity.getString("gnsstime");
                    JsonExcursion entry = new JsonExcursion(offset, 0, tempString.length());
                    treeMap.put(date, entry);
                    offset += len;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            raf.close();

            bw = new BufferedWriter(new FileWriter(writerFile));
            RandomAccessFile inputFile = new RandomAccessFile(readerFile, "r");
            Iterator iterator = treeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                try {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    JsonExcursion entity = (JsonExcursion) entry.getValue();
                    inputFile.seek(entity.getStart());
                    long ii = inputFile.getFilePointer();
                    String ss = inputFile.readLine();
                    byte[] bytes = new byte[entity.getLength()];
                    inputFile.read(bytes, 0, entity.getLength());
                    String temp = new String(bytes);
                    bw.write(temp + "\t\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException var31) {
                    logger.error(var31.getMessage());
                }
            }
        }



    }

    /**
     * 随机读取文件内容
     *
     * @param fileName 文件名
     */
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


    public static void getBytes(File readerFile, int num) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(readerFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
