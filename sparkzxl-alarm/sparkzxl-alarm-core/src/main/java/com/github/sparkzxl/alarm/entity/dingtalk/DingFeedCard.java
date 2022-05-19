package com.github.sparkzxl.alarm.entity.dingtalk;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.github.sparkzxl.alarm.entity.ImageText;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description: FeedCard类型
 *
 * @author zhouxinlei
 * @since 2022-05-19 10:20:51
 */
public class DingFeedCard extends DingTalkMessage {

    /**
     * {@link FeedCard}
     */
    private FeedCard feedCard;

    public DingFeedCard() {
        setMsgtype(DingTalkMsgType.FEED_CARD.type());
    }

    public DingFeedCard(List<FeedCard.Link> links) {
        this();
        this.feedCard = new FeedCard(links);
    }

    public FeedCard getFeedCard() {
        return feedCard;
    }

    public void setFeedCard(FeedCard feedCard) {
        this.feedCard = feedCard;
    }

    public static class FeedCard implements Serializable {
        /**
         * {@link Link}
         */
        private List<Link> links;

        public FeedCard() {
        }

        public FeedCard(List<Link> links) {
            this.links = links;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public static class Link implements Serializable {
            /**
             * 单条信息文本
             */
            private String title;
            /**
             * 点击单条信息到跳转链接
             */
            private String messageURL;
            /**
             * 单条信息后面图片的URL
             */
            private String picURL;

            public Link() {
            }

            public Link(String title, String messageURL, String picURL) {
                this.title = title;
                this.messageURL = messageURL;
                this.picURL = picURL;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMessageURL() {
                return messageURL;
            }

            public void setMessageURL(String messageURL) {
                this.messageURL = messageURL;
            }

            public String getPicURL() {
                return picURL;
            }

            public void setPicURL(String picURL) {
                this.picURL = picURL;
            }
        }
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<ImageText> imageTexts = Convert.convert(new TypeReference<List<ImageText>>() {
                }, value);
                for (ImageText imageText : imageTexts) {
                    this.feedCard.links.add(new FeedCard.Link(imageText.getTitle(), imageText.getUrl(), imageText.getPicUrl()));
                }
                break;
            }
        }
    }
}