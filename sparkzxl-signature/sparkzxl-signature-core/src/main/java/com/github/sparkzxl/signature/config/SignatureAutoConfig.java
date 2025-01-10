package com.github.sparkzxl.signature.config;

import com.github.sparkzxl.signature.algorithm.*;
import com.github.sparkzxl.signature.executor.SignatureExecutor;
import com.github.sparkzxl.signature.executor.SignatureExecutorContext;
import com.github.sparkzxl.signature.executor.StandardSignatureExecutor;
import com.github.sparkzxl.signature.properties.SignatureProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 签名自动装配
 *
 * @author zhouxinlei
 * @since 2024-05-21 14:16:40
 */
@Configuration
@EnableConfigurationProperties(SignatureProperties.class)
public class SignatureAutoConfig {

    @Bean
    public SignAlgorithm hmacSHA256SignAlgorithm() {
        return new HmacSHA256SignAlgorithm();
    }

    @Bean
    public SignAlgorithm sha256RSASignAlgorithm() {
        return new SHA256RSASignAlgorithm();
    }
    @Bean
    public SignAlgorithm sm3SignAlgorithm() {
        return new SM3SignAlgorithm();
    }

    @Bean
    public SignAlgorithmContext signAlgorithmContext(@Autowired List<SignAlgorithm> signAlgorithmList) {
        return new SignAlgorithmContext(signAlgorithmList);
    }

    @Bean
    public SignatureExecutor standardSignatureExecutor() {
        return new StandardSignatureExecutor();
    }

    @Bean
    public SignatureExecutorContext signatureExecutorContext(@Autowired List<SignatureExecutor> signatureExecutorList) {
        return new SignatureExecutorContext(signatureExecutorList);
    }

}
