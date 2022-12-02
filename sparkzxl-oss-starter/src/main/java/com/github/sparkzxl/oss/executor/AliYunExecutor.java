package com.github.sparkzxl.oss.executor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.amazonaws.services.s3.model.S3Object;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.enums.BucketPolicyEnum;
import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.oss.support.OssErrorCode;
import com.github.sparkzxl.oss.support.OssException;
import com.github.sparkzxl.oss.utils.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * description: aliYun 执行器
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:54:27
 */
@Slf4j
public class AliYunExecutor extends AbstractOssExecutor<OSSClient> {


    public AliYunExecutor(OssClient<OSSClient> client) {
        super(client);
    }

    @Override
    public void createBucket(String bucketName) {
        OSSClient ossClient = obtainClient();
        try {
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
            } else {
                log.info("bucket [{}] already exists.", bucketName);
            }
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.CREATE_BUCKET_ERROR);
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        OSSClient ossClient = obtainClient();
        try {
            ossClient.deleteBucket(bucketName);
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.DELETE_BUCKET_ERROR);
        }
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName, Integer expire) {
        OSSClient ossClient = obtainClient();
        String objectUrl;
        try {
            DateTime expireDateTime = DateUtils.offsetDay(new Date(), expire);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
            req.setExpiration(expireDateTime);
            URL signedUrl = ossClient.generatePresignedUrl(req);
            objectUrl = signedUrl.toString();
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
        OSSClient ossClient = obtainClient();
        try {
            OSSObject object = ossClient.getObject(bucketName, objectName);
            S3Object s3Object = new S3Object();
            s3Object.setObjectContent(object.getObjectContent());
            s3Object.setBucketName(object.getBucketName());
            s3Object.setKey(object.getKey());
            return s3Object;
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.GET_OBJECT_INFO_ERROR);
        }
    }

    @Override
    public boolean exists(String bucketName, String objectName) {
        OSSClient ossClient = obtainClient();
        try {
            return ossClient.doesObjectExist(bucketName, objectName);
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.OSS_ERROR);
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, MultipartFile multipartFile) {
        OSSClient ossClient = obtainClient();
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, multipartFile.getInputStream());
            putObjectRequest.setMetadata(objectMetadata);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, String filePath) {
        OSSClient ossClient = obtainClient();
        try {
            File tempFile;
            if (StringUtils.startsWith(StrPool.HTTP, filePath) || StringUtils.startsWith(StrPool.HTTPS, filePath)) {
                URL url = new URL(filePath);
                tempFile = FileUtil.file(url);
            } else {
                tempFile = FileUtil.createTempFile(new File(filePath));
            }
            String mimeType = FileUtil.getType(tempFile);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(tempFile.length());
            objectMetadata.setContentType(mimeType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, FileUtil.getInputStream(tempFile));
            putObjectRequest.setMetadata(objectMetadata);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, URL url) {
        OSSClient ossClient = obtainClient();
        try {
            File tempFile = FileUtil.file(url);
            String mimeType = FileUtil.getType(tempFile);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(tempFile.length());
            objectMetadata.setContentType(mimeType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, FileUtil.getInputStream(tempFile));
            putObjectRequest.setMetadata(objectMetadata);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException e) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{} Error Message:{} Request ID:{} Host ID:{}",
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    e.getRequestId(),
                    e.getHostId());
            e.printStackTrace();
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        }

    }

    @Override
    public void multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        OSSClient ossClient = obtainClient();
        try {
            long fileLength = multipartFile.getSize();
            InputStream inputStream = multipartFile.getInputStream();
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);

            // 如果需要在初始化分片时设置请求头，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // 指定该Object的网页缓存行为。
            // metadata.setCacheControl("no-cache");
            // 指定该Object被下载时的名称。
            // metadata.setContentDisposition("attachment;filename=oss_MultipartUpload.txt");
            // 指定该Object的内容编码格式。
            // metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
            // 指定过期时间，单位为毫秒。
            // metadata.setHeader(HttpHeaders.EXPIRES, "1000");
            // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
            // metadata.setHeader("x-oss-forbid-overwrite", "true");
            // 指定上传该Object的每个part时使用的服务器端加密方式。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定Object的加密算法。如果未指定此选项，表明Object使用AES256加密算法。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_DATA_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定KMS托管的用户主密钥。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, "9468da86-3509-4f8d-a61e-6eab1eac****");
            // 指定Object的存储类型。
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
            // 指定Object的对象标签，可同时设置多个标签。
            // metadata.setHeader(OSSHeaders.OSS_TAGGING, "a:1");
            // request.setObjectMetadata(metadata);
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关的操作，例如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partTagList = new ArrayList<>();
            // 每个分片的大小1 MB，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1024 * 1024L;
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
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(inputStream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partTagList.add(uploadPartResult.getPartETag());
            }


            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partTagList);

            // 如果需要在完成分片上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);
            // 指定是否列举当前UploadId已上传的所有Part。如果通过服务端List分片数据来合并完整文件时，以上CompleteMultipartUploadRequest中的partETags可为null。
            // Map<String, String> headers = new HashMap<String, String>();
            // 如果指定了x-oss-complete-all:yes，则OSS会列举当前UploadId已上传的所有Part，然后按照PartNumber的序号排序并执行CompleteMultipartUpload操作。
            // 如果指定了x-oss-complete-all:yes，则不允许继续指定body，否则报错。
            // headers.put("x-oss-complete-all","yes");
            // completeMultipartUploadRequest.setHeaders(headers);
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{}\n"
                            + "Error Message:{}\n"
                            + "Request ID:{}\n"
                            + "Host ID:{}",
                    oe.getErrorCode(),
                    oe.getErrorMessage(),
                    oe.getRequestId(),
                    oe.getHostId());
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with OSS," +
                            "such as not being able to access the network.\n "
                            + "Error Message:{}",
                    ce.getMessage());
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        OSSClient ossClient = obtainClient();
        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.\n"
                            + "Error Code:{}\n"
                            + "Error Message:{}\n"
                            + "Request ID:{}\n"
                            + "Host ID:{}",
                    oe.getErrorCode(),
                    oe.getErrorMessage(),
                    oe.getRequestId(),
                    oe.getHostId());
            throw new OssException(OssErrorCode.DELETE_OBJECT_ERROR);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with OSS," +
                            "such as not being able to access the network.\n "
                            + "Error Message:{}",
                    ce.getMessage());
            throw new OssException(OssErrorCode.DELETE_OBJECT_ERROR);
        }
    }

    @Override
    public void downloadFile(String bucketName, String objectName, Consumer<InputStream> consumer) {
        OSSClient ossClient = obtainClient();
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        try (InputStream in = ossObject.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new OssException(OssErrorCode.DOWNLOAD_OBJECT_ERROR, e);
        }
    }

    @Override
    public UrlBuilder getObjectPrefixUrl(String bucket) {
        Configuration configInfo = obtainConfigInfo();
        if (StringUtils.isNotBlank(configInfo.getDomain())) {
            return UrlBuilder.ofHttp(configInfo.getDomain(), Charset.defaultCharset())
                    .addPath(bucket);
        } else {
            return UrlBuilder.ofHttp(configInfo.getEndpoint(), Charset.defaultCharset())
                    .addPath(bucket);
        }
    }

    @Override
    public void setBucketPolicy(String bucket, BucketPolicyEnum policy) {

    }

    @Override
    public void showdown() {
        OSSClient ossClient = obtainClient();
        ossClient.shutdown();
    }
}
