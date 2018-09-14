package com.incarcloud.saic.json;

/**
 * @author GuoKun
 * @version 1.0
 * @create_date 2018/9/13 10:34
 */
public class JsonExcursion {
    public JsonExcursion(int start, int end, int length) {
        this.start = start;
        this.end = end;
        this.length = length;
    }

    private int start;
    private int end;
    private int length;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
