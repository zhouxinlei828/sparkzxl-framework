package com.github.sparkzxl.drools.executor;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.drools.KieClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.util.List;

/**
 * description: drools 规则执行器
 *
 * @author zhouxinlei
 */
@Slf4j
public class DroolsRuleExecutor {

    public <T> Object executeRule(String agendaGroup, String resultQuery, String resultVariable, String className, List<T> tList) {
        KieSession kieSession = KieClient.getKieContainer().newKieSession();
        kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
        kieSession.addEventListener(new RuleRuntimeEventListener() {

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                log.info("Object--objectUpdated {}", event.getObject().toString());
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                log.info("Object--objectInserted {}", event.getObject().toString());
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                log.info("Object--objectDeleted {}", event.getOldObject().toString());
            }
        });
        if (CollectionUtils.isNotEmpty(tList)) {
            tList.forEach(kieSession::insert);
        }
        int ruleFiredCount = kieSession.fireAllRules();
        QueryResults results = kieSession.getQueryResults(resultQuery);
        try {
            Class rClass = Class.forName(className);
            Object newInstance = rClass.newInstance();
            for (QueryResultsRow row : results) {
                newInstance = row.get("$".concat(resultVariable));
            }
            kieSession.dispose();
            log.info("命中了 {} 条drools规则", ruleFiredCount);
            return newInstance;
        } catch (Exception exception) {
            log.error("drools查询结果发生异常 massage：{}", ExceptionUtil.getMessage(exception));
            return null;
        }

    }
}
