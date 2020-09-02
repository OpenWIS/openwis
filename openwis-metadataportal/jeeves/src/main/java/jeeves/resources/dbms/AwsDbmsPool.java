package jeeves.resources.dbms;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jeeves.constants.Jeeves;
import jeeves.interfaces.Logger;
import jeeves.utils.Log;
import org.jdom.Attribute;
import org.jdom.Element;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;

/**
 * Extends Dmbs pool to use AwsSecretManager to retrieve credentials for DB
 */
public class AwsDbmsPool extends DbmsPool {

    private static Logger logger = Log.createLogger(Log.RESOURCES);
    /**
     * Wraps the base init method to get the secret from secret manager.
     * @param name
     * @param config
     * @throws Exception
     */
    public void init(String name, Element config) throws Exception {
        String secretName = config.getChildText(Jeeves.Res.Pool.AWS_SECRET);
        String region = config.getChildText(Jeeves.Res.Pool.AWS_REGION);
        String endpoint = config.getChildText(Jeeves.Res.Pool.AWS_ENDPOINT);

        if (secretName == null || region == null) {
            throw new Exception("Cannot retrieve secret. Secret name or aws region is missing from config.xml");
        }

        AwsClientBuilder.EndpointConfiguration smConfig = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(smConfig);
        clientBuilder.setCredentials(InstanceProfileCredentialsProvider.getInstance());
        AWSSecretsManager client = clientBuilder.build();

        String secret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            logger.error("Cannot retrieve secret: " + e.getMessage());
            throw new Exception(e);
        }

        Element dbmsConfig = null;
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            ObjectMapper objectMapper = new ObjectMapper();
            AwsDBSecret awsDBSecret = objectMapper.readValue(secret, AwsDBSecret.class);
            dbmsConfig = fromSecret(awsDBSecret);
        } else {
            throw  new Exception("Got secret from AWS but it is empty");
        }

        super.init(name,dbmsConfig);
    }

    private Element fromSecret(AwsDBSecret secret) {
        Element config = secret.toElement();
        config.addContent(new Element("driver").setText("org.postgresql.Driver"));
        config.addContent(new Element("poolSize").setText("10"));

        Element properties = new Element("properties");
        Element propertyString = new Element("property");
        propertyString.setAttribute(new Attribute("key","stringtype"));
        propertyString.setText("unspecified");
        properties.setContent(propertyString);

        config.addContent(properties);
        return config;

    }
}
