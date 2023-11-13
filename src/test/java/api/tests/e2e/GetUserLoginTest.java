package api.tests.e2e;

import api.services.UserService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

@Epic("User Operations")
@Feature("Login Operation")
public class GetUserLoginTest {
    private UserService userService;
    private Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetUserLoginTest.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        userService = new UserService(BASE_URL);
        logger.info("\n\nInitializing GetPetByIdTests class");
    }

    @Test
    @Description("Test getting a user's login with valid credentials.")
    @Story("Positive Test: Getting a user's login with a valid login and password.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserLogin() {
        logger.info("\n\nStarting the test: testGetUserLogin");
        String log = "userLoginTest";
        String pass = "userPasswordTest";
        Response response = userService.getLoginUser(log, pass);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet user login with login: {} and password: {}", log, pass);
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code between " + expectedMinStatusCode + " and " + expectedMaxStatusCode + ", but got " + statusCode + "\n");
    }
}
