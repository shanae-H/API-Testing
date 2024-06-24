package restAssured;

import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.example.Board;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TrelloAPITest {
    Properties prop = new Properties();
    String ACCESS_KEY;
    static String boardId;

    public static String getBoardId() {
        return boardId;
    }

    public static void setBoardId(String newBoardId) {
        if (boardId==null){
            boardId = newBoardId;
        }
    }

    @BeforeTest
    public void beforeAllTests() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("api.properties");
        prop.load(input);
        String API_KEY = prop.get("key").toString();
        String TOKEN = prop.get("token").toString();
        ACCESS_KEY = "token=" + TOKEN + "&key=" + API_KEY;
        RestAssured.baseURI = "https://api.trello.com/1/boards/";
    }

    @Test
    void createBoard(){
        ValidatableResponse response = given()
                .contentType("application/json")
                // .body(new Board("abc"))
                .queryParam("name","Brand New Appliances12345")
                .queryParam("desc","Describes in detail household appliances to be purchased")
                .when()
                .post("?"+ACCESS_KEY)
                .then()
                .statusCode(200)
                .body("name",equalTo("Brand New Appliances"));
        TrelloAPITest.setBoardId(response.extract().jsonPath().getString("id"));
        System.out.println(response.extract().jsonPath().getString("id"));

        System.out.println("New board created: "+TrelloAPITest.boardId);
    }

    @Test(dependsOnMethods = {"createBoard"})
    void getBoard() {
        ResponseBody responseBody = given()
                .when()
                .get( TrelloAPITest.boardId +"/?" + ACCESS_KEY).getBody();
        responseBody.as(Board.class);
        System.out.println("The following board(s) have been found \n" +responseBody.prettyPrint());
        Assert.assertTrue(responseBody.prettyPrint().contains((getBoardId())));
    }

    @Test(dependsOnMethods = {"getBoard"})
    void updateBoard(){
        System.out.println(getBoardId());
        ValidatableResponse response = given()
                    .header("Accept","application/json")
                    .contentType("application/json")
                    .queryParam("name","Household Shopping List")
                    .queryParam("desc","Describes list of kitchen and bathroom appliances to be purchased")
                .when()
                    .put(TrelloAPITest.boardId+"/?"+ACCESS_KEY)
                .then()
                    .statusCode(200);
    }

    @Test (dependsOnMethods = {"updateBoard"})
    void deleteBoard(){
        ValidatableResponse response =given()
                .contentType("application/json")
                .when()
                    .delete(TrelloAPITest.boardId+ "?"+ ACCESS_KEY)
                .then()
                    .statusCode(200);
        Assert.assertNull(response.extract().path("id"));
    }

}
