
package org.payment.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.service.PaymentRuleEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PaymentRuleProcessingLamda implements RequestHandler<S3Event, String> {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleProcessingLamda.class);

    private final S3Client s3Client;

    private final ObjectMapper objectMapper;

    @Autowired
    private PaymentRuleEngineService paymentRuleEngineService;

    public PaymentRuleProcessingLamda() {
        this.s3Client = S3Client.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        logger.info("PaymentRuleProcessingLamda handleRequest {}", s3Event.toString());

        // Retrieve S3 event details
        S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);

        String s3BucketName = record.getS3().getBucket().getName();

        String s3BucketKey = record.getS3().getObject().getKey();

        try {
            logger.info("Downloading Json file.....");
            //Download file from S3
            JsonNode rootNode = downloadJsonFile(s3BucketName, s3BucketKey);
            if (null != rootNode) {
                logger.info("Parsing Json content.....");
                //Parse Rules from Json
                JsonNode rulesNode = rootNode.path("rules");
                String status = paymentRuleEngineService.processRules(rulesNode);
                logger.info("Successfully processed and store Json file from S3 : {}", status);
            }
        } catch (Exception e) {
            logger.error("Error processing file from S3:" + e.getMessage());
        }
        return "Done";
    }

    private JsonNode downloadJsonFile(String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        //Parse Json content
        try (InputStream s3Object = s3Client.getObject(getObjectRequest)) {
            return objectMapper.readTree(s3Object);
        }
    }
}

