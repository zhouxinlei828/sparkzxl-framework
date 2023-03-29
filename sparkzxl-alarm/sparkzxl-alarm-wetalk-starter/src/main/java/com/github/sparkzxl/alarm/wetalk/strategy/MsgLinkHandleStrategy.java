package com.github.sparkzxl.alarm.wetalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import java.text.MessageFormat;

/**
 * description: 企业微信link消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class MsgLinkHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        throw new AlarmException(AlarmErrorEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorCode(),
                MessageFormat.format(AlarmErrorEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                        AlarmChannel.WETALK.getType(),
                        MessageSubType.LINK.getCode()));
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.LINK.getCode());
        messageSource.setAlarmType(AlarmChannel.WETALK.getType());
        return messageSource.convert();
    }
}
