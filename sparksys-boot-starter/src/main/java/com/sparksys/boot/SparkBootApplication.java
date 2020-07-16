package com.sparksys.boot;

import com.sparksys.core.utils.SpringContextUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * description: sparksys 启动类
 *
 * @author: zhouxinlei
 * @date: 2020-07-15 21:49:59
*/
@Import(SpringContextUtils.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SparkBootApplication {

}
