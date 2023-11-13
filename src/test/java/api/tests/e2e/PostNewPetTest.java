package api.tests.e2e;

import api.data.PetData;
import api.services.PetService;
import api.utils.TestDataCleanup;
import io.qameta.allure.*;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.Random;

@Epic("Pet Operations")
@Feature("Create Pet")
public class PostNewPetTest {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private static final Logger logger = LoggerFactory.getLogger(PostNewPetTest.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private final Random rnd = new Random();
    private int rndNumber;
    private String strNumber;

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        petService = new PetService(BASE_URL);
        testDataCleanup = new TestDataCleanup();
        logger.info("\n\nInitializing PostNewPetTest class");
    }

    @BeforeTest
    @Description("Generate random data for testing")
    public void generateData() {
        rndNumber = rnd.nextInt(99);
        strNumber = Integer.toString(rndNumber);
        logger.info("\n\nGenerated random data for testing: rndNumber = {}, strNumber = {}", rndNumber, strNumber);
    }

    @Test
    @Description("Test creating a new pet with valid data")
    @Story("Positive Test: Creating a pet with valid data.")
    public void testCreatePetValid() {
        logger.info("\n\nStarting the test: testCreatePetValid");
        String namePetNew = "testPetNew";
        String statusPetNew = "pending";

        PetData petObj = createPet(strNumber, namePetNew, statusPetNew);
        logger.info("\n\nCreating a pet with ID: {}, name: {}, and status: {}", strNumber, namePetNew, statusPetNew);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, petObj);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);
    }

    @AfterClass
    @Description("Cleanup after test class")
    public void cleanup() {
        testDataCleanup.cleanupPets();
    }

    private PetData createPet(String id, String name, String status) {
        PetData petObj = new PetData();
        petObj.setId(id);
        petObj.setName(name);
        petObj.setStatus(status);
        return petObj;
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code between " + expectedMinStatusCode + " and " + expectedMaxStatusCode + ", but got " + statusCode + "\n");
    }

    private void assertResponseBodyContainsFields(ResponseBody body, PetData petObj) {
        logger.info("\n\nChecking response body for ID, name, and status");
        Assert.assertTrue(body.asString().contains(petObj.getId()));
        Assert.assertTrue(body.asString().contains(petObj.getName()));
        Assert.assertTrue(body.asString().contains(petObj.getStatus()));
    }
}
