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

public class Score extends AppCompatActivity {

    TextView score_aff;
    ProgressBar bar;
    Button btn_Try, btn_LogOut, btnProfile;

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
            //----
        });

        bar = findViewById(R.id.progressBar2);
        score_aff = findViewById(R.id.Valeur_score);
        btn_Try = findViewById(R.id.button8);
        btn_LogOut = findViewById(R.id.button9);

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Score.this, activity_profile.class);
                startActivity(i);
            }
        });

        Intent i1 = getIntent();
        score = i1.getIntExtra("score", 0);
        score_aff.setText(100*score/5 + "%");
        bar.setProgress(100*score/5);

        btn_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Score.this, "Merci pour votre participation",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
        );


        btn_Try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Qst_1.class));

                overridePendingTransition(R.anim.sortie,R.anim.entre);

                finish();
            }
        });



    }
}