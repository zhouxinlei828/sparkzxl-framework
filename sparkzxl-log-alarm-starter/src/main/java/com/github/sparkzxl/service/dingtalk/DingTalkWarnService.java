package com.github.sparkzxl.service.dingtalk;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.entity.core.ErrorInfo;
import com.github.sparkzxl.service.BaseWarnService;
import com.github.sparkzxl.utils.ThrowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * description: 钉钉告警服务实现类
 *
 * @author zhoux
 * @date 2021-08-21 12:12:35
 */
@Slf4j
public class DingTalkWarnService extends BaseWarnService {

    private final Logger logger = LoggerFactory.getLogger(DingTalkWarnService.class);

    private final String token;

    private final String secret;

    private static final String ROBOT_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    public DingTalkWarnService(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public String sendRobotMessage(String message) throws Exception {
        DingTalkSendParam param = new DingTalkSendParam();
        param.setMsgtype(DingTalkSendMsgTypeEnum.TEXT.getType());
        param.setText(new DingTalkSendParam.Text(message));
        String json = JsonUtil.toJson(param);
        String sign = getSign();
        return HttpRequest.post(sign).contentType(ContentType.JSON.getValue()).body(json).execute().body();
    }

    /**
     * 获取签名
     *
     * @return 返回签名
     */
    private String getSign() throws Exception {
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return ROBOT_SEND_URL + token + "&timestamp=" + timestamp + "&sign=" + URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), StandardCharsets.UTF_8.toString());
    }

    @Override
    protected void doSend(ErrorInfo context, Throwable throwable) throws Exception {
        sendRobotMessage(ThrowableUtils.dingTalkContent(context, throwable));
    }
}
