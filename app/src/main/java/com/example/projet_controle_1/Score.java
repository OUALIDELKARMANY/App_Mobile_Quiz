package com.example.projet_controle_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Score extends AppCompatActivity {

    TextView score_aff;
    ProgressBar bar;
    Button btn_Try, btn_LogOut, btnProfile, btnClassement;

    int score;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bar = findViewById(R.id.progressBar2);
        score_aff = findViewById(R.id.Valeur_score);
        btn_Try = findViewById(R.id.button8);
        btn_LogOut = findViewById(R.id.button9);
        btnClassement = findViewById(R.id.buttonClassement); // Assurez-vous d'avoir ce bouton dans votre XML

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            Intent i = new Intent(Score.this, activity_profile.class);
            startActivity(i);
        });

        if (btnClassement != null) {
            btnClassement.setOnClickListener(v -> {
                Intent i = new Intent(Score.this, Classement.class);
                startActivity(i);
            });
        }

        Intent i1 = getIntent();
        score = i1.getIntExtra("score", 0);
        score_aff.setText(100 * score / 5 + "%");
        bar.setProgress(100 * score / 5);

        // 1. Sauvegarder dans Firestore
        updateScoreInFirestore(score);

        // 2. Envoyer à l'API Retrofit
        sendScoreToApi(score);

        btn_LogOut.setOnClickListener(v -> {
            Toast.makeText(Score.this, "Merci pour votre participation", Toast.LENGTH_SHORT).show();
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        btn_Try.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Qst_1.class));
            overridePendingTransition(R.anim.sortie, R.anim.entre);
            finish();
        });
    }

    private void updateScoreInFirestore(int scoreValue) {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("score", scoreValue);
            db.collection("users").document(uid)
                    .set(data, com.google.firebase.firestore.SetOptions.merge());
        }
    }

    private void sendScoreToApi(int scoreValue) {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            // On récupère le nom depuis Firestore pour l'envoyer à l'API
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    if (name == null) name = "Joueur Inconnu";

                    RetrofitClient.getApiService().addScore(name, scoreValue).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Score ajouté à l'API avec succès
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Erreur API
                        }
                    });
                }
            });
        }
    }
}
