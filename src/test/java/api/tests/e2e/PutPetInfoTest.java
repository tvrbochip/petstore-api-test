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
@Feature("Update Pet Info")
public class PutPetInfoTest {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private static final Logger logger = LoggerFactory.getLogger(PutPetInfoTest.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private final Random rnd = new Random();
    private int rndNumber;
    private String strNumber;

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        petService = new PetService(BASE_URL);
        testDataCleanup = new TestDataCleanup();
        logger.info("\n\nInitializing PutPetInfoTest class");
    }

    @BeforeTest
    @Description("Generate random data for testing")
    public void generateData() {
        rndNumber = rnd.nextInt(99);
        strNumber = Integer.toString(rndNumber);
        logger.info("\n\nGenerated random data for testing: rndNumber = {}, strNumber = {}", rndNumber, strNumber);
    }

    @Test
    @Description("Test updating existing pet information")
    @Story("Positive Test: Updating existing pet information.")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateExistingPetInfo() {
        logger.info("\n\nStarting the test: testUpdateExistingPetInfo");

        String namePetOld = "testPetOld";
        String statusPetOld = "available";

        String namePetNew = "testPetNew";
        String statusPetNew = "pending";

        PetData originalPet = createPet(strNumber, namePetOld, statusPetOld);
        logger.info("\n\nCreating a pet with ID: {}", strNumber);

        Response response = petService.postNewPet(originalPet);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        response = petService.getPetByID(strNumber);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, originalPet);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);

        PetData updatedPet = createPet(strNumber, namePetNew, statusPetNew);
        logger.info("\n\nCreating a new pet with ID: {}", strNumber);

        response = petService.putPetInfo(updatedPet);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nUpdated pet with ID: {}", strNumber);

        response = petService.getPetByID(strNumber);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet a pet with ID: {}", strNumber);

        ResponseBody updatedBody = response.getBody();
        assertResponseBodyContainsFields(updatedBody, updatedPet);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);
        System.out.println(response.getBody().asString());
    }

    @Test
    @Description("Test updating non-existing pet information")
    @Story("Negative Test: Attempt to update non-existing pet information.")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistingPetInfo() {
        logger.info("\n\nStarting the test: testUpdateNonExistingPetInfo");

        String namePetOld = "testPetOld";
        String statusPetOld = "available";

        String namePetNew = "testPetNew";
        String statusPetNew = "pending";

        PetData petObj = createPet(strNumber, namePetOld, statusPetOld);
        logger.info("\n\nCreating a pet with ID: {}", strNumber);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        response = petService.getPetByID(petObj.getId());
        assertStatusCode(response, 200, 300);
        logger.info("\n\nGet a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, petObj);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);

        response = petService.deletePet(petObj.getId());
        assertStatusCode(response, 200, 300);
        logger.info("\n\nDeleting the pet with ID: {}", strNumber);

        response = petService.getPetByID(petObj.getId());
        assertStatusCode(response, 400, 500);
        logger.info("\n\nGet a deleted pet with ID: {}", strNumber);

        PetData updatedPet = createPet(strNumber, namePetNew, statusPetNew);
        logger.info("\n\nCreating a new pet with ID: {}", strNumber);

        response = petService.putPetInfo(updatedPet);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nUpdated pet with ID: {}", strNumber);
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
