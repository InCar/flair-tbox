package com.incarcloud.saic.t2017;

import com.incarcloud.lang.Action;
import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.ds.DSFactory;
import com.incarcloud.saic.ds.ISource2017;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作逻辑
 */
public class TaskWork implements Action<TaskArg> {
    private static final Logger s_logger = LoggerFactory.getLogger(TaskWork.class);

    // 数据源
    private ISource2017 ds = null;

    public TaskWork(){
    }

    // 初始化
    public void init(MongoConfig cfg){
        ds = DSFactory.create(cfg);
        ds.init();
    }

    // 清理
    public void clean(){
        if(ds != null) ds.clean();
    }

    @Override
    public void run(TaskArg arg){
        SaicDataWalk dataWalk = new SaicDataWalk(arg.vin, arg.date, arg.mode);
        ds.fetch(arg.vin, arg.date, dataWalk);
    }
}
