package com.github.sparkzxl.drools.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.drools.KieClient;
import com.github.sparkzxl.drools.properties.DroolsProerties;
import com.github.sparkzxl.drools.service.DroolsRuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * description: 规则重载实现类
 *
 * @author: fin-9062
 * @date: 2020-12-15 10:56:12
*/
@Slf4j
@AllArgsConstructor
public class DroolsRuleServiceImpl implements DroolsRuleService {

    private final DroolsProerties droolsProerties;

    @Override
    public boolean reloadRule() {
        KieServices kieServices = getKieServices();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        try {
            for (Resource file : getRuleFiles()) {
                kfs.write(ResourceFactory.newClassPathResource(droolsProerties.getRulesPath() + file.getFilename(), "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("reload新规则重载失败：message：{}", ExceptionUtil.getMessage(e));
            return false;
        }
        reloadRules(kieServices, kfs);
        log.info("reload新规则重载成功");
        System.out.println("reload新规则重载成功");
        return true;
    }

    private void reloadRules(KieServices kieServices, KieFileSystem kfs) {
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }
        KieClient.setKieContainer(kieServices.newKieContainer(getKieServices().getRepository().getDefaultReleaseId()));
    }

    @Override
    public boolean reloadRule(String fileName) {
        KieServices kieServices = getKieServices();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(ResourceFactory.newClassPathResource(droolsProerties.getRulesPath() + fileName, "UTF-8"));
        reloadRules(kieServices, kfs);
        return false;
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    private Resource[] getRuleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + droolsProerties.getRulesPath() + "**/*.*");
    }
}
