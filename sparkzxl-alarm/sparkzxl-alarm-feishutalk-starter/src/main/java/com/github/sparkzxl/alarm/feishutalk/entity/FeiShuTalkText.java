package com.github.sparkzxl.alarm.feishutalk.entity;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.github.sparkzxl.core.util.StrPool;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 飞书文本消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:51:30
 */
public class FeiShuTalkText extends Message {

    /**
     * 消息内容
     */
    private Content content;

    public FeiShuTalkText(Content content) {
        this.content = content;
        setMsgtype(FeiShuTalkMsgType.TEXT.type());
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<FeiShuAt> atList = Convert.convert(new TypeReference<List<FeiShuAt>>() {
                }, value);
                setAtList(atList);
                break;
            }
        }
        this.content.text = replaceContent(this.content.text, params);
    }

    @Override
    public String toJson() {
        List<FeiShuAt> atList = super.getAtList();
        StringBuilder atListStr = new StringBuilder();
        if (CollectionUtils.isNotEmpty(atList)) {
            atList.forEach(at -> {
                String toXML = at.toXML();
                atListStr.append(toXML).append(StrPool.NEWLINE);
            });
        }
        String text = getContent().getText();
        if (StringUtils.isNotEmpty(text)) {
            setContent(new Content(text.concat(StrPool.NEWLINE).concat(atListStr.toString())));
        }
        return super.toJson();
    }

    public static class Content implements Serializable {

        private String text;

        public Content() {
        }

        public Content(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
