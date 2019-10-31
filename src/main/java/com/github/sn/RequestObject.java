package com.github.sn;

import org.apache.http.entity.ContentType;

import java.util.HashMap;
import java.util.Map;

public class RequestObject {
    private MethodType method;
    private String baseUrl;
    private String endpoint;
    private String body;
    private ContentType contentType;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;

    public RequestObject() {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.reset();
    }

    public void reset() {
        this.clearMethod();
        this.clearBaseUrl();
        this.clearEndpoint();
        this.clearBody();
        this.clearHeaders();
        this.clearQueryParams();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void addQueryParam(String key, String value) {
        this.queryParams.put(key, value);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String removeQueryParam(String key) {
        return this.queryParams.remove(key);
    }

    public String removeHeader(String key) {
        return this.headers.remove(key);
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getQueryParams() {
        return this.queryParams;
    }

    public MethodType getMethodType() {
        return this.method;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getBody() {
        return this.body;
    }

    private void clearMethod() {
        this.method = null;
    }

    private void clearBaseUrl() {
        this.baseUrl = "";
    }

    private void clearEndpoint() {
        this.endpoint = "";
    }

    private void clearBody() {
        this.body = "";
    }

    private void clearQueryParams() {
        this.queryParams.clear();
    }

    private void clearHeaders() {
        this.headers.clear();
    }
}