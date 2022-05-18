/*
 * Copyright ©2015-2022 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 异常类
 *
 * @author Jaemon
 * @since 1.0
 */
@Setter
@Getter
public class AlarmException extends RuntimeException {

    /**
     * 具体异常码
     */
    private String errorCode;
    /**
     * 异常信息
     */
    private String errorMessage;

    public AlarmException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AlarmException(AlarmResponseCodeEnum alarmResponseCodeEnum) {
        super(alarmResponseCodeEnum.message());
        this.errorCode = alarmResponseCodeEnum.code();
        this.errorMessage = alarmResponseCodeEnum.message();
    }

    public AlarmException(Throwable cause) {
        super(cause);
        this.errorCode = AlarmResponseCodeEnum.FAILED.code();
        this.errorMessage = cause.getMessage();
    }

    public AlarmException(Throwable cause,String errorCode, String errorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
