package com.github.sparkzxl.mybatis.properties;

import com.baidu.fsg.uid.buffer.RejectedPutBufferHandler;
import com.baidu.fsg.uid.buffer.RejectedTakeBufferHandler;
import com.baidu.fsg.uid.buffer.RingBuffer;
import com.baomidou.mybatisplus.annotation.DbType;
import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import com.github.sparkzxl.mybatis.constant.SqlConditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: CustomMybatisProperties配置属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = ConfigurationConstant.DATA_PREFIX
)
public class DataProperties {

    private long workerId = 0;

    private long dataCenterId = 10;

    /**
     * 使用多租户
     */
    private boolean enableTenant;

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.COLUMN;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "sparkzxl_auth";
    /**
     * 租户字段
     */
    private List<GlobalMetaData> globalColumn = Lists.newArrayList(new GlobalMetaData("tenant_id", "tenantId"));

    /**
     * 租户默认不填充数据表
     */
    private String[] ignoreTable;

    /**
     * 是否启用控制台sql记录
     */
    private Boolean p6spy = false;
    /**
     * 溢出总页数后是否进行处理
     */
    protected Boolean overflow = true;
    /**
     * 生成 countSql 优化掉 join 现在只支持 left join
     */
    protected Boolean optimizeJoin = true;
    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;
    /**
     * 分页大小限制
     */
    private long maxLimit = -1;
    /**
     * 是否启用  sql性能规范插件
     */
    private Boolean isIllegalSql = false;
    /**
     * 是否启用 防止全表更新与删除插件
     */
    private Boolean isBlockAttack = false;

    /**
     * id 类型
     */
    private IdTypeEnum idType = IdTypeEnum.HU_TOOL;
    /**
     * 百度 uid 生成策略
     */
    private CacheId cacheId = new CacheId();
    private DefaultId defaultId = new DefaultId();
    private HuToolId hutoolId = new HuToolId();


    /**
     * 使用数据权限
     */
    private boolean enableDataScope;

    /**
     * 使用数据权限
     */
    private List<DataScope> dataScopeList;


    @Data
    public static class HuToolId {
        /**
         * 终端ID (0-31)      单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long workerId = 0L;
        /**
         * 数据中心ID (0-31)   单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long dataCenterId = 0L;
    }


    /**
     * 参数说明参考：https://github.com/baidu/uid-generator
     * 长期运行：重启频率为12次/天, 那么配置成{"workerBits":23,"timeBits":31,"seqBits":9}时, 可支持28个节点以整体并发量14400 UID/s的速度持续运行68年
     * 长期运行+集群数量少+并发稍高：{"workerBits":20,"timeBits":31,"seqBits":12}这样的配置可以68年，100W次重启，单机每秒4096个并发的情况
     * 频繁重启：重启频率为24*12次/天, 那么配置成{"workerBits":27,"timeBits":30,"seqBits":6}时, 可支持37个节点以整体并发量2400 UID/s的速度持续运行34年.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CacheId extends DefaultId {

        /**
         * RingBuffer size扩容参数, 可提高UID生成的吞吐量.
         * 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
         */
        private int boostPower = 3;
        /**
         * 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
         * 举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512.
         * 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
         */
        private int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;
        /**
         * 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
         * 默认:不配置此项, 即不实用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
         */
        private Long scheduleInterval = 5 * 60L;
        /**
         * 拒绝策略: 当环已满, 无法继续填充时
         * 默认无需指定, 将丢弃Put操作, 仅日志记录. 如有特殊需求, 请实现RejectedPutBufferHandler接口(支持Lambda表达式)
         */
        private Class<? extends RejectedPutBufferHandler> rejectedPutBufferHandlerClass;
        /**
         * 拒绝策略: 当环已空, 无法继续获取时
         * 默认无需指定, 将记录日志, 并抛出UidGenerateException异常. 如有特殊需求, 请实现RejectedTakeBufferHandler接口(支持Lambda表达式)
         */
        private Class<? extends RejectedTakeBufferHandler> rejectedTakeBufferHandlerClass;
    }


    @Data
    public static class DefaultId {

        /**
         * 当前时间，相对于时间基点"${epochStr}"的增量值，单位：秒，
         * 28: 大概可以使用 8.7年, 28位即最大表示2^28的数值的秒数
         * 30: 大概可以使用 34年, 30位即最大表示2^30的数值的秒数
         * 31: 大概可以使用 68年, 31位即最大表示2^31的数值的秒数
         */
        private int timeBits = 31;
        /**
         * 机器id，
         * <p>
         * 20：100W次重启
         * 22: 最多可支持约420w次机器启动。内置实现为在启动时由数据库分配。420w = 2^22
         * 23：800w次重启  12次/天
         * 27: 1.3亿次重启 24*12次/天
         */
        private int workerBits = 23;
        /**
         * 每秒下的并发序列，13 bits可支持每秒8192个并发，即2^13个并发
         * 9: 512 并发
         * 13: 8192 并发
         */
        private int seqBits = 9;
        /**
         * Customer epoch, unit as second. For example 2016-05-20 (ms: 1463673600000)
         * 可以改成你的项目开始开始的时间
         */
        private String epochStr = "2021-09-26";
    }

    /**
     * 行级元数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalMetaData {

        private String column;
        private String loadKey;
    }

    /**
     * 行级元数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataScope {
        /**
         * 数据权限id标识
         */
        private String scopeId;
        /**
         * 数据权限字段
         */
        private String column;
        /**
         * SQL类型
         */
        private SqlCommandType sqlCommandType;
        /**
         * 条件类型
         */
        private SqlConditions condition;
        /**
         * 表名
         */
        private String tableName;
        /**
         * 查询key值
         * 查询key值
         */
        private String loadKey;
    }

}
