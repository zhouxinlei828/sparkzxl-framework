package com.github.sparkzxl.service.wechat;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.cache.CaffeineCache;
import com.github.sparkzxl.entity.AlarmLogInfo;
import com.github.sparkzxl.service.BaseWarnService;
import com.github.sparkzxl.utils.ThrowableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description: 日志企业微信告警服务实现类
 *
 * @author zhoux
 * @date 2021-08-21 12:11:31
 */
public class WorkWeXinWarnService extends BaseWarnService {

    private final Logger logger = LoggerFactory.getLogger(WorkWeXinWarnService.class);

    private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";
    private static final String WECHAT_AUTH_TOKEN = "wechat_auth_token";

    private static final Long TOKEN_EXPIRES_IN = 7000000L;

    private final CaffeineCache caffeineCache = new CaffeineCache();

    long maxSize = 1000L;

    private String to;

    private final Integer applicationId;

    private final String corpId;

    private final String corpSecret;

    public WorkWeXinWarnService(String to, Integer applicationId, String corpId, String corpSecret) {
        this.to = to;
        this.applicationId = applicationId;
        this.corpId = corpId;
        this.corpSecret = corpSecret;
    }

    /**
     * 微信授权请求，GET类型，获取授权响应，用于其他方法截取token
     */
    private String toAuth(String getTokenUrl) throws IOException {
        String resp = HttpRequest.get(getTokenUrl).execute().body();
        logger.info("get work weixin token resp:{}", resp);
        return resp;
    }

    private String getToken() throws IOException {
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

    private String createPostData(String touser, String msgtype, String contentValue) {
        WorkWeXinSendRequest wcd = new WorkWeXinSendRequest();
        wcd.setTouser(touser);
        wcd.setAgentid(applicationId);
        wcd.setMsgtype(msgtype);
        Map<String, Object> content = new HashMap<>();
        content.put("content", contentValue);
        wcd.setText(content);
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
    protected void doSend(AlarmLogInfo context, Throwable throwable) throws Exception {
        String data = createPostData(toUser(to.split(",")), WorkWeXinSendMsgTypeEnum.TEXT.name(), ThrowableUtils.workWeChatContent(context, throwable));
        String url = String.format(SEND_MESSAGE_URL, getToken());
        String resp = HttpRequest.post(url).body(data).execute().body();
        logger.info("send work weixin message call [{}], param:{}, resp:{}", url, data, resp);
    }
}
