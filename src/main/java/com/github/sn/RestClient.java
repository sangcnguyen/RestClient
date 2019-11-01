package com.github.sn;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestClient implements Closeable {
    private CloseableHttpClient httpClient;
    private URI uri;
    private boolean isHttp;

    /**
     * Constructor for using the default CloseableHttpClient.
     */
    public RestClient() {
        this(100, true);
    }

    /**
     * Constructor for passing in a  an httpClient and isHttp parameter to allow for http/https calls
     */
    public RestClient(boolean isHttp) {
        this(100, isHttp);
    }

    /**
     * Constructor for passing in a  an httpClient
     * and defaultTimeOutInSeconds parameter to allow for set connection timeout
     */
    public RestClient(int defaultTimeOutInSeconds) {
        this(defaultTimeOutInSeconds, true);
    }

    public RestClient(int defaultTimeOutInSeconds, boolean isHttp) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder.setConnectTimeout(defaultTimeOutInSeconds * 1000);
        requestBuilder.setConnectionRequestTimeout(defaultTimeOutInSeconds * 1000);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        this.httpClient = builder.build();
        this.isHttp = isHttp;
    }

    public ResponseObject sendRequest(RequestObject request) throws IOException {
        try {
            if (request.getMethodType() == null) {
                throw new IOException("We only support GET, POST, PUT, PATCH and DELETE.");
            }
            switch (request.getMethodType()) {
                case GET:
                    return get(request);
                case POST:
                    return post(request);
                case PUT:
                    return put(request);
                case PATCH:
                    return patch(request);
                case DELETE:
                    return delete(request);
                default:
                    throw new IOException("We only support GET, POST, PUT, PATCH and DELETE.");
            }
        } catch (IOException ex) {
            throw ex;
        } catch (URISyntaxException ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            throw new IOException(errors.toString());
        }
    }

    public ResponseObject get(RequestObject request) throws URISyntaxException, IOException {
        HttpGet httpGet;
        try {
            uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
            httpGet = new HttpGet(uri.toString());
        } catch (URISyntaxException ex) {
            throw ex;
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeApi(httpGet);
    }

    public ResponseObject post(RequestObject request) throws URISyntaxException, IOException {
        HttpPost httpPost;
        try {
            uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
            httpPost = new HttpPost(uri.toString());
        } catch (URISyntaxException ex) {
            throw ex;
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPost.setEntity(new StringEntity(request.getBody(), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPost);
    }

    public ResponseObject put(RequestObject request) throws URISyntaxException, IOException {
        HttpPut httpPut;

        try {
            uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
            httpPut = new HttpPut(uri.toString());
        } catch (URISyntaxException ex) {
            throw ex;
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPut.setEntity(new StringEntity(request.getBody(), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPut);
    }

    public ResponseObject patch(RequestObject request) throws URISyntaxException, IOException {
        HttpPatch httpPatch;
        try {
            uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
            httpPatch = new HttpPatch(uri.toString());
        } catch (URISyntaxException ex) {
            throw ex;
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPatch.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPatch.setEntity(new StringEntity(request.getBody(), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPatch);
    }

    public ResponseObject delete(RequestObject request) throws URISyntaxException, IOException {
        HttpDelete httpDelete;
        try {
            uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
            httpDelete = new HttpDelete(uri.toString());
        } catch (URISyntaxException ex) {
            throw ex;
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeApi(httpDelete);
    }


    private ResponseObject executeApi(HttpRequestBase requestBase) throws IOException {
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(requestBase);
            try {
                return getResponse(httpResponse);
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            throw new IOException(e.getMessage());
        }
    }

    public ResponseObject getResponse(CloseableHttpResponse response) throws IOException {
        ResponseHandler<String> handler = new CustomResponseHandler();
        String responseBody = handler.handleResponse(response);
        int statusCode = response.getStatusLine().getStatusCode();
        Header[] headers = response.getAllHeaders();
        Map<String, String> responseHeader = new HashMap<>();
        for (Header header : headers) {
            responseHeader.put(header.getName(), header.getValue());
        }

        return new ResponseObject(statusCode, responseBody, responseHeader);
    }

    private void setDefaultHeaderIfHasBodyContent(RequestObject request) {
        if (request.getHeaders() == null && !"".equals(request.getBody())) {
            request.addHeader("Content-Type", "application/json");
        }
    }

    @Override
    public void close() throws IOException {
        this.httpClient.close();
    }
}