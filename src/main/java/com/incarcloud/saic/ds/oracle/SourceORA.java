package com.incarcloud.saic.ds.oracle;

import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Oracle数据源
 */
public class SourceORA implements ISource2017 {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger s_logger = LoggerFactory.getLogger(SourceORA.class);

    public SourceORA(OracleConfig cfg){
        // TODO: oracle的实现
        throw new RuntimeException("NotImplementation");
    }

    public void init(){

    }

    public void clean(){

    }

    public void fetch(String vin, LocalDate date, IDataWalk dataWalk){
        // TODO: oracle的实现


        // 从Oracle里读出一堆ResultSet,替代掉null
        // 有索引能排序更好，没有索引就自己在内存里排序
        // SELECT * FROM tb_xxx WHERE vin = ...( ORDER BY ...)
        int i = 0;
        ResultSet rs = null; //

        int total = 0;
        // 先取一下count，打一下日志
        s_logger.debug("fetching {} {} {}", vin, date.format(s_fmt), total);

        if(total > 0) {
            try {
                // 再取数据ResultSet
                dataWalk.onBegin(total);

                while (rs.next()) {
                    dataWalk.onData(rs, i);
                    i++;
                }

            } catch (Exception ex) {
                dataWalk.onFailed(ex);
                return;
            }

            dataWalk.onFinished();
        }
    }
}
