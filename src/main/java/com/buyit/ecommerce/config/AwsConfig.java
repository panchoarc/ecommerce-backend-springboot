package com.buyit.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.s3.localstackEndpoint:}")
    private String localStackEndpoint;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.useLocalStack:false}")
    private boolean useLocalStack;


    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;


    @Bean
    public S3Client s3Client() {
        if (useLocalStack) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .endpointOverride(URI.create(localStackEndpoint)) // Conexión a LocalStack
                    .credentialsProvider(credentialsProvider())
                    .forcePathStyle(true)
                    .build();
        } else {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(credentialsProvider()) // Usa credenciales de AWS reales
                    .build();
        }
    }

    @Bean
    public AwsCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }
}
