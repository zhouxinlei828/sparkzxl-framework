package com.github.sparkzxl.log.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * description: 日志配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.LOG_PREFIX)
public class LogProperties {

    /**
     * 是否开启控制台输出
     */
    private boolean enableConsole = true;

    @NestedConfigurationProperty
    private FileProperties file = new FileProperties();

    @NestedConfigurationProperty
    private KafkaProperties kafka = new KafkaProperties();

    /**
     * 日志告警
     */
    @NestedConfigurationProperty
    private AlarmProperties alarm = new AlarmProperties();


    /**
     * description: 日志文件配置类
     *
     * @author zhouxinlei
     */
    @Getter
    @Setter
    public static class FileProperties {

        /**
         * 是否开启日志持久化
         */
        private boolean enable;

        /**
         * 是否开启日志json化存储
         */
        private boolean enableJson;

        private String name;

        private boolean cleanHistoryOnStart;

        private String totalSizeCap = "10GB";

        private String path = System.getProperty("user.home").concat("/logs");

        private int maxHistory = 7;

        private String maxFileSize = "10MB";

    }


    /**
     * description: kafka配置类
     *
     * @author zhouxinlei
     */
    @Getter
    @Setter
    public static class KafkaProperties {

        /**
         * kafka是否开启
         */
        private boolean enable;

        /**
         * kafka地址
         */
        private String servers;

        /**
         * kafka topic
         */
        private String topic;

        /**
         * kafka topic
         */
        private int linger;

        /**
         * ack确认机制
         * <p>
         * 生产者要求领导者在考虑完成请求之前收到的确认数量。这控制了发送的记录的持久性。允许以下设置：
         * <ul>
         *     <li><code>acks=0</code> 如果设置为0，则生产者根本不会等待来自服务器的任何确认。该记录将立即添加到套接字缓冲区并被视为已发送。这种情况下不能保证服务端已经收到记录， <code>retries</code> 配置也不会生效（因为客户端一般不会知道任何失败）。每条记录返回的偏移量将始终设置为 <code>-1</code>。
         * <li><code>acks=1</code> 这意味着领导者会将记录写入其本地日志，但会在不等待所有追随者的完全确认的情况下做出响应。在这种情况下，如果领导者在确认记录后但在追随者复制它之前立即失败，那么记录将丢失。
         * <li><code>acks=all</code> 这意味着领导者将等待完整的同步副本集来确认记录。这保证了只要至少一个同步副本保持活动状态，记录就不会丢失。这是最有力的保证。这等效于 acks=-1 设置。</ul>
         * 请注意，启用幂等性要求此配置值为“all”。如果设置了冲突配置并且没有显式启用幂等性，则禁用幂等性。
         * </P>
         */
        private String acks;

        /**
         * kafka topic
         * 配置控制 <code>KafkaProducer.send()</code> 和 <code>KafkaProducer.partitionsFor()</code> 将阻塞多长时间。
         * 这些方法可能因为缓冲区已满或元数据不可用而被阻止。用户提供的序列化程序或分区程序中的阻塞将不计入此超时。
         */
        private int maxBlock;

    }


    /**
     * description: 日志告警全局配置
     *
     * @author zhoux
     */
    @Getter
    @Setter
    public static class AlarmProperties {

        private boolean enabled = false;

        private boolean printStackTrace = false;

        private boolean simpleWarnInfo = false;

        private boolean warnExceptionExtend = false;

        private List<Class<? extends Throwable>> doWarnException;
    }

}
