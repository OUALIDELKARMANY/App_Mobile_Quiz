package com.example.projet_controle_1;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;
import java.util.Map;

public class activity_profile extends AppCompatActivity {



    CircleImageView imgProfile;
    EditText etName, etEmail;
    Button btnChangeImage, btnSave, btnCommencer;

    FirebaseFirestore db;
    FirebaseAuth auth;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        btnCommencer = findViewById(R.id.btnCommencer);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();



        //---------------------------------------------------------------
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //---------------------------------------------------------------




        //-------------------------- CHARGER LES DONNÉES FIRESTORE -----------------
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        etName.setText(value.getString("name"));
                        etEmail.setText(value.getString("email"));

                        String img = value.getString("imageUri");

                        if (img != null && !img.isEmpty()) {
                            imgProfile.setImageURI(Uri.parse(img));
                        }
                    }
                });



        //----------------------------- CHANGER IMAGE ------------------------
        btnChangeImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);

        });



        btnCommencer.setOnClickListener(v -> {

            Intent i = new Intent(activity_profile.this, Qst_1.class);
            startActivity(i);

        });








        //------------------------------ SAUVEGARDER MODIFICATIONS ----------------------------

        btnSave.setOnClickListener(v -> {

//            String uid = auth.getCurrentUser().getUid();

            Map<String, Object> update = new HashMap<>();
            update.put("name", etName.getText().toString());
            update.put("email", etEmail.getText().toString());


            if (imageUri != null) {
                update.put("imageUri", imageUri.toString());
            }

            db.collection("users").document(uid)
                    .update(update)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show()
                    );
        });

    }




    //-------------------------------------- RÉCUPÉRER IMAGE ------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }
}