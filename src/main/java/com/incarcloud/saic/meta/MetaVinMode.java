package com.incarcloud.saic.meta;

import com.incarcloud.auxiliary.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 加载车辆VIN码和车辆类型
 */
public class MetaVinMode {
    private static final Logger s_logger = LoggerFactory.getLogger(MetaVinMode.class);

    // split string like "LSJW26765GS158204	OLD-IP24MCE"
    private Pattern rgxLine = Pattern.compile("(\\S+)\\s+(\\S+)");

    // key:vin value: modes
    private SortedMap<String, List<String>> mapVinModes = new TreeMap<>();

    /**
     * 加载预定义的配置文件vin.txt
     */
    public boolean load(){
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try (InputStream fs = loader.getResourceAsStream("vin.txt");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fs))
            ) {
                String line;
                int count = 0;
                while((line = reader.readLine() )!= null){
                    Matcher m = rgxLine.matcher(line);
                    if(m.find()){
                        String vin = m.group(1);
                        String mode = m.group(2);
                        mapVinModes.computeIfAbsent(vin, k->new ArrayList<>(4)).add(mode);
                    }
                    count++;
                }
                s_logger.info("meta loaded {} lines", count);
            }
        }
        catch (Exception ex){
            s_logger.error("Load meta file vin.txt failed: {}", Helper.printStackTrace(ex));
            return false;
        }

        return true;
    }

    /**
     * 返回全部预定义的VIN码
     */
    public Set<String> getVins(){
        return mapVinModes.keySet();
    }

    public Set<Map.Entry<String, List<String>>> getVinModes(){
        return mapVinModes.entrySet();
    }
}
