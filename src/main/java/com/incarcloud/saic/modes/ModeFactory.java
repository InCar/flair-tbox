package com.incarcloud.saic.modes;

/**
 * 映射算法工厂
 */
public class ModeFactory {
    public static Mode create(String mode){
        Mode obj;
        switch (mode){
            case "AS24":
            case "AS26":
            case "IP24":
            case "IP34":
                obj = new ModeMongo(mode); break;
            default:
                throw new UnsupportedOperationException("mode");
        }
        return obj;
    }
}
