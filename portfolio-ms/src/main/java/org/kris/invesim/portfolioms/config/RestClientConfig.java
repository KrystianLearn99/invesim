package org.kris.invesim.portfolioms.config;

import org.kris.invesim.portfolioms.client.MarketDataClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() { return RestClient.builder(); }

    @Bean
    public MarketDataClient marketDataClient(RestClient.Builder builder) {
        RestClient rc = builder
                .baseUrl("http://MARKET-DATA-MS")
                .build();
        HttpServiceProxyFactory f = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(rc)).build();
        return f.createClient(MarketDataClient.class);
    }

}
