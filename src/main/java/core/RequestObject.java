package core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class RequestObject {
    private MethodType method;
    private String endpoint;
    private String restUrl;
    private JsonElement body;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;

    public RequestObject() {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.reset();
    }

    public void reset() {
        this.clearMethod();
        this.clearEndpoint();
        this.clearRestUrl();
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

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
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

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getRestUrl() {
        return this.restUrl;
    }

    public JsonElement getBody() {
        return this.body;
    }

    private void clearMethod() {
        this.method = null;
    }

    private void clearEndpoint() {
        this.endpoint = "";
    }

    private void clearRestUrl() {
        this.restUrl = "";
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
        return "RequestObject { " +
                "method=" + method +
                ", endpoint='" + endpoint + '\'' +
                ", restUrl='" + restUrl + '\'' +
                ", \nbody=" + JsonUtil.getPrettyString(body) +
                ", \nheaders=" + headers +
                ", queryParams=" + queryParams +
                '}';
    }
}