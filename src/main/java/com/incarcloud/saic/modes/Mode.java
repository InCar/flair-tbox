package com.incarcloud.saic.modes;

import com.incarcloud.saic.GB32960.DataGBx01;

/**
 * 把数据转换为GB32960数据
 */
public abstract class Mode {

    // TODO: 先暂定这个,稍后按其他项目的GB32960里代码命名
    public abstract DataGBx01 makeGBx01(Object data);
}
