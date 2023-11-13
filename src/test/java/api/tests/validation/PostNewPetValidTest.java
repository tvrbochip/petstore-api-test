package api.tests.validation;

import api.data.PetData;
import api.services.PetService;
import api.utils.TestDataCleanup;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import io.qameta.allure.*;

import java.util.Random;

@Epic("Pet Operations")
@Feature("Create New Pet")
public class PostNewPetValidTest {
    private TestDataCleanup testDataCleanup;
    private PetService petService;
    private static final Logger logger = LoggerFactory.getLogger(PostNewPetValidTest.class);
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

    @DataProvider(name = "validId")
    public Object[][] getValidID() {
        return new Object[][]{
                {strNumber},
                {"1"},
        };
    }

    @DataProvider(name = "invalidId")
    public Object[][] getInvalidId() {
        return new Object[][]{
                {null}, // Null
                {""}, // Пустая строка
                {" "}, // Пробел
                {"phone123"}, // Недопустимые символы латиница
                {"!%:*()?123"}, // Недопустимые спец-символы
                {"123 456"}, // Пробел в середине
                {"-123"}, // Отрицательное число
                {"12.3"} // Число с плавающей точкой
        };
    }

    @DataProvider(name = "validName")
    public Object[][] getValidName() {
        return new Object[][]{
                {"testpet"},
                {"Testpet"},
                {"test-pet"}
        };
    }

    @DataProvider(name = "invalidName")
    public Object[][] getInvalidName() {
        return new Object[][]{
                {null}, // Null
                {""}, // Пустая строка
                {" "}, // Пробел
                {"!%:*()?"}, // Недопустимые спец-символы
                {"test test"}, // Пробел в середине
                {" testtest"}, // Пробел в начале
                {"testtest "}, // Пробел в конце
                {"123"}, // число
                {"12.3"} // Число с плавающей точкой
        };
    }

    @Description("Testing the ability to create a new pet with a valid ID.")
    @Story("Positive Test: Creating a pet with a valid ID.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "validId")
    public void testCreatePetValidId(String validId) {
        String namePet = "testPetNew";
        String statusPet = "pending";

        PetData petObj = createPet(validId, namePet, statusPet);
        logger.info("\n\nCreating a pet with ID: {}, name: {}, and status: {}", strNumber, namePet, statusPet);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, petObj);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);
    }

    @Description("Testing the system's reaction to creating a new pet with an invalid ID.")
    @Story("Negative Test: Attempt to create a pet with an invalid ID.")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "invalidId")
    public void testCreatePetInvalidId(String invalidId) {
        String namePet = "testPetNew";
        String statusPet = "pending";

        PetData petObj = createPet(invalidId, namePet, statusPet);
        logger.info("\n\nCreating a pet with ID: {}, name: {}, and status: {}", strNumber, namePet, statusPet);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 400, 500);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, petObj);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);
    }

    @Description("Testing the ability to create a new pet with a valid name.")
    @Story("Positive Test: Creating a pet with a valid name.")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "validName")
    public void testCreatePetValidName(String validName) {
        String namePet = validName;
        String statusPet = "pending";

        PetData petObj = createPet(strNumber, namePet, statusPet);
        logger.info("\n\nCreating a pet with ID: {}, name: {}, and status: {}", strNumber, namePet, statusPet);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 200, 300);
        logger.info("\n\nAdded a pet with ID: {}", strNumber);

        ResponseBody body = response.getBody();
        assertResponseBodyContainsFields(body, petObj);
        logger.info("\n\nChecking the pet's name with ID: {}", strNumber);
    }

    @Description("Testing the system's reaction to creating a new pet with an invalid name.")
    @Story("Negative Test: Attempt to create a pet with an invalid name.")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "invalidName")
    public void testCreatePetInvalidName(String invalidName) {
        String namePet = invalidName;
        String statusPet = "pending";

        PetData petObj = createPet(strNumber, namePet, statusPet);
        logger.info("\n\nCreating a pet with ID: {}, name: {}, and status: {}", strNumber, namePet, statusPet);

        Response response = petService.postNewPet(petObj);
        assertStatusCode(response, 400, 500);
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
        Assert.assertTrue(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode, "\nExpected a status code greater than or equal to " + expectedMinStatusCode + ", but got " + statusCode + "\n");
    }

    private void assertResponseBodyContainsFields(ResponseBody body, PetData petObj) {
        Assert.assertTrue(body.asString().contains(petObj.getId()));
        Assert.assertTrue(body.asString().contains(petObj.getName()));
        Assert.assertTrue(body.asString().contains(petObj.getStatus()));
    }
}
