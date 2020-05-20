package util;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class RestUtil {
    private static final Logger LOG = LoggerUtil.getLogger();

    private RestUtil() {
        throw new IllegalStateException("Can't instantiate the RestUtil class");
    }

    public static URI buildUri(String baseUrl, String endpoint, Map<String, String> queryParams) {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = null;
        String newBaseUrl = "";

        if (baseUrl.contains("http://")) {
            newBaseUrl = baseUrl.replace("http://", "");
            uriBuilder.setScheme("http");
        } else {
            newBaseUrl = baseUrl.replace("https://", "");
            uriBuilder.setScheme("https");
        }

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }

        uriBuilder.setHost(newBaseUrl);
        uriBuilder.setPath(endpoint);

        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException ex) {
            LOG.error("Unable to build url", ex);
        }

        return uri;
    }
}
