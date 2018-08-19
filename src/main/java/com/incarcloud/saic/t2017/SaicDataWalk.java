package com.incarcloud.saic.t2017;

import com.incarcloud.saic.ds.IDataWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 上汽数据处理
 */
class SaicDataWalk implements IDataWalk {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger s_logger = LoggerFactory.getLogger(SaicDataWalk.class);

    private final String vin;
    private final LocalDate date;
    private final String mode;
    private final String out;

    private OutputStream fs = null;
    private OutputStreamWriter writer = null;

    SaicDataWalk(String vin, LocalDate date, String mode, String out){
        this.vin = vin;
        this.date = date;
        this.mode = mode;
        this.out = out;
    }

    /**
     * 目录结构组织形式
     * out/yyyy/MM/dd
     * 每vin一个文件
     * 按时序存贮1天的数据
     * 每天所有的数据打包成一个 dd.tar.gz 文件
     */
    public boolean onBegin(){
        Path path = Paths.get(this.out,
                String.valueOf(date.getYear()),
                String.format("%02d", date.getMonthValue()),
                String.format("%02d", date.getDayOfMonth()),
                this.vin);
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);

            fs = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING);
            writer = new OutputStreamWriter(fs, Charset.forName("UTF-8").newEncoder());

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
    public boolean onData(Object data){
        // TODO: write file
        try {
            if (data instanceof Document) {
                Document doc = (Document) data;
                String buf = doc.toJson();
                writer.write(buf);
                writer.write("\n");
            }
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
        s_logger.error("Fetch data failed: {}", ex);
        closeFS();
    }

    private void closeFS(){
        try {
            if (fs != null) fs.close();
        }catch (Exception ex){
            s_logger.error("Close file failed: {} : {} : {}",
                    this.vin, this.date.format(s_fmt), ex);
        }
    }
}
