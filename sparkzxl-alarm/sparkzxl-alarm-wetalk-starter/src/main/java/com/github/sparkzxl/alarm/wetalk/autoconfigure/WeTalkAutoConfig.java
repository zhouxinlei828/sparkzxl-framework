package com.github.sparkzxl.alarm.wetalk.autoconfigure;

import com.github.sparkzxl.alarm.wetalk.executor.WeTalkAlarmExecutor;
import com.github.sparkzxl.alarm.wetalk.strategy.ActionCardMsgHandleStrategy;
import com.github.sparkzxl.alarm.wetalk.strategy.ImageTextMsgHandleStrategy;
import com.github.sparkzxl.alarm.wetalk.strategy.MarkdownMsgHandleStrategy;
import com.github.sparkzxl.alarm.wetalk.strategy.MsgLinkHandleStrategy;
import com.github.sparkzxl.alarm.wetalk.strategy.TextMsgHandleStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * description: 企业微信告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.wetalk.enabled", havingValue = "true")
@Configuration
public class WeTalkAutoConfig {

    /**
     * 企业微信告警执行器
     *
     * @return WeTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(WeTalkAlarmExecutor.class)
    public WeTalkAlarmExecutor weTalkAlarmExecutor() {
        return new WeTalkAlarmExecutor();
    }


    @Configuration
    static class MessageAutoConfig {

        private final String MSG_HANDLE_STRATEGY_PREFIX_BEAN_NAME = "wetalk";

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
