package retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pojo.Board;
import org.pojo.TrelloHttpRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TrelloRetrofitTest {
    Properties prop = new Properties();
    String ACCESS_KEY;
    String API_KEY;
    String TOKEN;
    String baseURI= "https://api.trello.com";
    Board board;
    String boardId;
    Retrofit retrofitObj;
    TrelloHttpRequest httpRequest;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String newBoardId) {
        if (boardId==null){
            this.boardId = newBoardId;
        }
    }

    public Retrofit getRetrofitObj() {
        return retrofitObj;
    }

    public void setRetrofitObj(Retrofit retrofitObj) {
        this.retrofitObj = retrofitObj;
    }

    @BeforeTest
    public void setUp() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input  = loader.getResourceAsStream("api.properties");
        prop.load(input);
        API_KEY = prop.get("key").toString();
        TOKEN = prop.get("token").toString();
        ACCESS_KEY = "key=" + API_KEY + "&token=" + TOKEN;

        retrofitObj = new Retrofit.Builder()
                .baseUrl(baseURI)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        setRetrofitObj(retrofitObj);
        httpRequest = retrofitObj.create(TrelloHttpRequest.class);
    }

    @Test
    public void verifyConnection(){
        Response<Board> response;
        {
            try {
                response = httpRequest.getConnection(API_KEY,TOKEN).execute();
                int status = response.code();
                Assert.assertEquals(status,200,"Account info not retrieved");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test (dependsOnMethods = {"verifyConnection"})
    public void createBoard(){
        String name = "RetroFit Board 2.0";
        String color = "red";
        try {
            Response <Board> response = httpRequest.createBoard(name,color, API_KEY,TOKEN).execute();
            int status = response.code();
            Assert.assertEquals(status,200,"Board not created");

            board = response.body();
            Assert.assertNotNull(board);
            System.out.println("Board Id is: "+ board.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test (dependsOnMethods = {"createBoard"})
    public void getBoard(){
        try {
            Response <Board> response = httpRequest.getBoard(board.getId(),API_KEY,TOKEN).execute();
            int status = response.code();
            Assert.assertEquals(status,200,"Board not found");
            board = response.body();

            Assert.assertNotNull(response.body());
            System.out.println("Board found: "+ response.body().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test (dependsOnMethods = {"getBoard"})
    public void updateBoard(){
        try{
            String name= "Shopping List";
            String desc= "June shopping list";
            String color= "lime";
            Response <Board> response = httpRequest.updateBoard(board.getId(),name, desc, color, API_KEY, TOKEN).execute();
            int status= response.code();
            Assert.assertEquals(status,200,"Board not updated");

            Assert.assertNotNull(response.body());
            System.out.println("Board has been updated "+ response.body().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test (dependsOnMethods = {"updateBoard"})
    public void deleteBoard(){
        try{
            Response <Board> response = httpRequest.getBoard(board.getId(),API_KEY,TOKEN).execute();
            int status = response.code();
            Assert.assertEquals(status,200,"Board not deleted");
            board = response.body();

            Assert.assertNotNull(response.body());
            System.out.println("Board has been deleted: ID# "+ response.body().getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
