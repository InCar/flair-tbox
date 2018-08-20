package com.incarcloud.saic.t2017;

import com.incarcloud.saic.heliosphere.Hourglass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 任务参数
 */
public class TaskArg extends TaskArgBase {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    final LocalDate date;
    final String vin;
    final String mode;

    private volatile long total = 0;
    private volatile long idx = 0;

    public TaskArg(LocalDate date, String vin, String mode, Hourglass hourglass){
        super(hourglass);

        this.date = date;
        this.vin = vin;
        this.mode = mode;
    }

    // 进度指示
    void updateTotal(long val){ total = val; }
    void updateIdx(long val){ idx = val; }

    @Override
    public String toString(){
        return String.format("TaskArg@%s %s %s %s %d/%d",
                String.format("%08x", hashCode()),
                vin, date.format(s_fmt), mode,
                idx, total);
    }
}
