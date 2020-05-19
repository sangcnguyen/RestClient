package com.github.sn;

import java.util.Map;

public class ResponseObject {
    private int statusCode;
    private String body;
    private Map<String, String> headers;

    public ResponseObject(int statusCode, String body, Map<String, String> header) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = header;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "Status code: " + getStatusCode() +
                "\nHeaders: " + getHeaders() +
                "\nResponse: \n" + getBody();
    }
}