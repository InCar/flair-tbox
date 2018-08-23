package com.incarcloud.saic.t2017;

import com.incarcloud.lang.Action;
import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.config.OracleConfig;
import com.incarcloud.saic.ds.DSFactory;
import com.incarcloud.saic.ds.ISource2017;
import com.incarcloud.saic.modes.ModeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作逻辑
 */
public class TaskWork implements Action<TaskArg> {
    private static final Logger s_logger = LoggerFactory.getLogger(TaskWork.class);

    // 数据源
    private ISource2017 dsMGO = null;
    private ISource2017 dsORA = null;
    // 数据输出位置
    private String out = null;

    public TaskWork(){
    }

    // 初始化
    public void init(MongoConfig cfgMGO, OracleConfig cfgORA, String out){
        // 输入数据源
        dsMGO = DSFactory.create(cfgMGO);
        dsMGO.init();

        dsORA = DSFactory.create(cfgORA);
        dsORA.init();

        // 输出文件夹
        this.out = out;
    }

    // 清理
    public void clean(){
        if(dsMGO != null) dsMGO.clean();
        if(dsORA != null) dsORA.clean();
    }

    @Override
    public void run(TaskArg arg){
        SaicDataWalk dataWalk = new SaicDataWalk(arg, this.out);
        if(arg.getDS() == ModeFactory.DS_MONGO)
            dsMGO.fetch(arg.vin, arg.date, dataWalk);
        else
            dsORA.fetch(arg.vin, arg.date, dataWalk);

        arg.increaseFinishedVin();
    }
}
