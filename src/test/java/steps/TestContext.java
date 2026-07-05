package steps;

import com.dotdot.marketplace.product.dto.ProductRequestDto;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import org.springframework.boot.test.context.TestComponent;
import io.cucumber.spring.ScenarioScope;

@TestComponent
@ScenarioScope
public class TestContext {
    private RequestSpecification request;
    private Response response;
    private String accessToken;
    private Long generatedProductId;
    private Long dynamicSellerId;
    private String currentLogin;
    private ProductRequestDto contextProductRequest;

    public RequestSpecification getRequest() { return request; }
    public void setRequest(RequestSpecification request) { this.request = request; }

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public Long getGeneratedProductId() { return generatedProductId; }
    public void setGeneratedProductId(Long generatedProductId) { this.generatedProductId = generatedProductId; }

    public Long getDynamicSellerId() { return dynamicSellerId; }
    public void setDynamicSellerId(Long dynamicSellerId) { this.dynamicSellerId = dynamicSellerId; }

    public String getCurrentLogin() { return currentLogin; }
    public void setCurrentLogin(String currentLogin) { this.currentLogin = currentLogin; }

    public ProductRequestDto getContextProductRequest() { return contextProductRequest; }
    public void setContextProductRequest(ProductRequestDto contextProductRequest) { this.contextProductRequest = contextProductRequest; }
}