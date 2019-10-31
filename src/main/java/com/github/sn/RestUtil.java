package com.github.sn;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class RestUtil {

    private RestUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static URI buildUrl(String baseUrl, String endpoint, Map<String, String> queryParams) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri;
        if (!baseUrl.startsWith("http")) {
            uriBuilder.setScheme("http");
        }

        uriBuilder.setHost(baseUrl);
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
}
