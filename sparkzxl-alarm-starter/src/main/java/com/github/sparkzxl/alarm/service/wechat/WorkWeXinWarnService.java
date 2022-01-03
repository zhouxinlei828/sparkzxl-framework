package com.github.sparkzxl.alarm.service.wechat;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.alarm.cache.CaffeineCache;
import com.github.sparkzxl.alarm.constant.enums.MessageTye;
import com.github.sparkzxl.alarm.service.BaseWarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: 日志企业微信告警服务实现类
 *
 * @author zhoux
 */
@Slf4j
public class WorkWeXinWarnService extends BaseWarnService {

    private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";
    private static final String WECHAT_AUTH_TOKEN = "wechat_auth_token";
    private static final Long TOKEN_EXPIRES_IN = 7000000L;
    private final CaffeineCache caffeineCache = new CaffeineCache();
    private final Integer applicationId;
    private final String corpId;
    private final String corpSecret;
    long maxSize = 1000L;
    private String to;

    public WorkWeXinWarnService(String to, Integer applicationId, String corpId, String corpSecret) {
        this.to = to;
        this.applicationId = applicationId;
        this.corpId = corpId;
        this.corpSecret = corpSecret;
    }

    /**
     * 微信授权请求，GET类型，获取授权响应，用于其他方法截取token
     */
    private String toAuth(String getTokenUrl) {
        String resp = HttpRequest.get(getTokenUrl).execute().body();
        log.info("get work weixin token resp:{}", resp);
        return resp;
    }

    private String getToken() {
        String accessToken = caffeineCache.get(WECHAT_AUTH_TOKEN);
        if (StringUtils.isEmpty(accessToken)) {
            String resp = toAuth(String.format(GET_TOKEN_URL, corpId, corpSecret));
            Map<String, Object> responseMap = JSONUtil.toBean(resp, new TypeReference<Map<String, Object>>() {
            }, true);
            assert responseMap != null;
            accessToken = responseMap.get("access_token").toString();
            caffeineCache.set(WECHAT_AUTH_TOKEN, accessToken, TOKEN_EXPIRES_IN, TimeUnit.MILLISECONDS);
        }
        return accessToken;
    }

    private String createPostData(String touser, MessageTye messageTye, String contentValue) {
        WorkWeXinSendRequest wcd = new WorkWeXinSendRequest();
        wcd.setTouser(touser);
        wcd.setAgentid(applicationId);
        wcd.setMsgtype(messageTye.getValue());
        if (messageTye.equals(MessageTye.TEXT)) {
            WorkWeXinSendRequest.Text text = new WorkWeXinSendRequest.Text(contentValue);
            wcd.setText(text);
        } else if (messageTye.equals(MessageTye.MARKDOWN)) {
            WorkWeXinSendRequest.Markdown text = new WorkWeXinSendRequest.Markdown(contentValue);
            wcd.setMarkdown(text);
        }
        return JSONUtil.toJsonStr(wcd);
    }

    private String toUser(String[] receiver) {
        String[] arr = this.toString(receiver).split(",");
        StringBuilder sb = new StringBuilder();
        //将不含有@符号的邮箱地址取出
        for (String str : arr) {
            if (!str.contains("@")) {
                sb.append(str).append("|");
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 数组转字符串
     **/
    private String toString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String str : array) {
            sb.append(str).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    @Override
    protected void doSendText(String message) throws Exception {
        String data = createPostData(toUser(to.split(",")), MessageTye.TEXT, message);
        String url = String.format(SEND_MESSAGE_URL, getToken());
        String resp = HttpRequest.post(url).body(data).execute().body();
        log.info("send work weixin message call [{}], param:{}, resp:{}", url, data, resp);
    }

    @Override
    protected void doSendMarkdown(String title, String message) throws Exception {
        String data = createPostData(toUser(to.split(",")), MessageTye.MARKDOWN, message);
        String url = String.format(SEND_MESSAGE_URL, getToken());
        String resp = HttpRequest.post(url).body(data).execute().body();
        log.info("send work weixin message call [{}], param:{}, resp:{}", url, data, resp);
    }
}
