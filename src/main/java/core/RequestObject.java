package core;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class RequestObject {
    private MethodType method;
    private String baseUrl;
    private String endpoint;
    private JsonObject body;
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

    public void setBody(JsonObject body) {
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

    public JsonObject getBody() {
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
        this.body = null;
    }

    private void clearQueryParams() {
        this.queryParams.clear();
    }

    private void clearHeaders() {
        this.headers.clear();
    }

    @Override
    public String toString() {
        return "RequestObject{ " +
                "method=" + method +
                ", baseUrl='" + baseUrl + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                " }";
    }
}