import com.github.sn.MethodType;
import com.github.sn.RequestObject;
import com.github.sn.ResponseObject;
import com.github.sn.RestClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class SimpleRestTest {
    private RequestObject requestObject;
    private RestClient restClient;

    @BeforeClass
    public void setUp() {
        requestObject = new RequestObject();
        restClient = new RestClient();
        requestObject.setBaseUrl("dummy.restapiexample.com/api/v1");
    }


    @Test
    public void testGetMethod() throws IOException {
        requestObject.setEndpoint("/employees");
        requestObject.setMethod(MethodType.GET);
        ResponseObject response = restClient.sendRequest(requestObject);
        System.out.println(response.toString());
    }

    @Test
    public void testPostMethod() throws IOException {
        requestObject.setEndpoint("/create");
        requestObject.setMethod(MethodType.POST);
        requestObject.setBody("{\"name\":\"testes\",\"salary\":\"123\",\"age\":\"222223\"}");
        ResponseObject response = restClient.sendRequest(requestObject);
        System.out.println(response.toString());
    }
}
