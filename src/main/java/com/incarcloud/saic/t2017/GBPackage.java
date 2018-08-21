package com.incarcloud.saic.t2017;

/**
 * 用于按tm排序输出
 */
class GBPackage implements Comparable<GBPackage> {
    final String tm; // 时间戳
    final String val; // 数据

    GBPackage(String tm, String val){
        this.tm = tm;
        this.val = val;
    }

    @Override
    public int compareTo(GBPackage o) {
        int cmpTm = this.tm.compareTo(o.tm);
        if(cmpTm != 0) return cmpTm;
        else return val.hashCode() - o.val.hashCode();
    }
}
