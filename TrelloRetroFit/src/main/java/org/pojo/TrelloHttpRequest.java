package org.pojo;

import retrofit2.Call;
import retrofit2.http.*;

public interface TrelloHttpRequest {
    @GET("1/members/me?")
    public Call <Board> getConnection(@Query("key")String apikey, @Query("token") String token);

    @POST("/1/boards/")
    public Call <Board> createBoard(@Query("name")String name, @Query("prefs_background")String bgColor, @Query("key")String apikey, @Query("token")String token);

    @GET("/1/boards/{id}/")
    public Call <Board> getBoard(@Path("id")String id, @Query("key")String apikey, @Query("token")String token);

    @PUT("1/boards/{id}/")
    public Call <Board> updateBoard(@Path("id")String id, @Query("name")String name, @Query("desc")String desc, @Query("prefs/background")String color,@Query("key")String apikey, @Query("token")String token);

    @DELETE("1/boards/{id}/")
    public Call <Board> deleteBoard (@Path("id")String id, @Query("key")String apikey, @Query("token")String token);
}
