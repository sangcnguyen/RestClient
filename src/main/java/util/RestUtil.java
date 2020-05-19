package util;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class RestUtil {
    private static final Logger logger = LoggerUtil.getLogger();

    private RestUtil() {
        throw new IllegalStateException("Can't instantiate the RestUtil class");
    }

    public static URI buildUri(String baseUrl, String endpoint, Map<String, String> queryParams, boolean isHttp) {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = null;

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
            logger.error("Invalid url", ex);
        }

        return uri;
    }
}
