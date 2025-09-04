package org.kris.invesim.simulationms.config;

import org.kris.invesim.simulationms.infrastructure.client.MarketAsyncClient;
import org.kris.invesim.simulationms.infrastructure.client.MarketDataClient;
import org.kris.invesim.simulationms.infrastructure.client.NotificationClient;
import org.kris.invesim.simulationms.infrastructure.client.PortfolioAsyncClient;
import org.kris.invesim.simulationms.infrastructure.client.PortfolioClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {


    @Bean
    public MarketDataClient marketDataHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://MARKET-DATA-MS")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(MarketDataClient.class);
    }

    @Bean
    public PortfolioClient portfolioHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://PORTFOLIO-MS")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PortfolioClient.class);
    }

    @Bean
    public NotificationClient notificationHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://NOTIFICATION-MS")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(NotificationClient.class);
    }


    @Bean
    public PortfolioAsyncClient portfolioAsyncHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://PORTFOLIO-MS")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PortfolioAsyncClient.class);
    }

    @Bean
    public MarketAsyncClient marketAsyncHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://MARKET-DATA-MS")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(MarketAsyncClient.class);
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
