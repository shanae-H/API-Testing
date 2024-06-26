package okHttp;

import okhttp3.*;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class TrelloOkHttpTest {
    Properties prop = new Properties();
    String ACCESS_KEY;
    String API_KEY;
    String TOKEN;
    String baseURI= "https://api.trello.com/1/boards/";
    String boardId;
    private OkHttpClient client = new OkHttpClient();

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String newBoardId) {
        if (boardId==null){
            this.boardId = newBoardId;
        }
    }

    @BeforeTest
    public void setUp() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input  = loader.getResourceAsStream("api.properties");
        prop.load(input);
        API_KEY = prop.get("key").toString();
        TOKEN = prop.get("token").toString();
        ACCESS_KEY = "key=" + API_KEY + "&token=" + TOKEN;
    }

    @Test
    public void verifyConnection(){
        try {

            Request request = new Request.Builder()
                    .url("https://api.trello.com/1/members/me?"+ACCESS_KEY)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                int status =response.code();
                AssertJUnit.assertEquals(200,status);
                Assert.assertNotNull(response.body());
                System.out.println("Verify connection method resulted in: "+ response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createBoards(){
        //Assert.assertNotNull(baseURI);
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.trello.com")
                .addPathSegment("/1/boards")
                .addQueryParameter("name","New Board!!!")
                .addQueryParameter("prefs_background","lime")
                .addQueryParameter("key",API_KEY)
                .addQueryParameter("token", TOKEN)
                .build();

        System.out.println("Verify created uri:\n"+httpUrl.toString());
        Request request = new Request.Builder()
                .url(httpUrl)
                .addHeader("Content-type","application/json")
                .addHeader("Accept","application/json")
                .build();

        try (Response response = client.newCall(request).execute()){
            int status =response.code();
            AssertJUnit.assertEquals(200,status);
            Assert.assertNotNull(response.body());
            System.out.println("Verify createBoard method resulted in: "+ response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void createBoard(){
        RequestBody requestBody = new FormBody.Builder()
                .add("name","New Board!!!")
                .add("prefs_background","lime")
                .add("key",API_KEY)
                .add("token",TOKEN)
                .build();

        Request postRequest = new Request.Builder()
                .url(baseURI)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(postRequest).execute()) {
            int status = response.code();
            Assert.assertEquals(status,200);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
