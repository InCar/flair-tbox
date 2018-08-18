package com.incarcloud.saic.heliosphere;

import com.incarcloud.concurrent.LimitedSyncArgTask;
import com.incarcloud.concurrent.LimitedTask;
import com.incarcloud.lang.Action;
import com.incarcloud.saic.config.MongoConfig;
import com.incarcloud.saic.meta.MetaVinMode;
import com.incarcloud.saic.t2017.TaskArg;
import com.incarcloud.saic.t2017.TaskWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 执行数据转换服务
 */
public class Parker {
    private static final Logger s_logger = LoggerFactory.getLogger(Parker.class);

    private LocalDate beginDate;
    private LocalDate endDate;
    private MetaVinMode metaVinMode;

    private Thread thread = null;
    private final Object objExit = new Object();
    private final LimitedSyncArgTask<TaskArg> saTask;

    // 进度计算
    private Hourglass hourglass = new Hourglass();
    // 完成回调
    private Action<Integer> actionFinished = null;

    // 工作逻辑
    private final TaskWork taskWork;

    public Parker(){
        taskWork = new TaskWork();
        saTask = new LimitedSyncArgTask<>(taskWork);
    }

    // 设置起止日期
    public void setJob(LocalDate begin, LocalDate end, MetaVinMode meta){
        beginDate = begin;
        endDate = end;
        metaVinMode = meta;
    }

    // 设置完成通知
    public void onFinished(Action<Integer> actFin){
        actionFinished = actFin;
    }

    // 设置最大并发数
    public void setMaxPower(int max){
        saTask.setMax(max);
    }

    public void setDataSourceConfig(MongoConfig cfg){
        taskWork.init(cfg);
    }

    // 执行
    public void runAsync() throws Exception{
        if(thread != null)
            throw new IllegalThreadStateException();

        thread = new Thread(this::run);
        thread.start();
    }

    private void run(){
        long totalDays = beginDate.until(endDate, ChronoUnit.DAYS) + 1;
        hourglass.setTotalAmount(totalDays, metaVinMode.size());

        for(long i=0;i<totalDays;i++){
            int count = 0;
            LocalDate cursor = beginDate.plusDays(i);
            for(Map.Entry<String, List<String>> vinModes : metaVinMode.getVinModes()){
                // TODO: 如何决断Mode???
                saTask.submit(new TaskArg(cursor, vinModes.getKey(), vinModes.getValue().get(0)));
                count++;

                // TODO: 测试代码,只处理前20个vin,配置测试MODE和测试VIN限制
                if(count>=20) break;
            }

            // 等待工作完成
            while (saTask.getWaiting() > 0 || saTask.getRunning() > 0){
                synchronized (objExit){
                    try {
                        objExit.wait(1000 * 10);
                    }catch (InterruptedException ex){
                        return;
                    }
                }

                s_logger.info("\n{}",
                        LimitedTask.printMetric(saTask, 1000*60)
                            .replaceAll("(.*)", "\t$1"));
            }

            hourglass.increaseFinishedDay();
            s_logger.info(String.format("%6.2f%%", 100.0f * hourglass.getProgress()));
        }

        saTask.stop();
        taskWork.clean();

        if(actionFinished != null)
            actionFinished.run(0);
    }
}
