package com.incarcloud.saic.modes;

import com.incarcloud.saic.modes.AS24.AS24;

/**
 * 映射算法工厂
 */
public class ModeFactory {
    public static Mode create(String mode){
        Mode obj = null;
        switch (mode){
            case "AS24": obj = new AS24(); break;
            default:
                throw new UnsupportedOperationException("mode");
        }
        return obj;
    }
}
