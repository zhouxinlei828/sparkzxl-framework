package com.github.sparkzxl.oss.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.internal.TransferProgressUpdatingListener;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.amazonaws.util.IOUtils;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.oss.properties.OssConfigInfo;
import com.github.sparkzxl.oss.support.OssErrorCode;
import com.github.sparkzxl.oss.support.OssException;
import com.github.sparkzxl.oss.utils.OssUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * description: minio 执行器
 *
 * @author zhouxinlei
 * @since 2022-05-03 16:54:27
 */
@Slf4j
public class AmazonS3Executor implements OssExecutor {

    private final Supplier<OssConfigInfo> supplier;
    private AmazonS3 amazonS3;

    @Setter
    @Getter
    private OssConfigInfo ossConfigInfo;

    public AmazonS3Executor(Supplier<OssConfigInfo> supplier) {
        this.supplier = supplier;
    }

    public AmazonS3 amazonS3Instance() {
        OssConfigInfo ossConfigInfo = supplier.get();
        if (ObjectUtils.isEmpty(ossConfigInfo)) {
            throw new OssException(OssErrorCode.NO_CONFIG_ERROR);
        }
        this.ossConfigInfo = ossConfigInfo;
        return Optional.ofNullable(this.amazonS3).orElseGet(() -> {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                    ossConfigInfo.getEndpoint(), ossConfigInfo.getRegion().getName());
            AWSCredentials awsCredentials = new BasicAWSCredentials(ossConfigInfo.getAccessKey(),
                    ossConfigInfo.getSecretKey());
            AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
            AmazonS3 amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
                    .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
                    .disableChunkedEncoding().withPathStyleAccessEnabled(ossConfigInfo.getPathStyleAccess()).build();
            this.amazonS3 = amazonS3;
            return amazonS3;
        });
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                Statement allowPublicReadStatement = new Statement(Statement.Effect.Allow)
                        .withPrincipals(Principal.AllUsers)
                        .withActions(S3Actions.GetObject)
                        .withResources(new com.amazonaws.auth.policy.resources.S3ObjectResource(bucketName, "*"));
                Statement allowRestrictedWriteStatement = new Statement(Statement.Effect.Allow)
                        .withPrincipals(new Principal(ossConfigInfo.getAccessKey()))
                        .withActions(S3Actions.PutObject)
                        .withResources(new com.amazonaws.auth.policy.resources.S3ObjectResource(bucketName, "*"));
                Policy policy = new Policy()
                        .withStatements(allowPublicReadStatement, allowRestrictedWriteStatement);
                amazonS3.createBucket((bucketName));
                amazonS3.setBucketPolicy(bucketName, policy.toJson());
            } else {
                log.info("bucket [{}] already exists.", bucketName);
            }
        } catch (Exception e) {
            throw new OssException(OssErrorCode.CREATE_BUCKET_ERROR);
        }
    }

    @Override
    public void createBucket(String bucketName, Policy policy) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                amazonS3.createBucket((bucketName));
                amazonS3.setBucketPolicy(bucketName, policy.toJson());
            } else {
                log.info("bucket [{}] already exists.", bucketName);
            }
        } catch (Exception e) {
            throw new OssException(OssErrorCode.CREATE_BUCKET_ERROR);
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        try {
            amazonS3Instance().deleteBucket(bucketName);
        } catch (Exception e) {
            throw new OssException(OssErrorCode.DELETE_BUCKET_ERROR);
        }
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName, Integer expires) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusDays(expires);
        URL url = amazonS3Instance().generatePresignedUrl(bucketName, objectName, DateUtils.localDateTime2Date(expireDateTime));
        return OssUtils.replaceHttpDomain(url, ossConfigInfo.getDomain());
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        return OssUtils.replaceHttpDomain(amazonS3Instance().getUrl(bucketName, objectName), ossConfigInfo.getDomain());
    }

    @Override
    public S3Object getObjectInfo(String bucketName, String objectName) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            return amazonS3.getObject(bucketName, objectName);
        } catch (Exception e) {
            throw new OssException(OssErrorCode.GET_OBJECT_INFO_ERROR);
        }
    }

    @Override
    public PutObjectResult putObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(size);
            objectMetadata.setContentType(contentType);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            return amazonS3Instance().putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);
        } catch (Exception e) {
            throw new OssException(OssErrorCode.PUT_OBJECT_ERROR);
        }
    }

    @Override
    public UploadResult multipartUpload(String bucketName, String objectName, String path) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3)
                    .build();
            File file = FileUtil.file(path);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
            // To receive notifications when bytes are transferred, add a
            // ProgressListener to your request.
            TransferProgress transferProgress = new TransferProgress();
            transferProgress.setTotalBytesToTransfer(file.length());
            putObjectRequest.setGeneralProgressListener(new TransferProgressUpdatingListener(transferProgress) {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    super.progressChanged(progressEvent);
                    log.info("Transferred bytes: {}", progressEvent.getBytesTransferred());
                }
            });
            Upload upload = tm.upload(putObjectRequest);
            log.info("objectName [{}] upload started", objectName);
            UploadResult uploadResult = upload.waitForUploadResult();
            log.info("objectName [{}] upload complete", objectName);
            return uploadResult;
        } catch (Exception e) {
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        }
    }

    @Override
    public UploadResult multipartUpload(String bucketName, String objectName, MultipartFile multipartFile) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3)
                    .build();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, multipartFile.getInputStream(), objectMetadata);

            // To receive notifications when bytes are transferred, add a
            // ProgressListener to your request.
            TransferProgress transferProgress = new TransferProgress();
            transferProgress.setTotalBytesToTransfer(multipartFile.getSize());
            putObjectRequest.setGeneralProgressListener(new TransferProgressUpdatingListener(transferProgress) {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    super.progressChanged(progressEvent);
                    log.info("Transferred bytes: {}", progressEvent.getBytesTransferred());
                }
            });
            Upload upload = tm.upload(putObjectRequest);
            log.info("objectName [{}] upload started", objectName);
            UploadResult uploadResult = upload.waitForUploadResult();
            log.info("objectName [{}] upload complete", objectName);
            return uploadResult;
        } catch (Exception e) {
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        }
    }

    @Override
    public UploadResult multipartUpload(String bucketName, String objectName, InputStream inputStream, String contentType, long fileLength) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3)
                    .build();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileLength);
            objectMetadata.setContentType(contentType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, null);

            // To receive notifications when bytes are transferred, add a
            // ProgressListener to your request.
            TransferProgress transferProgress = new TransferProgress();
            transferProgress.setTotalBytesToTransfer(fileLength);
            putObjectRequest.setGeneralProgressListener(new TransferProgressUpdatingListener(transferProgress) {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    super.progressChanged(progressEvent);
                    log.info("Transferred bytes: {}", progressEvent.getBytesTransferred());
                }
            });
            Upload upload = tm.upload(putObjectRequest);
            log.info("objectName [{}] upload started", objectName);
            UploadResult uploadResult = upload.waitForUploadResult();
            log.info("objectName [{}] upload complete", objectName);
            return uploadResult;
        } catch (Exception e) {
            throw new OssException(OssErrorCode.MULTIPART_UPLOAD_ERROR);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            AmazonS3 amazonS3 = amazonS3Instance();
            amazonS3.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            throw new OssException(OssErrorCode.DELETE_OBJECT_ERROR);
        }
    }

    @Override
    public void downloadFile(String fileUrl, HttpServletResponse response) {
        try {
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();
            String fileName = FileNameUtil.getName(fileUrl);
            handleDownloadFile(fileName, response, url, inputStream);
        } catch (IOException e) {
            throw new OssException(OssErrorCode.DOWNLOAD_OBJECT_ERROR);
        }
    }

    @Override
    public void downloadFile(String fileUrl, String fileName, HttpServletResponse response) {
        try {
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();
            handleDownloadFile(fileName, response, url, inputStream);
        } catch (IOException e) {
            throw new OssException(OssErrorCode.DOWNLOAD_OBJECT_ERROR);
        }
    }

    private void handleDownloadFile(String fileName, HttpServletResponse response, URL url, InputStream inputStream) throws IOException {
        ServletOutputStream outputStream = null;
        try {
            PresignedUrlDownloadRequest urlDownloadRequest = new PresignedUrlDownloadRequest(url);
            PresignedUrlDownloadResult presignedUrlDownloadResult = amazonS3Instance().download(urlDownloadRequest);
            ObjectMetadata objectMetadata = presignedUrlDownloadResult.getS3Object().getObjectMetadata();
            String contentLength = String.valueOf(objectMetadata.getContentLength());
            S3ObjectInputStream s3ObjectInputStream = presignedUrlDownloadResult.getS3Object().getObjectContent();
            InputStream delegateStream = s3ObjectInputStream.getDelegateStream();
            // 清空response
            response.reset();
            response.setContentType(objectMetadata.getContentType());
            // 设置response的Header
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            response.addHeader("Content-Length", contentLength);
            outputStream = response.getOutputStream();
            int length;
            byte[] bs = new byte[1024];
            while ((length = delegateStream.read(bs)) != -1) {
                outputStream.write(bs, 0, length);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (Objects.nonNull(outputStream)) {
                outputStream.close();
            }
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
        }
    }
}
