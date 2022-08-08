package com.github.sparkzxl.alarm.dingtalk.entity;

import cn.hutool.core.convert.Convert;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description: 钉钉 Link类型
 *
 * @author zhouxinlei
 * @since 2022-05-19 10:35:31
 */
public class DingTalkActionCard extends DingTalkMessage {
    /**
     * {@link ActionCard}
     */
    private ActionCard actionCard;

    public DingTalkActionCard() {
        setMsgtype(DingTalkMsgType.ACTION_CARD.type());
    }

    public DingTalkActionCard(ActionCard actionCard) {
        this();
        this.actionCard = actionCard;
    }

    public ActionCard getActionCard() {
        return actionCard;
    }

    public void setActionCard(ActionCard actionCard) {
        this.actionCard = actionCard;
    }

    @Getter
    @Setter
    public static class ActionCard implements Serializable {

        /**
         * 首屏会话透出的展示内容
         */
        private String title;
        /**
         * markdown格式的消息
         */
        private String text;

        /**
         * 0：按钮竖直排列
         * 1：按钮横向排列
         */
        private String btnOrientation;
        /**
         * 按钮集合
         */
        private List<Button> btns;

        public ActionCard(String title, String text, String btnOrientation, List<Button> btns) {
            this.title = title;
            this.text = text;
            this.btnOrientation = btnOrientation;
            this.btns = btns;
        }

        public ActionCard(String title, String text, List<Button> btns) {
            this.title = title;
            this.text = text;
            this.btns = btns;
        }

        /**
         * 按钮属性
         */
        @Getter
        @Setter
        public static class Button {

            /**
             * 按钮标题
             */
            private String title;

            /**
             * 点击按钮触发的URL，打开方式如下：
             * 1.移动端，在钉钉客户端内打开
             * 2.PC端
             * - 默认侧边栏打开
             * 希望在外部浏览器打开，请参考消息链接说明
             *
             * @see <a href="https://open.dingtalk.com/document/orgapp-server/message-link-description"/>
             */
            private String actionURL;

            public Button(String title, String actionURL) {
                this.title = title;
                this.actionURL = actionURL;
            }
        }
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof ActionCard) {
                this.actionCard = Convert.convert(ActionCard.class, entry.getValue());
                break;
            }
        }
    }
}