package restAssured;

import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class TrelloAPITest {
    Properties prop = new Properties();
    String ACCESS_KEY;
    String TRELLO = "https://api.trello.com/1/boards/6670a17ed711030864d3fbc1/?";


    @BeforeTest
    public void beforeAllTests() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("api.properties");
        prop.load(input);
        String API_KEY = prop.get("key").toString();
        String TOKEN = prop.get("token").toString();
        ACCESS_KEY = "token=" + TOKEN + "&key=" + API_KEY;
        RestAssured.baseURI = "https://api.trello.com/";
    }

    @Test
    void getBoard() {
        ResponseBody responseBody = given()
                .when()
                .get("1/boards/6670a17ed711030864d3fbc1/?" + ACCESS_KEY).getBody();
        System.out.println(responseBody.prettyPrint());
        Assert.assertTrue(responseBody.prettyPrint().contains("6670a17ed711030864d3fbc1"));
    }

    @Test
    void createBoard(){

    }


}
