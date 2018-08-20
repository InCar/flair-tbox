package com.incarcloud.saic.t2017;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 上汽数据处理
 * 1天1车对应一个此对象
 */
class SaicDataWalk implements IDataWalk {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger s_logger = LoggerFactory.getLogger(SaicDataWalk.class);

    private final TaskArg taskArg;
    private final String out;
    private final Mode modeObj;

    private final Base64.Encoder base64Encoder;

    private Path path = null;
    private OutputStream fs = null;
    private BufferedWriter writer = null;

    // 实际写入行数
    private long actualWritten = 0;

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
        path = Paths.get(this.out,
                String.valueOf(taskArg.date.getYear()),
                String.format("%02d", taskArg.date.getMonthValue()),
                String.format("%02d", taskArg.date.getDayOfMonth()),
                taskArg.vin);
        try {
            Files.createDirectories(path.getParent());

            fs = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            final int size = 4096*1024*4; // 16M write buffer
            writer = new BufferedWriter(new OutputStreamWriter(fs, Charset.forName("UTF-8").newEncoder()), size);
            taskArg.updateTotal(totalCount);

            return true;
        }catch (Exception ex){
            s_logger.error("Create file failed : {} : {}",
                    path.toAbsolutePath(),
                    ex);
            return false;
        }
    }

    /**
     * 每条数据调用一次
     * 在开始和结束之间可能会被调用多次
     * 也可能一次都不调用
     */
    public boolean onData(Object data, long idx){
        try {

            List<GBData> listGBData = new ArrayList<>();
            for(Func<GBData, Object> makeFn : makeFuncs()){
                GBData dataGB = makeFn.call(data);
                if(dataGB != null) listGBData.add(dataGB);
            }

            if(listGBData.size() > 0){
                String vin = listGBData.get(0).getVin();
                String tm = listGBData.get(0).getTmGMT8AsString();
                byte[] bufGB32960 = GBData.makeGBPackage(vin, tm, listGBData);

                // 一条数据一行 时间戳,base64帧
                // 20171123103928,IyMH/kFCQ0RFRkcwMTIzNDU2Nzg5AQAAwQ==
                writer.write(tm);
                writer.write(",");
                writer.write(base64Encoder.encodeToString((bufGB32960)));
                writer.write("\n");
                actualWritten++;
            }

            // 用于跟踪进度
            taskArg.updateIdx(idx);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * 在正常结束时调用一次
     * 它和onFailed相互排斥,两者只有一个会被调用
     */
    public void onFinished(){
        closeFS();
    }

    /**
     * 在异常结束时调用一次
     * 它和onFinished相互排斥,两者只有一个会被调用
     */
    public void onFailed(Exception ex){
        s_logger.error("Fetch data failed: {} : {} : {}", taskArg.vin, taskArg.date.format(s_fmt), ex);
        closeFS();
    }

    private void closeFS(){
        try {
            if(writer != null){
                writer.flush();
                writer.close();
            }

            if (fs != null) {
                fs.close();
            }

            // 如果实际没有数据,清除掉文件
            if(actualWritten == 0)
                Files.deleteIfExists(path);

        }catch (Exception ex){
            s_logger.error("Close file failed: {} : {} : {}",
                    taskArg.vin, taskArg.date.format(s_fmt), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Func<GBData, Object>[] makeFuncs(){
        Func<GBData, Object>[] fnMake = (Func<GBData, Object>[])new Object[6];
        fnMake[0] = modeObj::makeGBx01Overview;
        fnMake[1] = modeObj::makeGBx02Motor;
        fnMake[2] = modeObj::makeGBx04Engine;
        fnMake[3] = modeObj::makeGBx05Position;
        fnMake[4] = modeObj::makeGBx06Peak;
        fnMake[5] = modeObj::makeGBx07Alarm;

        return fnMake;
    }
}
