package com.github.sparkzxl.oss.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import com.amazonaws.services.s3.model.S3Object;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.enums.BucketPolicyEnum;
import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.oss.support.OssErrorCode;
import com.github.sparkzxl.oss.support.OssException;
import com.github.sparkzxl.oss.utils.OssUtils;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * description: minio 执行器
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:54:27
 */
@Slf4j
public class MinioExecutor extends AbstractOssExecutor<MinioClient> {

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只读
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";

    public MinioExecutor(OssClient<MinioClient> client) {
        super(client);
    }

    @Override
    public void createBucket(String bucketName) {
        MinioClient minioClient = obtainClient();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                log.info("bucket [{}] already exists.", bucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.CREATE_BUCKET_ERROR);
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        MinioClient minioClient = obtainClient();
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.DELETE_BUCKET_ERROR);
        }
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName, Integer expire) {
        MinioClient minioClient = obtainClient();
        String objectUrl;
        try {
            objectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expire).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.GET_OBJECT_INFO_ERROR);
        }
        Configuration configInfo = obtainConfigInfo();
        return OssUtils.replaceHttpDomain(objectUrl, configInfo.getDomain());
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        return URLUtil.decode(getObjectPrefixUrl(bucketName).addPath(objectName).build());
    }

    @Override
    public S3Object getObjectInfo(String bucketName, String objectName) {
        MinioClient minioClient = obtainClient();
        try {
            GetObjectResponse minioClientObject = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
            S3Object s3Object = new S3Object();
            s3Object.setObjectContent(minioClientObject);
            s3Object.setBucketName(minioClientObject.bucket());
            s3Object.setKey(minioClientObject.object());
            return s3Object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.GET_OBJECT_INFO_ERROR);
        }
    }

    @Override
    public boolean exists(String bucketName, String objectName) {
        MinioClient minioClient = obtainClient();
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return stat != null && stat.lastModified() != null;
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException | InternalException | ServerException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException e) {
            throw new OssException(OssErrorCode.OSS_ERROR.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, MultipartFile multipartFile) {
        MinioClient minioClient = obtainClient();
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(multipartFile.getInputStream(), multipartFile.getSize(), PutObjectArgs.MIN_MULTIPART_SIZE).contentType(multipartFile.getContentType()).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, String filePath) {
        MinioClient minioClient = obtainClient();
        try {
            InputStream inputStream;
            if (StringUtils.startsWith(StrPool.HTTP, filePath) || StringUtils.startsWith(StrPool.HTTPS, filePath)) {
                URL url = new URL(filePath);
                inputStream = url.openStream();
            } else {
                inputStream = FileUtil.getInputStream(filePath);
            }
            String mimeType = FileUtil.getMimeType(filePath);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(inputStream, inputStream.available(), -1).contentType(mimeType).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        }
    }

    @Override
    public void multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        MinioClient minioClient = obtainClient();
        try {
            List<SnowballObject> snowballObjects = new ArrayList<>();
            InputStream inputStream = multipartFile.getInputStream();
            // 计算文件有多少个分片。
            // 1MB
            final long partSize = 1024 * 1024L;
            long fileLength = multipartFile.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                // 跳过已经上传的分片。
                inputStream.skip(startPos);
                SnowballObject snowballObject = new SnowballObject(objectName, inputStream, curPartSize, null);
                snowballObjects.add(snowballObject);
            }
            log.info("objectName [{}] upload started", objectName);
            minioClient.uploadSnowballObjects(UploadSnowballObjectsArgs.builder().bucket(bucketName).object(objectName).objects(snowballObjects).build());
            log.info("objectName [{}] upload complete", objectName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        MinioClient minioClient = obtainClient();
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.DELETE_OBJECT_ERROR);
        }
    }

    @Override
    public void downloadFile(String bucketName, String objectName, Consumer<InputStream> consumer) {
        MinioClient minioClient = obtainClient();
        try (InputStream in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build())) {
            consumer.accept(in);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | ServerException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 XmlParserException e) {
            throw new OssException(OssErrorCode.DOWNLOAD_OBJECT_ERROR, e);
        }
    }

    @Override
    public UrlBuilder getObjectPrefixUrl(String bucket) {
        Configuration configInfo = obtainConfigInfo();
        if (StringUtils.isNotBlank(configInfo.getDomain())) {
            return UrlBuilder.ofHttp(configInfo.getDomain(), Charset.defaultCharset()).addPath(bucket);
        } else {
            return UrlBuilder.ofHttp(configInfo.getEndpoint(), Charset.defaultCharset()).addPath(bucket);
        }
    }

    @Override
    public void setBucketPolicy(String bucket, BucketPolicyEnum policy) {
        MinioClient minioClient = obtainClient();
        try {
            switch (policy) {
                case READ_ONLY:
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                    break;
                case WRITE_ONLY:
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                    break;
                case READ_WRITE:
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.SET_BUCKET_POLICY_ERROR);
        }
    }

    @Override
    public void showdown() {
    }
}
