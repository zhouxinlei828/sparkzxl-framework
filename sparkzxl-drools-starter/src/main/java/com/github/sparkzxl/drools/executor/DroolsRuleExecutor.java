package com.github.sparkzxl.drools.executor;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.drools.KieClient;
import com.github.sparkzxl.drools.entity.DroolsRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description: drools 规则执行器
 *
 * @author zhouxinlei
 */
@Slf4j
@Component
@RequiredArgsConstructor
public abstract class DroolsRuleExecutor {


    private final KieClient kieClient;

    public <T> Object executeRule(DroolsRule droolsRule, List<T> tList) {
        KieSession kieSession = kieClient.getKieContainer().newKieSession();
        kieSession.getAgenda().getAgendaGroup(droolsRule.getAgendaGroup()).setFocus();
        kieSession.addEventListener(new RuleRuntimeEventListener() {
            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                log.info("Object--objectUpdated：[{}]", event.getObject().toString());
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                log.info("Object--objectInserted：[{}]", event.getObject().toString());
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                log.info("Object--objectDeleted：[{}]", event.getOldObject().toString());
            }
        });
        if (CollectionUtils.isNotEmpty(tList)) {
            tList.forEach(kieSession::insert);
        }
        try {
            int ruleFiredCount = kieSession.fireAllRules();
            log.info("命中了 [{}] 条drools规则", ruleFiredCount);
            if (StringUtils.isNotEmpty(droolsRule.getResultQuery())) {
                QueryResults results = kieSession.getQueryResults(droolsRule.getResultQuery());
                Class rClass = Class.forName(droolsRule.getClassName());
                Object newInstance = rClass.newInstance();
                for (QueryResultsRow row : results) {
                    newInstance = row.get("$".concat(droolsRule.getResultVariable()));
                }
                return newInstance;
            }
            kieSession.dispose();
        } catch (Exception exception) {
            log.error("drools查询结果发生异常 massage：[{}]", ExceptionUtil.getMessage(exception));
        }
        return null;
    }
}
