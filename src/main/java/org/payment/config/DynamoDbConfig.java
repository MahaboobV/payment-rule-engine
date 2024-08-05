package org.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    /*@Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;*/

   /* @Bean
    public DynamoDbClient dynamoDbClient() {
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create
                (AwsBasicCredentials.create("AKIA42SAEPLRKBYA4SQ2", "jsy1G33H/bgLJ8ibnCpAxQI3HDQilGUlJxGlt0wP"));

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }*/
}
