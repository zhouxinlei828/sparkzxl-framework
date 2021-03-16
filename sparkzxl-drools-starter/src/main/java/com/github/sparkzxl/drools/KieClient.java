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
    private static KieServices kieServices;
    private static KieBase kieBase;

    private static KieContainer kieContainer;

    private static KieContainerSessionsPool kieContainerSessionsPool;

    public static KieServices getKieServices() {
        return kieServices;
    }

    public static void setKieServices(KieServices kieServices) {
        KieClient.kieServices = kieServices;
    }

    public static KieSession getKieSession() {
        return kieContainerSessionsPool.newKieSession("ksession-rule");
    }

    public static void setKieSession(KieSession kieSession) {
    }

    public static KieBase getKieBase() {
        return kieBase;
    }

    public static void setKieBase(KieBase kieBase) {
        KieClient.kieBase = kieBase;
    }

    public static KieSession getKieSession(String agendaGroupName) {
        KieSession kieSession = getKieSession();
        kieSession.getAgenda().getAgendaGroup(agendaGroupName).setFocus();
        return kieSession;
    }

    public static KieSession getKieSessionByName(String sessionName) {
        return kieContainerSessionsPool.newKieSession(sessionName);
    }

    public static KieContainerSessionsPool getKieContainerSessionsPool() {
        return kieContainerSessionsPool;
    }

    public static void setKieContainerSessionsPool(KieContainerSessionsPool kieContainerSessionsPool) {
        KieClient.kieContainerSessionsPool = kieContainerSessionsPool;
    }

    public static KieContainer getKieContainer() {
        return kieContainer;
    }

    public static void setKieContainer(KieContainer kieContainer) {
        KieClient.kieContainer = kieContainer;
    }
}
