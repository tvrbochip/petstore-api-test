package api.services;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserService {
    private String baseUrl;

    public UserService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response getLoginUser(String login, String password) {
        return RestAssured
                .get(baseUrl + "/user/login?username=" + login + "&password=" + password + "/");
    }
}
