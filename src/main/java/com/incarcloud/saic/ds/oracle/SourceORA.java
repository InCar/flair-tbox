package com.incarcloud.saic.ds.oracle;

import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.IDataWalk;
import com.incarcloud.saic.ds.ISource2017;
import com.incarcloud.saic.modes.Mode;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Oracle数据源
 */
public class SourceORA implements ISource2017 {

    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    public void  fetch(String vin, LocalDate date, IDataWalk dataWalk){

        Mode mode = dataWalk.getMode();
        String str = mode.getMode();
        String sql;
        String sqlCount;
        if("OLD-IP24MCE".equals(str)){
            sql = "select * from TB_MNT_SIGNAL_DATA_IP24MCE_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_IP24MCE_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else if("OLD-IP24".equals(str)){
            sql = "select * from TB_MNT_SIGNAL_DATA_IP24_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_IP24_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else if("OLD-BP34".equals(str)){
            sql = "select * from TB_MNT_SIGNAL_DATA_BP34_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
            sqlCount = "select count(1) as res from TB_MNT_SIGNAL_DATA_BP34_HIS t where t.VIN = '" + vin + "' and to_char(t.DATA_DATE,'yyyy-MM-dd') = '" + date.format(s_fmt) + "'";
        }else{
            return;
        }
        PreparedStatement pstmtcount = null;
        try {
            pstmtcount = conn.prepareStatement(sqlCount);
            ResultSet rscount = pstmtcount.executeQuery();
            int rowCount = 0;
            if(rscount.next()){
                rowCount = rscount.getInt("res");
            }
            if(rowCount > 0){
                PreparedStatement pstmt = null;
                try {
                    if(dataWalk.onBegin(rowCount)) {

                        pstmt = conn.prepareStatement(sql);
                        //建立一个结果集，用来保存查询出来的结果
                        ResultSet rs = pstmt.executeQuery();
                        long idx = 0;
                        while (rs.next()) {
                            if (!dataWalk.onData(rs, idx)){
                                break;
                            }
                            idx++;
                        }
                        rs.close();
                        pstmt.close();
                    }
                }catch (Exception ex){
                    dataWalk.onFailed(ex);
                    return;
                }finally {
                    if(pstmt != null){
                        try {
                            pstmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                dataWalk.onFinished();
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
    }
}
