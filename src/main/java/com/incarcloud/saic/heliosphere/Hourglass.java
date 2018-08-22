package com.incarcloud.saic.heliosphere;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 进度计量
 */
public class Hourglass {
    private long totalDays;
    private long totalVins;

    private long finishedDays = 0L;
    private long finishedVins = 0L;

    private AtomicInteger atomPerfCount = new AtomicInteger(0);

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
    public synchronized void increaseFinishedDay(){
        finishedVins = 0L;
        finishedDays++;
    }

    /**
     * 完成车辆数+1
     */
    public synchronized void increaseFinishedVin(){
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

    /**
     * 数据数性能计数
     */
    public void increasePerfCount(){
        atomPerfCount.incrementAndGet();
    }

    /**
     * 性能计算
     */
    private long tmMark = System.currentTimeMillis();
    public float calcPerfAndReset(){
        long tmNow = System.currentTimeMillis();
        float fPerfHz = 1000.0f * atomPerfCount.getAndSet(0) / (tmNow - tmMark);
        tmMark = tmNow;
        return fPerfHz;
    }
}
