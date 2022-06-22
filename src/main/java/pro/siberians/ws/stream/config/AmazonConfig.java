package pro.siberians.ws.stream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;

@Configuration
public class AmazonConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accesskey}")
    private String awsAccessKey;

    @Value("${aws.secretkey}")
    private String awsSecretKey;

    @Value("${aws.dynamodb.endpoint}")
    private String awsDynamoDBEndpoint;

    @Value("${aws.ec2.endpoint}")
    private String awsEC2Endpoint;

    public static String AWS_ROUTE_53_ENDPOINT = "https://route53.amazonaws.com";
    public static String AWS_ROUTE_53_REGION = "us-east-1";

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
        builder.setCredentials(new AWSStaticCredentialsProvider(awsCredentials()));
        builder.setEndpointConfiguration(new EndpointConfiguration(awsDynamoDBEndpoint, awsRegion));
        return builder.build();
    }

    @Bean
    public AmazonEC2 amazonEC2() {
        AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard();
        builder.setCredentials(new AWSStaticCredentialsProvider(awsCredentials()));
        builder.setEndpointConfiguration(new EndpointConfiguration(awsEC2Endpoint, awsRegion));
        return builder.build();
    }

    @Bean
    public AmazonRoute53 amazonRoute53() {
        AmazonRoute53ClientBuilder builder = AmazonRoute53ClientBuilder.standard();
        builder.setCredentials(new AWSStaticCredentialsProvider(awsCredentials()));
        builder.setEndpointConfiguration(new EndpointConfiguration(AWS_ROUTE_53_ENDPOINT, AWS_ROUTE_53_REGION));
        return builder.build();
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }
}
