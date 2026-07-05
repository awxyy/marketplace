package steps.product;

import com.dotdot.marketplace.configuration.jwt.JwtProperties;
import com.dotdot.marketplace.product.dto.ProductRequestDto;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import steps.TestContext;

import javax.crypto.SecretKey;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductStepDefs {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TestContext context;

    @Before
    public void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Given("the seller profile is synchronized in the database")
    public void theSellerProfileIsSynchronizedInTheDatabase() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
            SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);

            Object claimsId = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(context.getAccessToken())
                    .getPayload()
                    .get("userId");

            if (claimsId == null) {
                throw new IllegalStateException("JWT Token parsing failed: 'userId' claim is missing.");
            }

            context.setDynamicSellerId(Long.valueOf(claimsId.toString()));

        } catch (Exception e) {
            throw new AssertionError("Failed to dynamically extract seller ID. Context: " + e.getMessage(), e);
        }
    }

    @Given("a product with name {string} already exists")
    public void aProductWithNameAlreadyExists(String name) {
        ProductRequestDto duplicate = new ProductRequestDto(name, "Pre-existing", 10.0, context.getDynamicSellerId(), 5);
        given()
                .header("Authorization", "Bearer " + context.getAccessToken())
                .contentType(ContentType.JSON)
                .body(duplicate)
                .post("/products")
                .then()
                .statusCode(200);
    }

    @When("I create a new product with the following details:")
    public void iCreateANewProductWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> row = dataTable.asMap(String.class, String.class);

        ProductRequestDto requestDto = new ProductRequestDto(
                row.get("name"),
                row.get("description"),
                Double.parseDouble(row.get("price")),
                context.getDynamicSellerId(),
                Integer.parseInt(row.get("quantity"))
        );
        context.setContextProductRequest(requestDto);

        context.setResponse(
                context.getRequest()
                        .contentType(ContentType.JSON)
                        .body(context.getContextProductRequest())
                        .post("/products")
        );

        if (context.getResponse().getStatusCode() == 200) {
            context.setGeneratedProductId(context.getResponse().as(ProductResponseDto.class).getId());
        }
    }

    @When("I try to create a product with name {string}, price {double}, and quantity {int}")
    public void iTryToCreateAProductWithValidationConstraints(String name, double price, int quantity) {
        ProductRequestDto requestDto = new ProductRequestDto(name, "Validation Test", price, context.getDynamicSellerId(), quantity);
        context.setContextProductRequest(requestDto);

        context.setResponse(
                context.getRequest()
                        .contentType(ContentType.JSON)
                        .body(context.getContextProductRequest())
                        .post("/products")
        );
    }

    @When("I request the product details by its generated ID")
    public void iRequestTheProductDetailsByItsGeneratedID() {
        context.setResponse(context.getRequest().get("/products/" + context.getGeneratedProductId()));
    }

    @When("I update product by its generated ID")
    public void iUpdateProductById(DataTable dataTable) {
        Map<String, String> row = dataTable.asMap(String.class, String.class);

        context.getContextProductRequest().setName(row.get("name"));
        context.getContextProductRequest().setPrice(Double.parseDouble(row.get("price")));

        context.setResponse(
                context.getRequest()
                        .contentType(ContentType.JSON)
                        .body(context.getContextProductRequest())
                        .put("/products/" + context.getGeneratedProductId())
        );
    }

    @When("I delete this product by its ID")
    public void iDeleteThisProductByItsID() {
        context.setResponse(context.getRequest().delete("/products/" + context.getGeneratedProductId()));
    }

    @Then("the server returns HTTP status {int}")
    public void theServerReturnsHTTPStatus(int statusCode) {
        context.getResponse().then().statusCode(statusCode);
    }

    @Then("the response headers contain {string} with value {string}")
    public void theResponseHeadersContainWithValue(String headerName, String expectedValue) {
        context.getResponse().then().header(headerName, equalTo(expectedValue));
    }

    @Then("the response body matches the Product DTO schema and values")
    public void theResponseBodyMatchesTheProductDTOSchemaAndValues() {
        ProductResponseDto actualResponse = context.getResponse().as(ProductResponseDto.class);

        assertNotNull(actualResponse.getId());
        assertNotNull(actualResponse.getCreatedAt());
        assertEquals(context.getContextProductRequest().getName(), actualResponse.getName());
        assertEquals(context.getContextProductRequest().getPrice(), actualResponse.getPrice(), 0.001);
        assertEquals(context.getContextProductRequest().getQuantity(), actualResponse.getQuantity());
        assertEquals(context.getContextProductRequest().getSellerId(), actualResponse.getSellerId());
    }

    @Then("product changes are persisted")
    public void productChangesArePersisted() {
        ProductResponseDto actualResponse = context.getResponse().as(ProductResponseDto.class);
        assertEquals(context.getContextProductRequest().getName(), actualResponse.getName(), "Name update was not persisted!");
        assertEquals(context.getContextProductRequest().getPrice(), actualResponse.getPrice(), 0.001, "Price update was not persisted!");
    }

    @Then("the retrieved product name is {string}")
    public void theRetrievedProductNameIs(String expectedName) {
        ProductResponseDto actualResponse = context.getResponse().as(ProductResponseDto.class);
        assertEquals(expectedName, actualResponse.getName());
    }

    @Then("the response body is empty")
    public void theResponseBodyIsEmpty() {
        context.getResponse().then().body(Matchers.emptyOrNullString());
    }

    @Then("the error response structure matches the validation schema with message {string}")
    public void theErrorResponseStructureMatchesTheValidationSchema(String expectedErrorMessage) {
        context.getResponse().then()
                .body("message", notNullValue())
                .body("errors", instanceOf(java.util.List.class))
                .body("errors", hasItem(expectedErrorMessage));
    }
}