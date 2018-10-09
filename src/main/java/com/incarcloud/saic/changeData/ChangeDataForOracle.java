package com.incarcloud.saic.changeData;

import com.incarcloud.concurrent.LimitedSyncArgTask;
import com.incarcloud.lang.Action;
import com.incarcloud.saic.json.JSONTreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GuoKun
 * @version 1.0
 * @Description 解析已有数据，替换VIN码和最后的校验位。
 */
public class ChangeDataForOracle {
    static Logger logger = LoggerFactory.getLogger(ChangeDataForOracle.class);

    static List<File> filelist = new ArrayList<>();

    static Base64.Encoder base64Encoder = Base64.getEncoder();
    static Base64.Decoder base64Decoder = Base64.getDecoder();

    // 读取vin码，然后选择替换的文件
    public static void changeVin(String path, String vinPath, String outFile) {
        File vinFile = new File(vinPath);
        List<String> vinList = new ArrayList<>(); // 读取所有需要数据的VIN
        Map<String, List<File>> mapFile = changeToMap(path); // 对已有数据的VIN分组存储 Map
        BufferedReader reader = null;

        try {
            // 赋值 vinList
            reader = new BufferedReader(new FileReader(vinFile));
            String tempVin;
            while ((tempVin = reader.readLine()) != null) {
                vinList.add(tempVin);
            }

            // 如果数据量小于需要被做的数据量，程序退出
            if (mapFile.size() < vinList.size()) {
                System.out.println("@@@@@@@@@@@@@@@@  被替换的VIN码小于替换的VIN码数量，程序退出 @@@@@@@@@@@@@@@@");
                System.exit(0);
            }

            StringBuffer bs = new StringBuffer();
            // 开始转换数据
            int i = 0;

            Iterator entries = mapFile.entrySet().iterator();
            while (entries.hasNext()) {
                if (i < (vinList.size() - 1)) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String key = (String) entry.getKey();
                    List<File> value = (List<File>) entry.getValue();
                    String vin = vinList.get(i);
                    changeDate(vin, key, value, outFile);
                    bs.append("需要转换的VIN: ").append(vin).append(", 原VIN: ").append(key).append(", 替换的文件数量: ")
                            .append(value.size()).append("\n");
                    i++;
                } else {
                    System.out.println(bs);
                    System.out.println(" 转换结束！！");
                    System.exit(-1);
                }
            }


            // 开始转换数据
            // 开启多线程任务
//            Action<InnerClass> taskDemo = (xz) -> {
//                changeDate(xz.getVin(), xz.getChangeVin(), xz.getFilelist(), xz.getOutPutFile());
//                logger.info("run task for argument: {}", xz.toString());
//            };
//
//            // 任务集合
//            LimitedSyncArgTask<InnerClass> syncArgTaskDemo = new LimitedSyncArgTask<>(taskDemo);
//
//            //
//            syncArgTaskDemo.setMax(8);
//            Iterator entriess = mapFile.entrySet().iterator();
//            while (entriess.hasNext()) {
//                if (i < (vinList.size() - 1)) {
//                    Map.Entry entry = (Map.Entry) entriess.next();
//                    String key = (String) entry.getKey();
//                    List<File> value = (List<File>) entry.getValue();
//                    String vin = vinList.get(i);
//                    InnerClass innerClass = new InnerClass(vin, key, value, outFile);
//                    syncArgTaskDemo.submit(innerClass);
//                    bs.append("需要转换的VIN: ").append(vin).append(", 原VIN: ").append(key).append(", 替换的文件数量: ")
//                            .append(value.size()).append("\n");
//                    i++;
//                } else {
//                    break;
//                }
//            }
            System.out.println(bs);
            System.out.println(" 转换结束！！");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    // 反编译 -- 替换字节码 -- 再次编译
    public static void changeDate(String vin, String changeVin, List<File> filelist, String outPutFile) {

        // 把VIN码按照GB36926 转为 US_ASCII 字节码
        byte[] vinByteArray = vin.getBytes(StandardCharsets.US_ASCII);
        byte[] changeVinByte = changeVin.getBytes(StandardCharsets.US_ASCII);

        for (File file : filelist) {
            try {
                // 获取原文件路径，并创建对应的文件，拿到对应的输入流
                String luj = file.getParent().substring(file.getParent().indexOf("data") + 4);
                File fileVin = new File(outPutFile + luj + "\\" + vin);
                JSONTreeUtil.createFile(fileVin);
                OutputStream fs = new FileOutputStream(fileVin);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs, Charset.forName("UTF-8").newEncoder()), 1024 * 1024);

                // 获取输出流
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        // time为明码
                        String time = line.substring(0, 15);

                        // 以下为编码内容
                        String bm = line.substring(15);
                        byte[] bytes = bm.getBytes(StandardCharsets.US_ASCII);
                        byte[] byteDecode = base64Decoder.decode(bytes);

                        // 替换vin字节码
                        byte[] aa = new byte[byteDecode.length];
                        System.arraycopy(byteDecode, 0, aa, 0, 4);
                        System.arraycopy(vinByteArray, 0, aa, 4, vinByteArray.length);
                        System.arraycopy(byteDecode, 21, aa, 21, byteDecode.length - 21);

                        // BCC位占位符
                        aa[aa.length - 1] = 0x00;

                        // 计算BCC位
                        int length = aa.length - 25;
                        aa[22] = (byte) ((length >>> 8) & 0xFF);
                        aa[23] = (byte) (length & 0xFF);
                        int bcc = aa[2] & 0xFF;
                        for (int i = 3; i < aa.length - 1; i++) {
                            bcc = bcc ^ (aa[i] & 0xFF);
                        }
                        aa[aa.length - 1] = (byte) (bcc & 0xFF);

                        String mingma = new String(aa);
                        String mingmayuan = new String(byteDecode);
                        // base64编译
                        String bmStrChanger = base64Encoder.encodeToString(aa);

                        // 输出
                        time += bmStrChanger;
                        writer.write(time);
                        writer.write("\n");
                        System.out.println("time: " + time);
                        System.out.println("line: " + line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 将文件按照文件名分组
    public static Map<String, List<File>> changeToMap(String path) {
        traverseFolder(path);
        Map<String, List<File>> mapFile = filelist.stream().collect(Collectors.groupingBy(File::getName));

        Iterator entries = mapFile.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            List<File> value = (List<File>) entry.getValue();
            if (value.size() < 30) {
                entries.remove();
            }
        }

        Set<String> keSet = mapFile.keySet();
        return mapFile;
    }

    // 读取一个文件夹下所有的文件
    public static void traverseFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            try {
                if (files.length == 0) {
                    System.out.println("文件夹是空的!");
                    return;
                } else {
                    for (File file2 : files) {
                        if (file2.isDirectory()) {
//                            System.out.println("文件夹:" + file2.getAbsolutePath());
                            traverseFolder(file2.getAbsolutePath());
                        } else {
                            filelist.add(file2);
//                            System.out.println("文件:" + file2.getAbsolutePath());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("~~~~~~~~~~~~~~" + e.getMessage());
            }
        } else {
            System.out.println("文件不存在!");
        }
    }
}
