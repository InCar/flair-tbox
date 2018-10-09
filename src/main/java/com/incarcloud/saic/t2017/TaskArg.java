package com.incarcloud.saic.t2017;

import com.incarcloud.saic.ds.DSFactory;
import com.incarcloud.saic.heliosphere.Hourglass;
import com.incarcloud.saic.modes.ModeFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务参数
 */
public class TaskArg extends TaskArgBase {
    private static final DateTimeFormatter s_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    final LocalDate date;
    final String vin;

    final List<String> modes = new ArrayList<>();

    private volatile long total = 0;
    private volatile long idx = 0;
    private volatile long actualWritten = 0;

    public TaskArg(LocalDate date, String vin, List<String> modes, Hourglass hourglass){
        super(hourglass);

        this.date = date;
        this.vin = vin;
        this.modes.addAll(modes);
    }

    // 进度指示
    void updateTotal(long val){ total = val; }
    void updateIdx(long val){ idx = val; }
    void updateActualWritten(long val){ actualWritten = val; }

    @Override
    public String toString(){
        return String.format("TaskArg@%s %s %s %s %d/%d/%d",
                String.format("%08x", hashCode()),
                vin, date.format(s_fmt), modes,
                actualWritten, idx, total);
    }
}
