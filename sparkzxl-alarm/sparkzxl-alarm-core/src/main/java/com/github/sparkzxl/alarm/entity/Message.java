package com.github.sparkzxl.alarm.entity;

import com.github.sparkzxl.alarm.entity.dingtalk.DingTalkMessage;

import java.io.Serializable;
import java.util.List;

/**
 * description: 消息实体
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:49:46
 */
public class Message extends DingTalkMessage implements Serializable {
    private At at;

    public Message() {
    }

    public Message(At at) {
        this.at = at;
    }

    public At getAt() {
        return at;
    }

    public void setAt(At at) {
        this.at = at;
    }

    public static class At implements Serializable {
        /**
         * 被@人的手机号(在content里添加@人的手机号)
         */
        private List<String> atMobiles;
        /**
         * `@所有人`时：true，否则为：false
         */
        private Boolean isAtAll = false;

        public At() {
        }

        public At(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public At(Boolean isAtAll) {
            this.isAtAll = isAtAll;
        }

        public At(List<String> atMobiles, Boolean isAtAll) {
            this.atMobiles = atMobiles;
            this.isAtAll = isAtAll;
        }

        public List<String> getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public Boolean getIsAtAll() {
            return isAtAll;
        }

        public void setIsAtAll(Boolean atAll) {
            isAtAll = atAll;
        }
    }

}
