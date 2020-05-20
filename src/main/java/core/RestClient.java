package core;

import com.google.gson.JsonElement;
import org.apache.http.Header;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import util.JsonUtil;
import util.LoggerUtil;
import util.RestUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestClient implements Closeable {
    private static final Logger LOG = LoggerUtil.getLogger();
    private CloseableHttpClient httpClient;
    private URI uri;
    private String BASE_URL = "";

    /**
     * Constructor for using the default CloseableHttpClient.
     */
    public RestClient() {
        this(100);
    }

    public RestClient(int defaultTimeOutInSeconds) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder.setConnectTimeout(defaultTimeOutInSeconds * 1000);
        requestBuilder.setConnectionRequestTimeout(defaultTimeOutInSeconds * 1000);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        this.httpClient = builder.build();
    }

    public void setBaseUrl(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public ResponseObject sendRequest(RequestObject request) {
        ResponseObject responseObject = null;

        switch (request.getMethodType()) {
            case GET:
                responseObject = get(request);
                break;
            case POST:
                responseObject = post(request);
                break;
            case PUT:
                responseObject = put(request);
                break;
            case PATCH:
                responseObject = patch(request);
                break;
            case DELETE:
                responseObject = delete(request);
                break;
            default:
                LOG.error("Only support GET, POST, PUT, PATCH and DELETE.");
        }

        return responseObject;
    }

    public ResponseObject get(RequestObject request) {
        HttpGet httpGet;

        if (request.getRestUrl().isEmpty()) {
            uri = RestUtil.buildUri(BASE_URL, request.getEndpoint(), request.getQueryParams());
            httpGet = new HttpGet(uri.toString());
        } else {
            httpGet = new HttpGet(request.getRestUrl());
        }


        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeApi(httpGet);
    }

    public ResponseObject post(RequestObject request) {
        HttpPost httpPost;

        if (request.getRestUrl().isEmpty()) {
            uri = RestUtil.buildUri(BASE_URL, request.getEndpoint(), request.getQueryParams());
            httpPost = new HttpPost(uri.toString());
        } else {
            httpPost = new HttpPost(request.getRestUrl());
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPost.setEntity(new StringEntity(String.valueOf(request.getBody()), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPost);
    }

    public ResponseObject put(RequestObject request) {
        HttpPut httpPut;

        if (request.getRestUrl().isEmpty()) {
            uri = RestUtil.buildUri(BASE_URL, request.getEndpoint(), request.getQueryParams());
            httpPut = new HttpPut(uri.toString());
        } else {
            httpPut = new HttpPut(request.getRestUrl());
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPut.setEntity(new StringEntity(String.valueOf(request.getBody()), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPut);
    }

    public ResponseObject patch(RequestObject request) {
        HttpPatch httpPatch;

        if (request.getRestUrl().isEmpty()) {
            uri = RestUtil.buildUri(BASE_URL, request.getEndpoint(), request.getQueryParams());
            httpPatch = new HttpPatch(uri.toString());
        } else {
            httpPatch = new HttpPatch(request.getRestUrl());
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpPatch.setHeader(entry.getKey(), entry.getValue());
            }
        }

        httpPatch.setEntity(new StringEntity(String.valueOf(request.getBody()), StandardCharsets.UTF_8));
        setDefaultHeaderIfHasBodyContent(request);

        return executeApi(httpPatch);
    }

    public ResponseObject delete(RequestObject request) {
        HttpDelete httpDelete;

        if (request.getRestUrl().isEmpty()) {
            uri = RestUtil.buildUri(BASE_URL, request.getEndpoint(), request.getQueryParams());
            httpDelete = new HttpDelete(uri.toString());
        } else {
            httpDelete = new HttpDelete(request.getRestUrl());
        }

        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeApi(httpDelete);
    }


    private ResponseObject executeApi(HttpRequestBase requestBase) {
        ResponseObject responseObject = null;
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(requestBase);
            responseObject = getResponse(httpResponse);
        } catch (IOException ex) {
            LOG.error("Failed to execute request", ex);
        }

        return responseObject;
    }

    public ResponseObject getResponse(CloseableHttpResponse response) throws IOException {
        ResponseHandler<String> handler = new CustomResponseHandler();
        String responseBody = handler.handleResponse(response);
        JsonElement jsonResponse = JsonUtil.parse(responseBody);
        int statusCode = response.getStatusLine().getStatusCode();
        Header[] headers = response.getAllHeaders();
        Map<String, String> responseHeader = new HashMap<>();
        for (Header header : headers) {
            responseHeader.put(header.getName(), header.getValue());
        }

        return new ResponseObject(statusCode, jsonResponse, responseHeader);
    }

    private void setDefaultHeaderIfHasBodyContent(RequestObject request) {
        if (request.getHeaders().size() == 0 && request.getBody() != null) {
            request.addHeader("Content-Type", "application/json");
        }
    }

    @Override
    public void close() {
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            LOG.error("Unable to close httpclient", ex);
        }
    }
}