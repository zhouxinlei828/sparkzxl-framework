package com.github.sparkzxl.drools;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;

/**
 * description: kie工具类
 *
 * @author fin-9062
 */
public class KieClient {
    private KieServices kieServices;

    private KieBase kieBase;

    private KieContainer kieContainer;

    private KieContainerSessionsPool kieContainerSessionsPool;

    public KieServices getKieServices() {
        return this.kieServices;
    }

    public void setKieServices(KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public KieSession getKieSession() {
        return kieContainerSessionsPool.newKieSession("ksession-rule");
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public KieSession getKieSession(String agendaGroupName) {
        KieSession kieSession = getKieSession();
        kieSession.getAgenda().getAgendaGroup(agendaGroupName).setFocus();
        return kieSession;
    }

    public KieSession getKieSessionByName(String sessionName) {
        return kieContainerSessionsPool.newKieSession(sessionName);
    }

    public KieContainerSessionsPool getKieContainerSessionsPool() {
        return kieContainerSessionsPool;
    }

    public void setKieContainerSessionsPool(KieContainerSessionsPool kieContainerSessionsPool) {
        this.kieContainerSessionsPool = kieContainerSessionsPool;
    }

    public KieContainer getKieContainer() {
        return this.kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }
}
