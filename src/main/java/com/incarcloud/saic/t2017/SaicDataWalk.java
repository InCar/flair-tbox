package com.incarcloud.saic.t2017;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.lang.Func;
import com.incarcloud.saic.GB32960.GBData;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.modes.Mode;
import com.incarcloud.saic.modes.ModeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * 上汽数据处理
 * 1天1车对应一个此对象
 */
class SaicDataWalk implements IDataWalk {
    private static final Logger s_logger = LoggerFactory.getLogger(SaicDataWalk.class);

    private final TaskArg taskArg;
    private final String out;
    private final Mode modeObj;

    private final Base64.Encoder base64Encoder;

    // 排序树
    private TreeSet<GBPackage> sortedPackages = new TreeSet<>();

    SaicDataWalk(TaskArg taskArg, String out){
        this.taskArg = taskArg;
        this.out = out;

        this.modeObj = ModeFactory.create(taskArg.mode);
        this.base64Encoder = Base64.getEncoder();
    }

    /**
     * 目录结构组织形式
     * out/yyyy/MM/dd
     * 每vin一个文件
     * 按时序存贮1天的数据
     * 每天所有的数据打包成一个 dd.tar.gz 文件
     */
    public boolean onBegin(long totalCount){
        taskArg.updateTotal(totalCount);
        return true;
    }

    /**
     * 每条数据调用一次
     * 在开始和结束之间可能会被调用多次
     * 也可能一次都不调用
     */
    public boolean onData(Object data, long idx){
        try {

            List<GBData> listGBData = new ArrayList<>();
            for(Object fn : makeFuncs()){
                @SuppressWarnings("unchecked")
                Func<GBData, Object> makeFn = (Func<GBData, Object>)fn;
                GBData dataGB = makeFn.call(data);
                if(dataGB != null) listGBData.add(dataGB);
            }

            if(listGBData.size() > 0){
                String vin = listGBData.get(0).getVin();
                String tm = listGBData.get(0).getTmGMT8AsString();
                ZonedDateTime tmGMT8 = listGBData.get(0).getTmGMT8();
                byte[] bufGB32960 = GBData.makeGBPackage(vin, tmGMT8, listGBData);
                String b64Val = base64Encoder.encodeToString((bufGB32960));
                // 按时间排序,以备输出
                sortedPackages.add(new GBPackage(tm, b64Val));
            }

            // 用于跟踪进度
            taskArg.updateIdx(idx);
            return true;
        }
        catch (Exception ex){
            // 用于跟踪进度
            taskArg.updateIdx(idx);
            s_logger.error("Processing data failed: {} : {} \n {}",
                    taskArg, Helper.printStackTrace(ex), data);

            return false;
        }
    }

    /**
     * 在正常结束时调用一次
     * 它和onFailed相互排斥,两者只有一个会被调用
     */
    public void onFinished(){
        output();
    }

    /**
     * 在异常结束时调用一次
     * 它和onFinished相互排斥,两者只有一个会被调用
     */
    public void onFailed(Exception ex){
        s_logger.error("Fetch data failed: {} : {}", taskArg, Helper.printStackTrace(ex));
        output();
    }

    // 输出到文件
    private void output(){
        Path path = Paths.get(this.out,
                String.valueOf(taskArg.date.getYear()),
                String.format("%02d", taskArg.date.getMonthValue()),
                String.format("%02d", taskArg.date.getDayOfMonth()),
                taskArg.vin);

        try {
            BufferedWriter writer = prepareFS(path);

            long actualWritten = 0L;
            for (GBPackage pack : sortedPackages) {
                // 一条数据一行 时间戳,base64帧
                // 20171123103928,IyMH/kFCQ0RFRkcwMTIzNDU2Nzg5AQAAwQ==
                writer.write(pack.tm);
                writer.write(",");
                writer.write(pack.val);
                writer.write("\n");
                actualWritten++;

                // 用于跟踪进度
                taskArg.updateActualWritten(actualWritten);
            }

            closeFS(writer);
        }
        catch (Exception ex){
            s_logger.error("Write file failed: {} : {}: {}",
                    taskArg, path.toAbsolutePath(), Helper.printStackTrace(ex));
        }
    }

    private BufferedWriter prepareFS(Path path) throws Exception{
        Files.createDirectories(path.getParent());
        OutputStream fs = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        final int size = 4096*1024*4; // 16M write buffer
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs, Charset.forName("UTF-8").newEncoder()), size);
        return writer;
    }

    private void closeFS(BufferedWriter writer) throws Exception{
        if(writer != null){
            writer.close();
        }
    }

    private Object[] makeFuncs(){
        Func<GBData, Object> fn;

        Object[] fnMake = new Object[6];

        fn = modeObj::makeGBx01Overview;
        fnMake[0] = fn;

        fn = modeObj::makeGBx02Motor;
        fnMake[1] = fn;

        fn = modeObj::makeGBx04Engine;
        fnMake[2] = fn;

        fn = modeObj::makeGBx05Position;
        fnMake[3] = fn;

        fn = modeObj::makeGBx06Peak;
        fnMake[4] = fn;

        fn = modeObj::makeGBx07Alarm;
        fnMake[5] = fn;

        return fnMake;
    }
}
