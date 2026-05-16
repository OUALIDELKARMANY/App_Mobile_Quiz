package com.example.projet_controle_1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class inscreption extends AppCompatActivity {

    EditText etNom, etEmail, etPassword, etConfirmPassword;

    Button etRegister;

    Button btnSelectImage;

    FirebaseAuth auth;

    FirebaseFirestore db;


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
        btnSelectImage = findViewById(R.id.btnSelectImage);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


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



        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });



    }

//    public void sign_up(String email, String password){
//
//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//                    Toast.makeText(inscreption.this, "Stockage avec succes",
//                            Toast.LENGTH_SHORT).show();
//
//
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    finish();
//
//                }else {
//
//                        Toast.makeText(inscreption.this, "Probleme de signup" +
//                                task.getException().getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
//
//    }





    public void sign_up(String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // 🔥 1. récupérer UID utilisateur
                        String uid = auth.getCurrentUser().getUid();

                        // 🔥 2. créer données utilisateur
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", etNom.getText().toString());
                        user.put("email", email);
                        user.put("imageUrl", ""); // profil vide au début
                        user.put("score", 0);

                        // 🔥 3. stocker dans Firestore
                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(inscreption.this,
                                            "Compte + profil créé avec succès",
                                            Toast.LENGTH_SHORT).show();

                                    // 🔥 redirection
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(inscreption.this,
                                            "Erreur Firestore: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });

                    } else {

                        Toast.makeText(inscreption.this,
                                "Probleme de signup: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            CircleImageView imgProfile = findViewById(R.id.imgProfile);
            imgProfile.setImageURI(imageUri);
        }
    }




}

