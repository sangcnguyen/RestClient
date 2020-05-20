import com.google.gson.JsonObject;
import core.MethodType;
import core.RequestObject;
import core.ResponseObject;
import core.RestClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RestClientTest {
    private RequestObject request;
    private RestClient restClient;

    @BeforeClass
    public void beforeClass() {
        request = new RequestObject();
        restClient = new RestClient();
        restClient.setBaseUrl("reqres.in/api/");
    }

    @AfterClass
    public void afterClass() {
        restClient.close();
    }

    @Test
    public void testGetMethod() {
        request.setEndpoint("users?page=2");
        request.setMethod(MethodType.GET);
        ResponseObject response = restClient.sendRequest(request);
        System.out.println(request.toString());
        System.out.println(response.toString());
    }

    @Test
    public void testPostMethod() {
        request.setEndpoint("users");
        request.setMethod(MethodType.POST);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "sang");
        jsonObject.addProperty("job", "tester");
        request.setBody(jsonObject);
        ResponseObject response = restClient.sendRequest(request);
        System.out.println(request.toString());
        System.out.println(response.toString());
    }

    @Test
    public void testPutMethod() {
        request.setEndpoint("users/2");
        request.setMethod(MethodType.PUT);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "sang");
        jsonObject.addProperty("job", "leader");
        request.setBody(jsonObject);
        ResponseObject response = restClient.sendRequest(request);
        System.out.println(request.toString());
        System.out.println(response.toString());
    }

    @Test
    public void testDeleteMethod() {
        request.setEndpoint("users/2");
        request.setMethod(MethodType.DELETE);
        ResponseObject response = restClient.sendRequest(request);
        System.out.println(request.toString());
        System.out.println(response.toString());
    }
}
