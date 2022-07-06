package com.github.sparkzxl.alarm.feishutalk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.io.Serializable;
import java.util.List;

/**
 * description: 消息实体
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:49:46
 */
public class Message extends FeiShuTalkMessage implements Serializable {

    @JsonIgnore
    private List<FeiShuAt> atList;

    public Message() {
    }

    public Message(List<FeiShuAt> atList) {
        this.atList = atList;
    }

    public List<FeiShuAt> getAtList() {
        return atList;
    }

    public void setAtList(List<FeiShuAt> atList) {
        this.atList = atList;
    }

    @XStreamAlias("at")
    public static class FeiShuAt implements Serializable {
        /**
         * 被@人的openId(在content里添加@人的openId)
         */
        @XStreamAsAttribute()
        @XStreamAlias("user_id")
        private String userId;

        @XStreamOmitField
        private String userName;

        public FeiShuAt() {
        }

        public FeiShuAt(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public FeiShuAt(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String toXML() {
            XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")));
            //识别obj类中的注解
            xstream.processAnnotations(this.getClass());
            String xmlData = xstream.toXML(this);
            xmlData = xmlData.replace("/>", ">".concat(userName).concat("</at>"));
            return xmlData;
        }
    }


}
