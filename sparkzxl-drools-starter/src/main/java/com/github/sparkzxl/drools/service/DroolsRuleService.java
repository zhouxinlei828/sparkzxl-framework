package com.github.sparkzxl.drools.service;


/**
 * description: 规则重载接口
 *
 * @author zhouxinlei
 */
public interface DroolsRuleService {
    /**
     * 重新加载规则
     *
     * @return boolean
     */
    boolean reloadRule();

    /**
     * 根据文件名重新加载规则
     *
     * @param fileName
     * @return boolean
     */
    boolean reloadRule(String fileName);
}
