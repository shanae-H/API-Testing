package org.example.apihelpers;

import static io.restassured.RestAssured.given;

public class BoardCreateHelper {

    public int deleteBoard(String ACCESS_KEY, String boardId){
        return given()
                .contentType("application/json")
                .when()
                .delete(boardId + "?"+ ACCESS_KEY).getStatusCode();
    }
}
