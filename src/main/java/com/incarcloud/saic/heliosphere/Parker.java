package com.incarcloud.saic.heliosphere;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 执行数据转换服务
 */
public class Parker {
    private static final Logger s_logger = LoggerFactory.getLogger(Parker.class);

    private LocalDate beginDate;
    private LocalDate endDate;

    private Thread thread = null;

    // 进度计算
    private Hourglass hourglass = new Hourglass();

    // 设置起止日期
    public void setDate(LocalDate begin, LocalDate end){
        beginDate = begin;
        endDate = end;
    }

    // 执行
    public void runAsync() throws Exception{
        if(thread != null)
            throw new IllegalThreadStateException();

        long totalDays = beginDate.until(endDate, ChronoUnit.DAYS) + 1;

        thread = new Thread(()->{
            for(long i=0;i<totalDays;i++){
                LocalDate cursor = beginDate.plusDays(i);
                hourglass.increaseFinishedDay();
                s_logger.info("x -> {} {}", cursor,
                        String.format("%6.2f%%", 100.0f * hourglass.getProgress()));
            }
        });

        hourglass.setTotalAmount(totalDays, 100);

        thread.start();
    }
}
