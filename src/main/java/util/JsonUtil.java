package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {

    private JsonUtil() {
        throw new IllegalStateException("Can't instantiate the JsonUtil class");
    }


    public static String getPrettyString(String jsonString) {
        if (jsonString != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            return gson.toJson(parse(jsonString));
        }
        return null;
    }

    private static JsonElement parse(String jsonString) {
        return JsonParser.parseString(jsonString);
    }
}
