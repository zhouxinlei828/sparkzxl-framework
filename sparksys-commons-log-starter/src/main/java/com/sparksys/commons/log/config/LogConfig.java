package com.sparksys.commons.log.config;

import cn.hutool.json.JSONUtil;
import com.sparksys.commons.log.event.SysLogListener;
import com.sparksys.commons.log.monitor.PointUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 00:05:46
*/
public class LogConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("${zuihou.log.enabled:true} && 'LOGGER'.equals('${zuihou.log.type:LOGGER}')")
    public SysLogListener sysLogListener() {
        ;
        return new SysLogListener((log) -> PointUtil.debug("0", "OPT_LOG", JSONUtil.parseObj(log).toStringPretty()));
    }


}
