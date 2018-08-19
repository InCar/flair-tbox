package com.incarcloud.saic.t2017;

import com.incarcloud.saic.ds.IDataWalk;

import java.time.LocalDate;

/**
 * 上汽数据处理
 */
class SaicDataWalk implements IDataWalk {

    SaicDataWalk(String vin, LocalDate date, String mode){

    }

    /**
     * 在开始时调用一次
     */
    public void onBegin(){

    }

    /**
     * 每条数据调用一次
     * 在开始和结束之间可能会被调用多次
     * 也可能一次都不调用
     */
    public void onData(Object data){

    }

    /**
     * 在正常结束时调用一次
     * 它和onFailed相互排斥,两者只有一个会被调用
     */
    public void onFinished(){

    }

    /**
     * 在异常结束时调用一次
     * 它和onFinished相互排斥,两者只有一个会被调用
     */
    public void onFailed(){

    }
}
