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
    public boolean load(List<String> modes, String vinMatch){
        try {
            // 组织modes以便高效检索
            HashSet<String> modeSet = new HashSet<>();
            modeSet.addAll(modes);

            // vin码正则表达过滤器
            Pattern rgxVin = null;
            if(vinMatch != null && vinMatch.length() > 0)
                rgxVin = Pattern.compile(vinMatch);

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
                        // modeSet为空集代表全部型号都需要处理
                        if((modeSet.size() == 0 || modeSet.contains(mode)) && (rgxVin == null || rgxVin.matcher(vin).matches()))
                            mapVinModes.computeIfAbsent(vin, k->new ArrayList<>(4)).add(mode);
                    }
                    count++;
                }
                s_logger.info("Loaded {} from {} where {}{}",
                        mapVinModes.size(), count,
                        modes.size() > 0 ? modes : "[*]",
                        rgxVin != null ? " && " + vinMatch : "");
            }
        }
        catch (Exception ex){
            s_logger.error("Load meta file vin.txt failed: {}", Helper.printStackTrace(ex));
            return false;
        }

        return true;
    }

    public int size(){
        return mapVinModes.size();
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
