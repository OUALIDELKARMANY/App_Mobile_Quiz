package com.example.projet_controle_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_profile extends AppCompatActivity {

    CircleImageView imgProfile;
    EditText etName, etEmail;
    TextView tvScore;
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
        tvScore = findViewById(R.id.tvScore);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        btnCommencer = findViewById(R.id.btnCommencer);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // CHARGER LES DONNÉES FIRESTORE
        db.collection("users").document(uid)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        etName.setText(value.getString("name"));
                        etEmail.setText(value.getString("email"));

                        // Récupérer et afficher le score
                        Long score = value.getLong("score");
                        if (score != null) {
                            tvScore.setText("Mon Score : " + score + " / 5");
                        }

                        // Utiliser 'imageUrl' (URL Cloudinary) au lieu de 'imageUri' (local)
                        String imgUrl = value.getString("imageUrl");

                        if (imgUrl != null && !imgUrl.isEmpty()) {
                            // Utiliser Glide pour charger l'image depuis l'URL
                            Glide.with(this)
                                    .load(imgUrl)
                                    .placeholder(R.drawable.img_1)
                                    .into(imgProfile);
                        }
                    }
                });

        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        btnCommencer.setOnClickListener(v -> {
            Intent i = new Intent(activity_profile.this, Qst_1.class);
            startActivity(i);
        });

        btnSave.setOnClickListener(v -> {
            Map<String, Object> update = new HashMap<>();
            update.put("name", etName.getText().toString());

            db.collection("users").document(uid)
                    .update(update)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }
}
