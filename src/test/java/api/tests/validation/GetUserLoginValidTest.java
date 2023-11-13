package api.tests.validation;

import api.services.UserService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;


@Epic("User Operations")
@Feature("Login Validation")
public class GetUserLoginValidTest {
    private UserService userService;
    private Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetUserLoginValidTest.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        userService = new UserService(BASE_URL);
        logger.info("\n\nInitializing GetUserLoginValidTest class");
    }

    @DataProvider(name = "validLogin")
    public Object[][] getValidLogin() {
        return new Object[][]{
                {"test"},
                {"testTest"},
                {"test-Test"},
                {"test-Test123"},
        };
    }

    @DataProvider(name = "validPassword")
    public Object[][] getValidPassword() {
        return new Object[][]{
                {"test"},
                {"testTest"},
                {"test-Test"},
                {"test-Test123"},
                {"test-!@#$%^"},
        };
    }

    @DataProvider(name = "invalidLogin")
    public Object[][] getInvalidLogin() {
        return new Object[][]{
                {null},
                {""},
                {" "},
                {" test"},
                {"test test"},
                {"testtest "},
                {"!$#%&#!"},
                {"1234"},
        };
    }

    @DataProvider(name = "invalidPassword")
    public Object[][] getInvalidPassword() {
        return new Object[][]{
                {null},
                {""},
                {" "},
                {" test"},
                {"test test"},
                {"testtest "},
                {"1234"},
        };
    }

    @Description("Positive Test: Getting user login with valid credentials.")
    @Story("Positive Test: Retrieving user login with valid login and password.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "validLogin")
    public void testGetValidLogin(String validLogin) {
        logger.info("\n\nStarting the test: testGetValidLogin");
        String pass = "userPasswordTest";
        Response response = userService.getLoginUser(validLogin, pass);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet user login with login {} ", validLogin);
    }

    @Description("Positive Test: Getting user login with valid password.")
    @Story("Positive Test: Retrieving user login with valid login and password.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "validPassword")
    public void testGetValidPassword(String validPassword) {
        logger.info("\n\nStarting the test: testGetValidLogin");
        String log = "test";
        Response response = userService.getLoginUser(log, validPassword);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet user login with password: {}", log, validPassword);
    }

    @Description("Negative Test: Handling invalid login.")
    @Story("Negative Test: Handling invalid login.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "invalidLogin")
    public void testGetInvalidLogin(String invalidLogin) {
        logger.info("\n\nStarting the test: testGetInvalidLogin");
        String invalidPassword = "invalidPasswordTest";
        Response response = userService.getLoginUser(invalidLogin, invalidPassword);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nAttempted to get user login with invalid login: {}", invalidLogin);
    }

    @Description("Negative Test: Handling invalid password.")
    @Story("Negative Test: Handling invalid password.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "invalidPassword")
    public void testGetInvalidPassword(String invalidPassword) {
        logger.info("\n\nStarting the test: testGetInvalidPassword");
        String validLogin = "test";
        Response response = userService.getLoginUser(validLogin, invalidPassword);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nAttempted to get user login with invalid password: {}", invalidPassword);
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code between " + expectedMinStatusCode + " and " + expectedMaxStatusCode + ", but got " + statusCode + "\n");
    }
}
