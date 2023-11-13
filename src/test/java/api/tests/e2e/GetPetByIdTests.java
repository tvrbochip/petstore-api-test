package api.tests.e2e;

import api.data.PetData;
import api.services.PetService;
import api.utils.TestDataCleanup;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.Random;

@Epic("Pet Operations")
@Feature("Get Pet by ID")
public class GetPetByIdTests {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetPetByIdTests.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private final Random rnd = new Random();
    private int rndNumber;
    private String strNumber;

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        petService = new PetService(BASE_URL);
        testDataCleanup = new TestDataCleanup();
        logger.info("\n\nInitializing GetPetByIdTests class");
    }

    @BeforeTest
    @Description("Generate random data for testing")
    public void generateData() {
        rndNumber = rnd.nextInt(99);
        strNumber = Integer.toString(rndNumber);
        logger.info("\n\nGenerated random data for testing: rndNumber = {}, strNumber = {}", rndNumber, strNumber);
    }

    @Test
    @Description("Test getting an existing pet.")
    @Story("Positive Test: Getting a pet with a valid ID.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetExistingPet() {
        logger.info("\n\nStarting the test: testGetExistingObject");
        
        PetData petObj = createPet(strNumber);
        logger.info("\n\nCreating a pet with ID: {}", strNumber);

        response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);
        
        response = petService.getPetByID(strNumber);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nRequested pet data for ID: {}", strNumber);
    }

    @Test
    @Description("Test getting a non-existing pet.")
    @Story("Negative Test: Attempt to get a non-existing pet with an invalid ID.")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistingPet() {
        logger.info("\n\nStarting the test: testGetNonExistingObject");
        
        PetData petObj = createPet(strNumber);
        logger.info("\n\nCreating a pet with ID: {}", strNumber);

        response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        response = petService.deletePet(strNumber);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nDeleted pet with ID: {}", strNumber);

        response = petService.getPetByID(strNumber);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nRequested pet data for ID: {}", strNumber);
    }

    @AfterClass
    @Description("Cleanup after test class")
    public void cleanup() {
        testDataCleanup.cleanupPets();
    }

    private PetData createPet(String id) {
        PetData petObj = new PetData();
        petObj.setId(id);
        petObj.setName("testPet");
        petObj.setStatus("available");
        return petObj;
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code between " + expectedMinStatusCode + " and " + expectedMaxStatusCode + ", but got " + statusCode + "\n");
    }
}
