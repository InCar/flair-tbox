package com.incarcloud.saic.ds;

/**
 * 数据巡游器
 */
public interface IDataWalk {
    /**
     * 在开始时调用一次
     * 成功返回true,失败返回false
     */
    boolean onBegin(long total);

    /**
     * 每条数据调用一次
     * 在开始和结束之间可能会被调用多次
     * 也可能一次都不调用
     * 成功返回true,失败返回false
     */
    boolean onData(Object data, long idx);

    /**
     * 在正常结束时调用一次
     * 它和onFailed相互排斥,两者只有一个会被调用
     */
    void onFinished();

    /**
     * 在异常结束时调用一次
     * 它和onFinished相互排斥,两者只有一个会被调用
     */
    void onFailed(Exception ex);
}
