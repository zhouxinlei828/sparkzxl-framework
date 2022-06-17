package com.github.sparkzxl.oss.support;

import com.github.sparkzxl.core.support.code.IErrorCode;
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
    OSS_ERROR("A0700", "OSS异常"),
    FILE_TYPE_ERROR("A0701", "用户上传文件类型不匹配"),
    FILE_EXCEED_LIMIT_ERROR("A0702", "用户上传文件太大"),
    IMAGE_EXCEED_LIMIT_ERROR("A0703", "用户上传图片太大"),
    VIDEO_EXCEED_LIMIT_ERROR("A0704", "用户上传视频太大"),
    COMPRESSED_FILE_EXCEED_LIMIT_ERROR("A0705", "用户上传压缩文件太大"),
    NO_CONFIG_ERROR("A0706", "未加载到配置信息"),
    CREATE_BUCKET_ERROR("A0707", "OSS创建bucket失败"),
    DELETE_BUCKET_ERROR("A0708", "OSS删除bucket失败"),
    GET_OBJECT_INFO_ERROR("A0709", "获取OSS文件信息失败"),
    PUT_OBJECT_ERROR("A0710", "上传OSS文件失败"),
    MULTIPART_UPLOAD_ERROR("A0711", "分段上传OSS文件失败"),
    DELETE_OBJECT_ERROR("A0712", "删除OSS文件失败"),
    DOWNLOAD_OBJECT_ERROR("A0713", "下载OSS文件失败"),
    SET_BUCKET_POLICY_ERROR("A0714", "设置桶策略失败"),
    ;

    final String errorCode;

    final String errorMsg;
}
