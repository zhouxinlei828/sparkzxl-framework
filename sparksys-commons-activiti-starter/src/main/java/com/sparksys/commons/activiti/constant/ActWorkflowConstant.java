package com.sparksys.commons.activiti.constant;

/**
 * description: 工作流核心常量
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:43:28
 */
public class ActWorkflowConstant {


    /**
     * 分发节点
     */
    public static final String TASK_CLAIM = "claim";

    /**
     * 流程任务动作类型
     */
    public static class WorkflowAction {
        /**
         * 启动
         */
        public static final int START = 0;
        /**
         * 提交
         */
        public static final int SUBMIT = 1;

        /**
         * 驳回
         */
        public static final int ROLLBACK = -1;
        /**
         * 分发
         */
        public static final int CLAIM = 2;
        /**
         * 退回分发
         */
        public static final int CANCEL_CLAIM = -2;
        /**
         * 流程挂起
         */
        public static final int SUSPEND = 3;
        /**
         * 直接提交至被分发者
         */
        public static final int ROUND_CLAIM = 4;
    }

}
