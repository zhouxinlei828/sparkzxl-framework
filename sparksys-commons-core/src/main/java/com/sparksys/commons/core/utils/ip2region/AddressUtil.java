package com.sparksys.commons.core.utils.ip2region;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;

/*
 * description：
 *
 * @author zhouxinlei
 * @date  2020/6/17 0017
 */

@Slf4j
public class AddressUtil {

    static DbConfig config = null;
    static DbSearcher searcher = null;

    public AddressUtil() {
    }

    public static String getRegion(String ip) {
        try {
            if (searcher != null && !StrUtil.isEmpty(ip)) {
                long startTime = System.currentTimeMillis();
                int algorithm = 3;
                Method method = null;
                switch (algorithm) {
                    case 1:
                        method = searcher.getClass().getMethod("btreeSearch", String.class);
                        break;
                    case 2:
                        method = searcher.getClass().getMethod("binarySearch", String.class);
                        break;
                    case 3:
                        method = searcher.getClass().getMethod("memorySearch", String.class);
                        break;
                    default:
                        break;
                }

                DataBlock dataBlock;
                if (!Util.isIpAddress(ip)) {
                    log.warn("warning: Invalid ip address");
                }

                dataBlock = (DataBlock) method.invoke(searcher, ip);
                String result = dataBlock.getRegion();
                long endTime = System.currentTimeMillis();
                log.debug("region use time[{}] result[{}]", endTime - startTime, result);
                return result;
            } else {
                log.error("DbSearcher is null");
            }
        } catch (Exception var9) {
            log.error("error:{}", var9);
        }
        return "";
    }

    static {
        try {
            String dbPath = "./data/ip2region/ip2region.db";
            File file = new File(dbPath);
            if (!file.exists()) {
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                dbPath = tmpDir + "ip2region.db";
                file = new File(dbPath);
                String classPath = "classpath:ip2region/ip2region.db";
                InputStream resourceAsStream = ResourceUtil.getStreamSafe(classPath);
                if (resourceAsStream != null) {
                    FileUtils.copyInputStreamToFile(resourceAsStream, file);
                }
            }

            config = new DbConfig();
            searcher = new DbSearcher(config, dbPath);
        } catch (Exception var5) {
            log.error("init ip region error:{}", var5);
        }

    }

    public static void main(String[] args) {
        String location = AddressUtil.getRegion("183.159.126.42");
        System.out.println(location);
    }
}
