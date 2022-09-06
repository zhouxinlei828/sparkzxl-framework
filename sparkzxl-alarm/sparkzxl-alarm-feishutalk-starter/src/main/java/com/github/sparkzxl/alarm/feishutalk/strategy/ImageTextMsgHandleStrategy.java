package com.github.sparkzxl.alarm.feishutalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;

import java.text.MessageFormat;

/**
 * description: 飞书图文消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class ImageTextMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorCode(),
                MessageFormat.format(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                        AlarmChannel.FEISHU.getType(),
                        MessageSubType.IMAGE_TEXT.getCode()));
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.IMAGE_TEXT.getCode());
        messageSource.setAlarmType(AlarmChannel.FEISHU.getType());
        return messageSource.convert();
    }
}
