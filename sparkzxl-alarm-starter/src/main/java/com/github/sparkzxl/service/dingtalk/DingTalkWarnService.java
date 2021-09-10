package com.github.sparkzxl.service.dingtalk;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.service.BaseWarnService;
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

    private Logger logger = LoggerFactory.getLogger(DingTalkWarnService.class);

    private final String token;

    private final String secret;

    private static final String ROBOT_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    public DingTalkWarnService(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public void sendRobotMessage(String message) throws Exception {
        DingTalkSendRequest param = new DingTalkSendRequest();
        param.setMsgtype(DingTalkSendMsgTypeEnum.TEXT.getType());
        param.setText(new DingTalkSendRequest.Text(message));
        String json = JSONUtil.toJsonStr(param);
        String sign = getSign();
        String body = HttpRequest.post(sign).contentType(ContentType.JSON.getValue()).body(json).execute().body();
        logger.info("钉钉机器人通知结果：{}", body);
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
    protected void doSend(String message) throws Exception {
        sendRobotMessage(message);
    }
}
