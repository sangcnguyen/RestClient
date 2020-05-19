package core;

import org.apache.http.Header;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import util.LoggerUtil;
import util.RestUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestClient implements Closeable {
    private static final Logger logger = LoggerUtil.getLogger();
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
     * Constructor for passing in a an httpClient and isHttp parameter to allow for http/https calls
     */
    public RestClient(boolean isHttp) {
        this(100, isHttp);
    }

    /**
     * Constructor for passing in a an httpClient
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

    public ResponseObject sendRequest(RequestObject request) {
        ResponseObject responseObject = null;
        if (request.getMethodType() == null) {
            logger.error("Only support GET, POST, PUT, PATCH and DELETE.");
        }

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
                logger.error("Only support GET, POST, PUT, PATCH and DELETE.");
        }

        return responseObject;
    }

    public ResponseObject get(RequestObject request) {
        HttpGet httpGet;

        uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
        httpGet = new HttpGet(uri.toString());


        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeApi(httpGet);
    }

    public ResponseObject post(RequestObject request) {
        HttpPost httpPost;


        uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
        httpPost = new HttpPost(uri.toString());


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


        uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
        httpPut = new HttpPut(uri.toString());


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

        uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
        httpPatch = new HttpPatch(uri.toString());


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

        uri = RestUtil.buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams(), this.isHttp);
        httpDelete = new HttpDelete(uri.toString());

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
            logger.error("", ex);
        }
        return responseObject;
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
        if (request.getHeaders().size() == 0 && request.getBody() != null) {
            request.addHeader("Content-Type", "application/json");
        }
    }

    @Override
    public void close() {
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            logger.error("Unable to close httpclient", ex);
        }
    }
}