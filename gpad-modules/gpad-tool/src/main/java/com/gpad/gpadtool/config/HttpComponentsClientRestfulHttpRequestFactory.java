package com.gpad.gpadtool.config;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HttpComponentsClientRestfulHttpRequestFactory.java
 * @Description TODO
 * @createTime 2023年09月21日 17:19:00
 */
public class HttpComponentsClientRestfulHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
    @Override
    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {

        if (httpMethod == HttpMethod.GET) {
            return new HttpGetRequestWithEntity(uri);
        }
        return super.createHttpUriRequest(httpMethod, uri);
    }

    /**
     * 定义HttpGetRequestWithEntity实现HttpEntityEnclosingRequestBase抽象类，以支持GET请求携带body数据
     */

    private static final class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
        public HttpGetRequestWithEntity(final URI uri) {
            super.setURI(uri);
        }

        @Override
        public String getMethod() {
            return HttpMethod.GET.name();

        }
    }
}
