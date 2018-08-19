package com.incarcloud.saic.t2017;

import java.time.LocalDate;

/**
 * 任务参数
 */
public class TaskArg {
    final LocalDate date;
    final String vin;
    final String mode;

    public TaskArg(LocalDate date, String vin, String mode){
        this.date = date;
        this.vin = vin;
        this.mode = mode;
    }
}
