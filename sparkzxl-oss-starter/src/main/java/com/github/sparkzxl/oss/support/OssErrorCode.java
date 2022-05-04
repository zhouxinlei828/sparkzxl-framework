package com.github.sparkzxl.oss.support;

import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 枚举Oss操作码
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public enum OssErrorCode implements IErrorCode {

    // OSS创建bucket失败
    NO_CONFIG_ERROR("405", "未加载到配置信息"),
    CREATE_BUCKET_ERROR("405", "oss创建bucket失败"),
    DELETE_BUCKET_ERROR("405", "oss删除bucket失败"),
    GET_OBJECT_INFO_ERROR("415", "获取oss文件信息失败"),
    PUT_OBJECT_ERROR("415", "上传oss文件失败"),
    MULTIPART_UPLOAD_ERROR("415", "分段上传oss文件失败"),
    DELETE_OBJECT_ERROR("415", "删除oss文件失败"),
    DOWNLOAD_OBJECT_ERROR("415", "下载oss文件失败"),
    SET_BUCKET_POLICY_ERROR("415", "设置桶策略失败"),
    ;

    final String errorCode;

    final String errorMessage;
}
