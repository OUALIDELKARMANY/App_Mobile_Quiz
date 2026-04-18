package com.example.projet_controle_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Qst_2 extends AppCompatActivity {

    RadioGroup rg;

    RadioButton rb;

    Button btn;

    String reponse = "Lionel Messi";

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qst2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        rg = findViewById(R.id.radioGrp);
        btn = findViewById(R.id.button3);

        Intent i1 = getIntent();
        score = i1.getIntExtra("score", 0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getCheckedRadioButtonId() == -1) {

                    Toast.makeText(Qst_2.this, "Merci de choisir un champs",
                            Toast.LENGTH_SHORT).show();

                } else {

                    rb = findViewById(rg.getCheckedRadioButtonId());
                    if (rb.getText().toString().equals(reponse)) {
                        score += 1;
                    }

                    Intent i1 = new Intent(getApplicationContext(), Qst_3.class);
                    i1.putExtra("score", score);
                    startActivity(i1);

                    overridePendingTransition(R.anim.sortie,R.anim.entre);

                    finish();

                }


            }
        });
    }}