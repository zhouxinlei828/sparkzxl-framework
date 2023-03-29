package com.github.sparkzxl.alarm.feishutalk.autoconfigure;

import com.github.sparkzxl.alarm.feishutalk.executor.FeiShuTalkAlarmExecutor;
import com.github.sparkzxl.alarm.feishutalk.sign.FeiShuTalkAlarmSignAlgorithm;
import com.github.sparkzxl.alarm.feishutalk.strategy.ActionCardMsgHandleStrategy;
import com.github.sparkzxl.alarm.feishutalk.strategy.ImageTextMsgHandleStrategy;
import com.github.sparkzxl.alarm.feishutalk.strategy.MarkdownMsgHandleStrategy;
import com.github.sparkzxl.alarm.feishutalk.strategy.MsgLinkHandleStrategy;
import com.github.sparkzxl.alarm.feishutalk.strategy.TextMsgHandleStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * description: 飞书告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.feishu.enabled", havingValue = "true")
@Configuration
public class FeiShuTalkAutoConfig {

    /**
     * FeiShuTalk签名算法
     *
     * @return FeiShuTalkAlarmSignAlgorithm
     */
    @Bean
    public FeiShuTalkAlarmSignAlgorithm feiShuTalkAlarmSignAlgorithm() {
        return new FeiShuTalkAlarmSignAlgorithm();
    }

    /**
     * 飞书告警执行器
     *
     * @return FeiShuTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(FeiShuTalkAlarmExecutor.class)
    public FeiShuTalkAlarmExecutor feiShuTalkAlarmExecutor(FeiShuTalkAlarmSignAlgorithm feiShuTalkAlarmSignAlgorithm) {
        return new FeiShuTalkAlarmExecutor(feiShuTalkAlarmSignAlgorithm);
    }

    @Configuration
    static class MessageAutoConfig {

        private final String MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME = "feishu";

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
