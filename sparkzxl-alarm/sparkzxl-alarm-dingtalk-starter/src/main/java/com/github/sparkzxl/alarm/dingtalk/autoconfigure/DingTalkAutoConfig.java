package com.github.sparkzxl.alarm.dingtalk.autoconfigure;

import com.github.sparkzxl.alarm.dingtalk.executor.DingTalkAlarmExecutor;
import com.github.sparkzxl.alarm.dingtalk.sign.DingTalkAlarmSignAlgorithm;
import com.github.sparkzxl.alarm.dingtalk.strategy.ActionCardMsgHandleStrategy;
import com.github.sparkzxl.alarm.dingtalk.strategy.ImageTextMsgHandleStrategy;
import com.github.sparkzxl.alarm.dingtalk.strategy.MarkdownMsgHandleStrategy;
import com.github.sparkzxl.alarm.dingtalk.strategy.MsgLinkHandleStrategy;
import com.github.sparkzxl.alarm.dingtalk.strategy.TextMsgHandleStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * description: 钉钉告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.dingtalk.enabled", havingValue = "true")
@Configuration
public class DingTalkAutoConfig {

    /**
     * DingTalk签名算法
     *
     * @return DingTalkAlarmSignAlgorithm
     */
    @Bean
    public DingTalkAlarmSignAlgorithm dingTalkAlarmSignAlgorithm() {
        return new DingTalkAlarmSignAlgorithm();
    }

    /**
     * 钉钉告警执行器
     *
     * @return DingTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(DingTalkAlarmExecutor.class)
    public DingTalkAlarmExecutor dingTalkAlarmExecutor(DingTalkAlarmSignAlgorithm dingTalkAlarmSignAlgorithm) {
        return new DingTalkAlarmExecutor(dingTalkAlarmSignAlgorithm);
    }

    @Configuration
    static class MessageAutoConfig {

        private final String MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME = "dingtalk";

        @Bean(name = MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME + "ActionCardMsgHandleStrategy")
        @Lazy
        @ConditionalOnMissingBean(ActionCardMsgHandleStrategy.class)
        public ActionCardMsgHandleStrategy actionCardMsgHandleStrategy() {
            return new ActionCardMsgHandleStrategy();
        }

        @Bean(name = MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME + "ImageTextMsgHandleStrategy")
        @Lazy
        @ConditionalOnMissingBean(ImageTextMsgHandleStrategy.class)
        public ImageTextMsgHandleStrategy imageTextMsgHandleStrategy() {
            return new ImageTextMsgHandleStrategy();
        }

        @Bean(name = MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME + "MarkdownMsgHandleStrategy")
        @Lazy
        @ConditionalOnMissingBean(MarkdownMsgHandleStrategy.class)
        public MarkdownMsgHandleStrategy markdownMsgHandleStrategy() {
            return new MarkdownMsgHandleStrategy();
        }

        @Bean(name = MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME + "MsgLinkHandleStrategy")
        @Lazy
        @ConditionalOnMissingBean(MsgLinkHandleStrategy.class)
        public MsgLinkHandleStrategy msgLinkHandleStrategy() {
            return new MsgLinkHandleStrategy();
        }

        @Bean(name = MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME + "TextMsgHandleStrategy")
        @Lazy
        @ConditionalOnMissingBean(TextMsgHandleStrategy.class)
        public TextMsgHandleStrategy textMsgHandleStrategy() {
            return new TextMsgHandleStrategy();
        }

    }

}
