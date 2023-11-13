package api.tests.validation;

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
public class GetPetByIdValidTests {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetPetByIdValidTests.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private final Random rnd = new Random();
    private int rndNumber;
    private String strNumber;

    @BeforeClass
    @Description("Initialize test class")
    public void setUp() {
        petService = new PetService(BASE_URL);
        testDataCleanup = new TestDataCleanup();
        logger.info("\n\nInitializing GetPetByIdValidTests class");
    }

    @BeforeTest
    @Description("Generate random data for testing")
    public void generateData() {
        rndNumber = rnd.nextInt(99);
        strNumber = Integer.toString(rndNumber);
        logger.info("\n\nGenerated random data for testing: rndNumber = {}, strNumber = {}", rndNumber, strNumber);
    }

    @DataProvider(name = "validId")
    public Object[][] getValidID() {
        return new Object[][]{
                {strNumber},
                {"1"}, // мин. длинна
                {"9223372036854775807"}, // макс. длинна
        };
    }

    @DataProvider(name = "invalidId")
    public Object[][] getInvalidID() {
        return new Object[][]{
                {" "}, // Пробел
                {"phone123"}, // Недопустимые символы латиница
                {"!%:*()?123"}, // Недопустимые спец-символы
                {"123 456"}, // Пробел в середине
                {"-123"}, // Отрицательное число
                {"12.3"}, // Число с плавающей точкой
                {"92233720368547758089"}, // Длина превышает максимально допустимую
        };
    }

    @Description("Testing the ability to get a pet by providing a valid ID.")
    @Story("Positive Test: Retrieving a pet using a valid ID.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "validId")
    public void testGetValidID(String validId) {
        PetData petObj = createPet(validId);
        logger.info("\n\nCreating a pet with ID: {}", validId);

        response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", validId);

        response = petService.getPetByID(validId);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nRequested pet data for ID: {}", validId);
    }

    @Description("Testing the system's reaction to getting a pet with an invalid ID.")
    @Story("Negative Test: Attempt to retrieve a pet using an invalid ID.")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "invalidId")
    public void testGetInvalidID(String invalidId) {
        response = petService.getPetByID(invalidId);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nRequested pet data for ID: {}", invalidId);
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
        petObj.setStatus("aviable");
        return petObj;
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code greater than or equal to " + expectedMinStatusCode + ", but got " + statusCode + "\n");
    }
}
