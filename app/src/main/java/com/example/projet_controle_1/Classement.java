package com.example.projet_controle_1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Classement extends AppCompatActivity {

    TextView tvFirst, tvSecond, tvThird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        tvFirst = findViewById(R.id.tvFirst);
        tvSecond = findViewById(R.id.tvSecond);
        tvThird = findViewById(R.id.tvThird);

        loadTop3();
    }

    private void loadTop3() {
        // On utilise UserScore (le POJO) et non Score (l'Activity)
        RetrofitClient.getApiService().getTop3()
                .enqueue(new Callback<List<UserScore>>() {

                    @Override
                    public void onResponse(Call<List<UserScore>> call, Response<List<UserScore>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<UserScore> list = response.body();

                            if (list.size() > 0) {
                                tvFirst.setText("🥇 1. " + list.get(0).getName() + " - " + list.get(0).getScore() + " pts");
                            }

                            if (list.size() > 1) {
                                tvSecond.setText("🥈 2. " + list.get(1).getName() + " - " + list.get(1).getScore() + " pts");
                            }

                            if (list.size() > 2) {
                                tvThird.setText("🥉 3. " + list.get(2).getName() + " - " + list.get(2).getScore() + " pts");
                            }
                        } else {
                            Toast.makeText(Classement.this, "Erreur serveur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserScore>> call, Throwable t) {
                        tvFirst.setText("Erreur : " + t.getMessage());
                        Toast.makeText(Classement.this, "Impossible de contacter l'API", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
