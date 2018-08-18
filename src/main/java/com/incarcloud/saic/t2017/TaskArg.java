package com.incarcloud.saic.t2017;

import java.time.LocalDate;

/**
 * 任务参数
 */
public class TaskArg {
    private final LocalDate date;
    private final String vin;
    private final String mode;

    public TaskArg(LocalDate date, String vin, String mode){
        this.date = date;
        this.vin = vin;
        this.mode = mode;
    }
}
