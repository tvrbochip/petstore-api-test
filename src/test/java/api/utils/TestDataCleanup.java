package api.utils;

import api.services.PetService;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestDataCleanup {
    private PetService petService;
    private Response response;
    private List<String> petIds;
    private static final Logger logger = LoggerFactory.getLogger(TestDataCleanup.class);
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    public TestDataCleanup() {
        petService = new PetService(BASE_URL);
        petIds = new ArrayList<>();
    }

    public void cleanupPets() {
        logger.info("Cleaning up pets");

        for (String id : petIds) {
            logger.info("Deleting pet with ID: {}", id);
            response = petService.deletePet(id);
            assertStatusCode(response, 200, 300);
        }
        petIds.clear();
    }

    public void addPetId(String petId) {
        petIds.add(petId);
    }

    private void assertStatusCode(Response response, int expectedMinStatusCode, int expectedMaxStatusCode) {
        int statusCode = response.getStatusCode();
        logger.info("\n\nReceived response with status code: {}", statusCode);
        logger.info("\n\nExpecting a status code between {} and {}", expectedMinStatusCode, expectedMaxStatusCode);

        if(!(statusCode >= expectedMinStatusCode && statusCode <= expectedMaxStatusCode)) {
            logger.info("\n\nExpected a status code between {} and {}, but got {}", expectedMinStatusCode, expectedMaxStatusCode, statusCode);
        }
    }
}