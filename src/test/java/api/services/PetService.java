package api.services;

import api.data.PetData;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PetService {
    private String baseUrl;
    private final String API_KEY = "special-key";

    public PetService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response getPetByID(String ID) {
        return RestAssured
                .get(baseUrl + "/pet/" + ID);
    }

    public Response getPetByStatus(String Status) {
        return RestAssured
                .get(baseUrl + "/pet/findByStatus?status=" + Status);
    }

    public Response postNewPet(PetData petObj) {
        return RestAssured
                .given()
                .contentType("application/json")
                .body(petObj)
                .post(baseUrl + "/pet");
    }

    public Response putPetInfo(PetData petObj) {
        return RestAssured
                .given()
                .contentType("application/json")
                .body(petObj)
                .put(baseUrl + "/pet");
    }

    public Response deletePet(String ID) {
        return RestAssured.given()
                .header("api_key", API_KEY)
                .delete(baseUrl + "/pet/" + ID);
    }

}