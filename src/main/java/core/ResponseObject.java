package core;

import com.google.gson.JsonElement;
import util.JsonUtil;

import java.util.Map;

public class ResponseObject {
    private int statusCode;
    private JsonElement body;
    private Map<String, String> headers;

    public ResponseObject(int statusCode, JsonElement body, Map<String, String> header) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = header;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public JsonElement getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public String toString() {
        return "ResponseObject { " +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", \nbody=" + JsonUtil.getPrettyString(body) +
                "}";
    }
}