package com.incarcloud.saic.heliosphere;

/**
 * 进度计量
 */
class Hourglass {
    private long totalDays;
    private long totalVins;

    private long finishedDays = 0L;
    private long finishedVins = 0L;

    /**
     * 设置总工作量
     * @param days 总天数
     * @param vins 总车辆数
     */
    void setTotalAmount(long days, long vins){
        totalDays = days;
        totalVins = vins;
    }

    /**
     * 完成天数+1
     */
    synchronized void increaseFinishedDay(){
        finishedVins = 0L;
        finishedDays++;
    }

    /**
     * 完成车辆数+1
     */
    synchronized void increaseFinishedVin(){
        finishedVins++;
    }

    /**
     * 计算当前进度
     */
    synchronized float getProgress(){
        float fVins = 0.0f;
        if(totalVins > 0)
            fVins = 1.0f * finishedVins / totalVins;
        float fDays = 1.0f * finishedDays / totalDays;
        return fDays + fVins / totalDays;
    }
}
