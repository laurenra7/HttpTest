package org.la.spring.config;

import edu.byu.wso2.core.Wso2Credentials;
import edu.byu.wso2.core.provider.ClientCredentialOauthTokenProvider;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "org.la"
})
public class Wso2Config {

    /* For user mybyuapp or laurenra and subscriptions for application MyBYU-prod */
    String consumerKey = "mykey";
    String consumerSecret = "mysecret";

    @Bean
    public Wso2Credentials wso2Credentials() {
        return new Wso2Credentials(consumerKey, consumerSecret);
    }

    @Bean
    public ClientCredentialOauthTokenProvider clientCredentialOauthTokenProvider() {
        return new ClientCredentialOauthTokenProvider(wso2Credentials());
    }

    @Bean
    public ClientCredentialsTokenHeaderProvider tokenHeaderProvider() {
        return new ClientCredentialsTokenHeaderProvider(clientCredentialOauthTokenProvider());
    }
}
