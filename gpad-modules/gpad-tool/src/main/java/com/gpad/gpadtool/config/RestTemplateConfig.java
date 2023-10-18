package com.gpad.gpadtool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName RestTemplateConfig.java
 * @Description TODO
 * @createTime 2023年09月21日 17:18:00
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        //修改restTemplate的RequestFactory使其支持Get携带body参数
        restTemplate.setRequestFactory(new HttpComponentsClientRestfulHttpRequestFactory());
        return restTemplate;
    }

    @Bean(name="remoteRestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory simpleClientHttpRequestFactory){
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        restTemplate.setRequestFactory(new HttpComponentsClientRestfulHttpRequestFactory());
        return restTemplate;
    }
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(20000);
        return factory;
    }
}
