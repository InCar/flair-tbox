package com.incarcloud.saic.ds;

import java.time.LocalDate;

/**
 * 数据源2017
 */
public interface ISource2017 {
    /**
     * 初始化
     */
    void init();

    /**
     * 清理
     */
    void clean();

    /**
     * 从数据源读取指定的数据,并调用传入的dataWalker进行处理
     */
    void fetch(String vin, LocalDate date, IDataWalk dataWalk);
}
