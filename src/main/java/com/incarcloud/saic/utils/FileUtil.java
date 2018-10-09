package com.incarcloud.saic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件夹工具
 *
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/26 9:37
 */
public class FileUtil {
    static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    // 获取一个文件夹下所有的文件
    public static List<File> readerAll(String path) {
        List<File> fileList = new ArrayList<>();
        readerAll(fileList, path);
        return fileList;
    }

    // 读取一个文件夹下所有的文件
    public static void readerAll(List<File> fileList, String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            try {
                if (files.length == 0) {
//                    System.out.println(file.getAbsolutePath() + "文件夹是空的!");
                    logger.info(file.getAbsolutePath() + "文件夹是空的!");
                    fileList.add(file);
                    return;
                } else {
                    for (File file2 : files) {
                        if (file2.isDirectory()) {
//                            System.out.println("文件夹:" + file2.getAbsolutePath());
//                            logger.info("文件夹:" + file2.getAbsolutePath());
                            readerAll(fileList, file2.getAbsolutePath());
                        } else {
                            fileList.add(file2);
//                            System.out.println("文件:" + file2.getAbsolutePath());
//                            logger.info("文件:" + file2.getAbsolutePath());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR ~~~~~~~~~~~~~~ " + file.getAbsolutePath() + e.getMessage());
                logger.error("ERROR ~~~~~~~~~~~~~~ " + file.getAbsolutePath() + e.getMessage());
            }
        } else {
            System.out.println(file.getAbsolutePath() + "文件不存在!");
            logger.info(file.getAbsolutePath() + "文件不存在!");
        }
    }

    // 将文件按照文件名分组 并排序
    public static Map<String, List<File>> groupByName(String path) {
        List<File> fileList = readerAll(path);
        return fileList.stream().collect(Collectors.groupingBy(File::getName));
    }

    // 删除所有的空文件
    public static void deleteEmptyFile(String path) {
        List<File> fileList = readerAll(path);
        fileList.forEach(file -> {
            if (file.isDirectory()) {
                try {
                    File[] files = file.listFiles();
                    if (files.length == 0) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(file.getAbsolutePath() + "文件读取失败" + e.getMessage());
                }
            }
        });
    }

//    public static boolean isFileContentEqual(String oldFilePath, String newFilePath) {
//        //check does the two file exist.
//        if (!TextUtils.isEmpty(oldFilePath) && !TextUtils.isEmpty(newFilePath)) {
//            File oldFile = new File(oldFilePath);
//            File newFile = new File(newFilePath);
//            FileInputStream oldInStream = null;
//            FileInputStream newInStream = null;
//            try {
//                oldInStream = new FileInputStream(oldFile);
//                newInStream = new FileInputStream(newFile);
//
//                int oldStreamLen = oldInStream.available();
//                int newStreamLen = newInStream.available();
//                //check the file size first.
//                if (oldStreamLen > 0 && oldStreamLen == newStreamLen) {
//                    //read file data with a buffer.
//                    int cacheSize = 128;
//                    byte[] data1 = new byte[cacheSize];
//                    byte[] data2 = new byte[cacheSize];
//                    do {
//                        int readSize = oldInStream.read(data1);
//                        newInStream.read(data2);
//
//                        for (int i = 0; i < cacheSize; i++) {
//                            if (data1[i] != data2[i]) {
//                                return false;
//                            }
//                        }
//                        if (readSize == -1) {
//                            break;
//                        }
//                    } while (true);
//                    return true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                //release the stream resource.
//                try {
//                    if (oldInStream != null) {
//                        oldInStream.close();
//                    }
//                    if (newInStream != null) {
//                        newInStream.close();
//                    }
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        return false;
//    }

}
