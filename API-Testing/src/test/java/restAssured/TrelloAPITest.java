package restAssured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.example.Board;
import org.testng.Assert;
import org.testng.ITestContext;
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
    String TRELLO = "https://api.trello.com/1/boards/6670a17ed711030864d3fbc1/?";


    public static String getBoardId() {
        return boardId;
    }

    public static void setBoardId(String boardId) {
        TrelloAPITest.boardId = boardId;
    }

    static String boardId;



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
                .body("name",equalTo("Brand New Appliances12345"));
        TrelloAPITest.setBoardId(response.extract().jsonPath().getString("id"));

//        context.setAttribute("id",response.extract().path("id"));
//        setBoardId(response.extract().path("id"));
//        id=response.extract().path("id");
        System.out.println("New board created: "+TrelloAPITest.boardId);
    }

    @Test(dependsOnMethods = {"createBoard"})
    void getBoard() {
        System.out.println(getBoardId());
        ResponseBody responseBody = given()
                //.header("Content-Type","application/json; charset-utf-8")
                //.contentType(ContentType.JSON)
                .when()
                .get( TrelloAPITest.boardId +"/?" + ACCESS_KEY).getBody();
        responseBody.as(Board.class);
        System.out.println(responseBody.prettyPrint());
        Assert.assertTrue(responseBody.prettyPrint().contains((getBoardId())));
    }

    @Test(dependsOnMethods = {"createBoard"})
    void updateBoard(){
        //boardId = context.getAttribute("id");
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

    @Test (dependsOnMethods = {"createBoard"})
    void deleteBoard(){
        System.out.println(boardId);
        ValidatableResponse response =given()
                .contentType("application/json")
                .when()
                .delete(TrelloAPITest.boardId+ "?"+ ACCESS_KEY)
                .then()
                .statusCode(200);
        Assert.assertNull(response.extract().path("id"));
    }




}
