package com.example.projet_controle_1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class inscreption extends AppCompatActivity {

    EditText etNom, etEmail, etPassword, etConfirmPassword;

    Button etRegister;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inscreption);
        FirebaseApp.initializeApp(getApplicationContext());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        etNom = findViewById(R.id.Nom);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.Confirmpassword);
        etRegister = findViewById(R.id.button3);

        auth = FirebaseAuth.getInstance();


        etRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String motPass = etPassword.getText().toString();
                String ConfirmPass = etConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(inscreption.this, "Vous devez remplir les champs",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(motPass)){
                    Toast.makeText(inscreption.this, "Vous devez remplir les champs",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(ConfirmPass)){
                    Toast.makeText(inscreption.this, "Vous devez remplir les champs",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(inscreption.this, "Vous devez remplir les champs",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(motPass.length() < 6){
                    Toast.makeText(inscreption.this, "Votre mot de passe doit contenir plus que 6 caractereres",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!ConfirmPass.equals(motPass)){
                    Toast.makeText(inscreption.this, "Vous entrez le meme mot de passe ",
                            Toast.LENGTH_SHORT).show();
                    return;
                }




                sign_up(email, motPass);




            }
        });



    }

    public void sign_up(String email, String password){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(inscreption.this, "Stockage avec succes",
                            Toast.LENGTH_SHORT).show();


                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }else {

                        Toast.makeText(inscreption.this, "Probleme de signup" +
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}