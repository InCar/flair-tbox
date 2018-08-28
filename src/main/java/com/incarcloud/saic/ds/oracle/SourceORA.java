package com.incarcloud.saic.ds.oracle;

import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import com.incarcloud.saic.modes.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.sql.*;

/**
 * Oracle数据源
 */
public class SourceORA implements ISource2017 {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger s_logger = LoggerFactory.getLogger(SourceORA.class);

    private final OracleConfig cfg;
    private final Connection conn;

    public SourceORA(OracleConfig cfg){
        this.cfg = cfg;
        conn = cfg.createClient();
    }

    public void init(){

    }

    public void clean(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fetch(String vin, LocalDate date, IDataWalk dataWalk){
        Mode mode = dataWalk.getMode();
        String str = mode.getMode();
        String sql;
        String sqlCount;
        if("OLD-IP24MCE".equals(str)){
            sql = "select t.*,t.DATA_DATE as tboxTime from TB_MNT_SIGNAL_DATA_IP24MCE_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "' order by t.DATA_DATE";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_IP24MCE_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else if("OLD-IP24".equals(str)){
            sql = "select t.*,t.DATA_DATE as tboxTime from TB_MNT_SIGNAL_DATA_IP24_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "' order by t.DATA_DATE";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_IP24_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else if("OLD-BP34".equals(str)){
            sql = "select t.*,t.DATA_DATE as tboxTime from TB_MNT_SIGNAL_DATA_BP34_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "' order by t.DATA_DATE";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_BP34_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else{
            return;
        }

        if(conn != null){
            PreparedStatement pstmtcount = null;
            int total = 0;
            try {
                pstmtcount = conn.prepareStatement(sqlCount);
                ResultSet rscount = pstmtcount.executeQuery();
                if(rscount.next()){
                    total = rscount.getInt("res");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(pstmtcount != null){
                    try {
                        pstmtcount.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 从Oracle里读出一堆ResultSet,替代掉null
            // 有索引能排序更好，没有索引就自己在内存里排序
            // SELECT * FROM tb_xxx WHERE vin = ...( ORDER BY ...)
            int i = 0;

            // 先取一下count，打一下日志
            s_logger.debug("fetching {} {} {}", vin, date.format(s_fmt), total);

            if(total > 0) {
                PreparedStatement pstmt = null;
                ResultSet rs = null; //
                try {
                    if(dataWalk.onBegin(total)) {
                        pstmt = conn.prepareStatement(sql);
                        rs = pstmt.executeQuery();
                        long idx = 0;
                        while (rs.next()) {
                            if (!dataWalk.onData(rs, idx)){
                                break;
                            }
                            idx++;
                        }

                    }
                    // 再取数据ResultSet
                    dataWalk.onBegin(total);

                    while (rs.next()) {
                        dataWalk.onData(rs, i);
                        i++;
                    }

                } catch (Exception ex) {
                    dataWalk.onFailed(ex);
                    return;
                } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                dataWalk.onFinished();
            }
        }
    }
}
