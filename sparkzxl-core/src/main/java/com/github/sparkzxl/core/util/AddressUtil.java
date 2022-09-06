package com.github.sparkzxl.core.util;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * description：获取ip地址
 *
 * @author zhouxinlei
 */
@Slf4j
public class AddressUtil {

    private static Searcher searcher = null;
    private static byte[] vIndex;
    private static String dbPath;

    static {
        dbPath = Objects.requireNonNull(AddressUtil.class.getResource("/ip2region/ip2region.xdb")).getPath();
        try {
            File file = new File(dbPath);
            if (!file.exists()) {
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                dbPath = tmpDir + "ip2region/ip2region.xdb";
                file = new File(dbPath);
                String classPath = "classpath:ip2region/ip2region.xdb";
                InputStream resourceAsStream = ResourceUtil.getStreamSafe(classPath);
                if (resourceAsStream != null) {
                    FileUtils.copyInputStreamToFile(resourceAsStream, file);
                }
            }
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
            searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
        } catch (Exception e) {
            System.out.printf("failed to load vector index from `%s`: %s\n", dbPath, e);
        }

    }

    public static String getRegion(String ip) {
        try {
            Searcher searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
            if (StringUtils.isEmpty(ip)) {
                return "";
            }
            long startTime = System.currentTimeMillis();
            String result = searcher.search(ip);
            long endTime = System.currentTimeMillis();
            log.debug("region use time[{}] result[{}]", endTime - startTime, result);
            return result;
        } catch (Exception e) {
            log.error("error:[{}]", e.getMessage());
            return "";
        } finally {
            try {
                searcher.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(AddressUtil.getRegion("127.0.0.1"));
    }
}
