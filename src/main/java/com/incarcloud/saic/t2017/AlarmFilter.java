package com.incarcloud.saic.t2017;

import com.incarcloud.auxiliary.Helper;
import com.incarcloud.lang.Action;
import com.incarcloud.saic.GB32960.GBx07Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 告警过滤器
 * 按时间顺序依次通过告警原始数据包
 * 过滤掉不符合条件的告警
 */
public class AlarmFilter {// 数目
    private static final int ALARM_2S = 16;
    // 报警持续时间门限2秒
    private static final int s_tmThresholdLastSeconds = 2;
    private static Logger s_logger = LoggerFactory.getLogger(AlarmFilter.class);

    private final String vin;
    // 报警开始时间
    private final ZonedDateTime[] tmAlarmStart = new ZonedDateTime[ALARM_2S];
    // 持续时间要求
    private final List<Callable<Byte>> listFnGet = new ArrayList<>(ALARM_2S);
    private final List<Action<Byte>> listFnSet = new ArrayList<>(ALARM_2S);

    public AlarmFilter(String vin){
        this.vin = vin;
        for(int i=0;i<ALARM_2S;i++) tmAlarmStart[i] = null;
    }

    /**
     * 过滤掉持续时间不足的告警
     */
    public GBx07Alarm filter(GBx07Alarm alarm){

        try {
            // vin不匹配的直接忽略
            if (alarm.getVin().equals(this.vin)) {
                // 16个需要操持2秒才算的报警
                makeFuncs(alarm, listFnGet, listFnSet);
                // 过滤掉持续时间不足的告警
                alarm = filterByTime(alarm);
                // 过滤掉一个报警数据也没有的
                alarm = filterByNotAlarm(alarm);
                return alarm;
            }
        }catch (Exception ex){
            s_logger.error("filter alarm failed: {}", Helper.printStackTrace(ex));
        }
        finally {
            // 清理
            clearFuncs(listFnGet, listFnSet);
        }

        return null;
    }

    // 过滤掉持续时间不足的告警
    private GBx07Alarm filterByTime(GBx07Alarm alarm) throws Exception{
        for (int i = 0; i < ALARM_2S; i++) {
            Byte val = listFnGet.get(i).call();
            // 记录初始报警时间
            if(tmAlarmStart[i] == null && (!val.equals(0x00))){
                tmAlarmStart[i] = alarm.getTmGMT8();
                // 还没满足持续时间,暂不设置报警
                listFnSet.get(i).run((byte)0x00);
            }
            else if(val.equals(0x00)){
                // 消除报警
                tmAlarmStart[i] = null;
            }
            else if(tmAlarmStart[i].until(alarm.getTmGMT8(), ChronoUnit.MILLIS) < 1000*s_tmThresholdLastSeconds){
                // 还没满足持续时间,暂不设置报警
                listFnSet.get(i).run((byte)0x00);
            }
        }

        return alarm;
        
    }

    // 过滤掉一个报警数据也没有的
    private GBx07Alarm filterByNotAlarm(GBx07Alarm alarm) throws Exception{
        byte maxLevel = 0x00;
        for(Callable<Byte> fn : listFnGet){
            if(fn.call() > maxLevel) maxLevel = fn.call();
        }

        // 3个不需要持续2秒的
        if(alarm.getBatterySysDismatch() > maxLevel) maxLevel = alarm.getBatterySysDismatch();
        if(alarm.getSocJumpAlarm() > maxLevel) maxLevel = alarm.getSocJumpAlarm();
        if(alarm.getSellVolHighestChargerl() > maxLevel) maxLevel = alarm.getSellVolHighestChargerl();

        alarm.setMaxLevel(maxLevel);

        // 任意1个有报警,则返回报警
        if(maxLevel > 0) return alarm;

        if(alarm.getDeviceFaultCount() > 0 && alarm.getDeviceFaultCount() != (byte)0xff) return alarm;
        if(alarm.getMotorFaultCount() > 0 && alarm.getMotorFaultCount() != (byte)0xff) return alarm;
        if(alarm.getEngineFaultCount() > 0 && alarm.getEngineFaultCount() != (byte)0xff) return alarm;

        // 啥报警也没有
        return null;
    }

    // 需要持续2秒的报警
    private static void makeFuncs(GBx07Alarm alarm, List<Callable<Byte>> listFnGet, List<Action<Byte>> listFnSet){
        listFnGet.add(alarm::getTempPlusHigherl);
        listFnSet.add(alarm::setTempPlusHigherl);
        listFnGet.add(alarm::getTempratureHighestl);
        listFnSet.add(alarm::setTempratureHighestl);
        listFnGet.add(alarm::getTotalVolHighestl);
        listFnSet.add(alarm::setTotalVolHighestl);
        listFnGet.add(alarm::getTotalVolLowestl);
        listFnSet.add(alarm::setTotalVolLowestl);

        listFnGet.add(alarm::getSocLowerl);
        listFnSet.add(alarm::setSocLowerl);
        listFnGet.add(alarm::getSellVolHighestChargerl);
        listFnSet.add(alarm::setSellVolHighestChargerl);
        listFnGet.add(alarm::getSellVolLowestl);
        listFnSet.add(alarm::setSellVolLowestl);
        listFnGet.add(alarm::getSocHigherAlarm);
        listFnSet.add(alarm::setSocHigherAlarm);

        listFnGet.add(alarm::getVolPlusBiggerl);
        listFnSet.add(alarm::setVolPlusBiggerl);
        listFnGet.add(alarm::getInsuLowl);
        listFnSet.add(alarm::setInsuLowl);
        listFnGet.add(alarm::getDcdcTempAlarm);
        listFnSet.add(alarm::setDcdcTempAlarm);
        listFnGet.add(alarm::getIcuBrakeSysErr);
        listFnSet.add(alarm::setIcuBrakeSysErr);

        listFnGet.add(alarm::getDcdcStatusAlarm);
        listFnSet.add(alarm::setDcdcStatusAlarm);
        listFnGet.add(alarm::getIsMotorControlerTempHigh);
        listFnSet.add(alarm::setIsMotorControlerTempHigh);
        listFnGet.add(alarm::getIsLockHigh);
        listFnSet.add(alarm::setIsLockHigh);
        listFnGet.add(alarm::getIsMotorTempHigh);
        listFnSet.add(alarm::setIsMotorTempHigh);
    }

    private static void clearFuncs(List<Callable<Byte>> listFnGet, List<Action<Byte>> listFnSet){
        listFnGet.clear();
        listFnSet.clear();
    }
}
