package com.github.sparkzxl.drools.config;

import com.github.sparkzxl.drools.KieClient;
import com.github.sparkzxl.drools.executor.DroolsRuleExecutor;
import com.github.sparkzxl.drools.properties.DroolsProperties;
import com.github.sparkzxl.drools.service.DroolsRuleService;
import com.github.sparkzxl.drools.service.impl.DroolsRuleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
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
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({DroolsProperties.class})
@RequiredArgsConstructor
public class DroolsAutoConfiguration {

    private final DroolsProperties droolsProperties;

    @Bean
    @ConditionalOnMissingBean(KieFileSystem.class)
    public KieFileSystem kieFileSystem() {
        KieFileSystem kieFileSystem = kieServices().newKieFileSystem();
        try {
            for (Resource file : ruleFiles()) {
                kieFileSystem.write(ResourceFactory.newClassPathResource(droolsProperties.getRulesPath() + file.getFilename(), "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    public KieContainer kieContainer() {
        KieServices kieServices = kieServices();
        final KieRepository kieRepository = kieServices.getRepository();

        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }

    @Bean
    @ConditionalOnMissingBean(KieContainerSessionsPool.class)
    public KieContainerSessionsPool kieContainerSessionsPool() {
        return kieContainer().newKieSessionsPool(droolsProperties.getPoolSize());
    }

    private KieServices kieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() {
        return kieContainer().getKieBase();
    }

    @Bean
    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSession() {
        return kieContainerSessionsPool().newKieSession();
    }

    @Bean
    public KieClient kieClient() {
        KieClient kieClient = new KieClient();
        kieClient.setKieContainer(kieContainer());
        kieClient.setKieServices(kieServices());
        kieClient.setKieContainerSessionsPool(kieContainerSessionsPool());
        return kieClient;
    }

    @Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }

    @Bean
    public DroolsRuleService droolsRuleService() {
        return new DroolsRuleServiceImpl(droolsProperties, kieClient());
    }

}


