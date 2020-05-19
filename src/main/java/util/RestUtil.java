package core;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class RestUtil {

    private RestUtil() {
        throw new IllegalStateException("Can't instantiate the RestUtil class");
    }

    public static URI buildUri(String baseUrl, String endpoint, Map<String, String> queryParams, boolean isHttp) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri;
        if (isHttp) {
            uriBuilder.setScheme("http");
        } else {
            uriBuilder.setScheme("https");
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
