package com.github.sparkzxl.drools.config;

import com.github.sparkzxl.drools.KieClient;
import com.github.sparkzxl.drools.executor.DroolsRuleExecutor;
import com.github.sparkzxl.drools.properties.DroolsProperties;
import com.github.sparkzxl.drools.service.DroolsRuleService;
import com.github.sparkzxl.drools.service.impl.DroolsRuleServiceImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-12-15 11:46:06
 */
@Configuration
@EnableConfigurationProperties({DroolsProperties.class})
public class DroolsAutoConfiguration {

    @Autowired
    private DroolsProperties droolsProperties;

    @Bean
    @ConditionalOnMissingBean(KieFileSystem.class)
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        for (Resource file : ruleFiles()) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(droolsProperties.getRulesPath() + file.getFilename(), "UTF-8"));
        }
        return kieFileSystem;
    }

    @Bean
    public Resource[] ruleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + droolsProperties.getRulesPath() + "**/*.*");
    }

    @Bean
    @ConditionalOnMissingBean(KieContainer.class)
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = getKieServices();
        final KieRepository kieRepository = kieServices.getRepository();

        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }

        kieBuilder.buildAll();

        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        KieClient.setKieContainer(kieContainer);
        return kieContainer;
    }

    @Bean
    @ConditionalOnMissingBean(KieContainerSessionsPool.class)
    public KieContainerSessionsPool kieContainerSessionsPool() throws IOException {
        return kieContainer().newKieSessionsPool(droolsProperties.getPoolSize());
    }

    private KieServices getKieServices() {
        KieClient.setKieServices(KieServices.Factory.get());
        return KieServices.Factory.get();
    }

    @Bean
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }

    @Bean
    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSession() throws IOException {
        return kieContainerSessionsPool().newKieSession();
    }

    @Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }

    @Bean
    public DroolsRuleService droolsRuleService(){
        return new DroolsRuleServiceImpl(droolsProperties);
    }

    @Bean
    public DroolsRuleExecutor droolsRuleExecutor(){
        return new DroolsRuleExecutor();
    }

}


