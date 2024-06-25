package apachehttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.pojo.Board;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TrelloApacheTest {
    Properties prop = new Properties();
    String ACCESS_KEY;
    String API_KEY;
    String TOKEN;
    String baseURI= "https://api.trello.com/1/boards/";
    String boardId;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String newBoardId) {
        if (boardId==null){
            this.boardId = newBoardId;
        }
    }

    @BeforeTest
    public void setUp() throws IOException{
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input  = loader.getResourceAsStream("api.properties");
        prop.load(input);
        API_KEY = prop.get("key").toString();
        TOKEN = prop.get("token").toString();
        ACCESS_KEY = "key=" + API_KEY + "&token=" + TOKEN;
    }

    @Test
    public void createBoard(){
        String request = baseURI+"?name=ApacheBoard&"+ACCESS_KEY;
        HttpPost postNewBoard = new HttpPost(request);
        postNewBoard.setHeader("Content-type","application/json");
        try(CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(postNewBoard)){
            int statusCode = response.getCode();
            Assert.assertEquals(statusCode,200);
            ObjectMapper objectMapper = new ObjectMapper();
            Board board = objectMapper.readValue(response.getEntity().getContent(), Board.class);
            setBoardId(board.getId());
            System.out.println("New board created. ID# "+ boardId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getBoard(){
        String request = baseURI + boardId + "/?" + ACCESS_KEY;
        HttpGet getSpecificBoard = new HttpGet(request);
        getSpecificBoard.setHeader("Content-type","application/json");
        try(CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(getSpecificBoard)){
            int statusCode =response.getCode();
            Assert.assertEquals(statusCode,200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateBoard(){
        System.out.println("The board with the following board has been updated: " + boardId);
        String queryParams ="name=ListofRollbackProducts&desc=DescribeslistofclothingitemstobeplacedinRollbacksections";
        String request = baseURI + boardId + "/?" + queryParams + "&" +ACCESS_KEY;
        HttpPut putUpdatedBoard = new HttpPut(request);
        //putUpdatedBoard.setHeader("Accept","application/json");
        //putUpdatedBoard.setHeader("Content-type","application/json");
        try(CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(putUpdatedBoard)){
            int statusCode = response.getCode();
            Assert.assertEquals(statusCode,200);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Test (dependsOnMethods = {"createBoard"})
    public void deleteBoard(){
        String request = baseURI + boardId + "/?" + ACCESS_KEY;
        HttpDelete removeSpecificBoard = new HttpDelete(request);
        removeSpecificBoard.setHeader("Content-type","application/json");
        try(CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(removeSpecificBoard)){
            int statusCode =response.getCode();
            Assert.assertEquals(statusCode,200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

