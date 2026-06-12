package com.ecommerce.order.clients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ProductServiceClientConfig {

  @Bean
  public ProductServiceClient productClientInterface(
      @Qualifier("restClientBuilderLoadBalanced") RestClient.Builder restClientBuilder) {

    RestClient restClient = restClientBuilder
        .baseUrl("http://product-service")
        .build();

    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(adapter)
        .build();

    return factory.createClient(ProductServiceClient.class);
  }
}
