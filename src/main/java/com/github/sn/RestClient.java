package com.github.sn;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
    private boolean test;
    private boolean createdHttpClient;

    public RestClient() {
        this.httpClient = HttpClients.createDefault();
        this.createdHttpClient = true;
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
        URI uri;
        HttpGet httpGet;
        try {
            uri = buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams());
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
        URI uri;
        HttpPost httpPost;

        try {
            uri = buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams());
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
        if (request.getContentType() == null) {
            writeDefaultContentType(request, httpPost);
        }

        return executeApi(httpPost);
    }

    public ResponseObject put(RequestObject request) throws URISyntaxException, IOException {
        URI uri;
        HttpPut httpPut;

        try {
            uri = buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams());
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
        if (request.getContentType() == null) {
            writeDefaultContentType(request, httpPut);
        }

        return executeApi(httpPut);
    }

    public ResponseObject patch(RequestObject request) throws URISyntaxException, IOException {
        URI uri;
        HttpPatch httpPatch;
        try {
            uri = buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams());
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
        if (request.getContentType() == null) {
            writeDefaultContentType(request, httpPatch);
        }

        return executeApi(httpPatch);
    }

    public ResponseObject delete(RequestObject request) throws URISyntaxException, IOException {
        URI uri;
        HttpDelete httpDelete;

        try {
            uri = buildUri(request.getBaseUrl(), request.getEndpoint(), request.getQueryParams());
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

    private URI buildUri(String baseUri, String endpoint, Map<String, String> queryParams) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri;
        if (this.test = true) {
            uriBuilder.setScheme("http");
        } else {
            uriBuilder.setScheme("https");
        }

        uriBuilder.setHost(baseUri);
        uriBuilder.setPath(endpoint);

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }

        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException ex) {
            throw ex;
        }
        return uri;
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

    private void writeDefaultContentType(RequestObject request, HttpMessage httpMessage) {
        if (!"".equals(request.getBody())) {
            httpMessage.setHeader("Content-Type", "application/json");
        }
    }

    @Override
    public void close() throws IOException {
        this.httpClient.close();
    }
}