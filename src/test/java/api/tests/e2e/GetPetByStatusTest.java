package api.tests.e2e;

import api.services.PetService;
import api.utils.TestDataCleanup;
import io.qameta.allure.*;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

@Epic("Pet Operations")
@Feature("Get Pet by Status")
public class GetPetByStatusTest {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private static final Logger logger = LoggerFactory.getLogger(GetPetByStatusTest.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        petService = new PetService(BASE_URL);
        testDataCleanup = new TestDataCleanup();
        logger.info("Initializing GetPetByStatusTest class");
    }

    @DataProvider(name = "validStatusData")
    public Object[][] getValidStatus() {
        return new Object[][]{
                {"available"},
                {"pending"},
                {"sold"},
        };
    }

    @Test(dataProvider = "validStatusData")
    @Description("Test getting pets by valid status")
    @Story("Positive Test: Getting pets with valid status.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetValidStatus(String validStatus) {
        logger.info("Starting the test for status: {}", validStatus);

        Response response = petService.getPetByStatus(validStatus);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet a pet with status: {}", validStatus);

        String responseBody = response.getBody().asString();
        JSONArray jsonArray = new JSONArray(responseBody);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String status = jsonObject.getString("status");
            Assert.assertEquals(validStatus, status, "Expected status doesn't match actual status");
        }
        logger.info("\n\nChecking status for objects in the response");
    }

    @AfterClass
    @Description("Cleanup after test class")
    public void cleanup() {
        testDataCleanup.cleanupPets();
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code between " + expectedMinStatusCode + " and " + expectedMaxStatusCode + ", but got " + statusCode + "\n");
    }
}
