package com.fernando.springcloud.msvc.items;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
// import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // @Value("${config.baseurl.endpoint.msvc-products}")   
    // private String url;

    @Bean
    WebClient webClient(@Value("${config.baseurl.endpoint.msvc-products}") String url,
        WebClient.Builder webClientBuilder,
        ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return webClientBuilder.baseUrl(url).filter(lbFunction).build();
    }

     // @Bean
    // @LoadBalanced
    // WebClient.Builder webClient() {
    //     return WebClient.builder().baseUrl(url);
    // }
}
