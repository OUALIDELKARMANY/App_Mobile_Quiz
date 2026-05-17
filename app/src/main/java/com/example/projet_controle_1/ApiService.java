package com.example.projet_controle_1;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("add_score")
    Call<Void> addScore(
            @Query("name") String name,
            @Query("score") int score
    );

    @GET("top3")
    Call<List<UserScore>> getTop3();
}
