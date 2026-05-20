package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

@ApplicationScoped
public class ApplicationPropertiesCredentialsProvider implements AwsCredentialsProvider {

    @Override
    public AwsCredentials resolveCredentials() {
        // Read from application.properties
        String accessKeyId = ConfigProvider.getConfig().getValue("aws.accessKeyId", String.class);
        String secretAccessKey = ConfigProvider.getConfig().getValue("aws.secretAccessKey", String.class);

        // Return the credentials
        return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
    }
}
