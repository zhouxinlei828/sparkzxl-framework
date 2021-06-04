package com.github.sparkzxl.log.realtime;

import com.github.sparkzxl.core.utils.StrPool;
import com.github.sparkzxl.log.properties.LogProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * description: 文件日志监听
 *
 * @author zhouxinlei
 * @date 2021-06-04 10:46:25
 */
public class FileLogListening implements ApplicationContextAware {

    /**
     * 上次文件大小
     */
    private long lastTimeFileSize = 0;

    private LogProperties logProperties;

    public void setLogProperties(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String logPath = logProperties.getFile().getPath();
        if (StringUtils.lastIndexOf(StrPool.SLASH, logPath) != -1) {
            logPath = logPath.substring(0, logPath.length() - 1);
        }
        String applicationName = applicationContext.getApplicationName();
        logPath = logPath.concat("/").concat(applicationName).concat(".log");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                3,
                0,
                NANOSECONDS,
                new LinkedBlockingDeque<>(1000),
                new CustomizableThreadFactory());
        try {
            File logFile = ResourceUtils.getFile(logPath);
            final RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
            threadPoolExecutor.execute(() -> {
                try {
                    randomFile.seek(lastTimeFileSize);
                    String tmp;
                    while ((tmp = randomFile.readLine()) != null) {
                        String log = new String(tmp.getBytes("ISO8859-1"));
                        LoggerDisruptorQueue.publishEvent(log);
                    }
                    lastTimeFileSize = randomFile.length();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 监听日志文件
     *
     * @throws IOException
     */
    @PostConstruct
    public void start() throws IOException {

    }
}
