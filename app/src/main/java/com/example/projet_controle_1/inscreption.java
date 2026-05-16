package com.example.projet_controle_1;

import android.content.Intent;
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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class inscreption extends AppCompatActivity {

    EditText etNom, etEmail, etPassword, etConfirmPassword;
    Button etRegister;
    Button btnSelectImage;
    CircleImageView imgProfile;

    FirebaseAuth auth;
    FirebaseFirestore db;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inscreption);
        FirebaseApp.initializeApp(getApplicationContext());

        // Initialisation Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "due6gruub");
        config.put("api_key", "373274545413971");
        config.put("api_secret", "OXEXSh5O9-eMGjfLPPeEW_YGnd8");
        try {
            MediaManager.init(this, config);
        } catch (IllegalStateException e) {
            // Déjà initialisé
        }

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
        imgProfile = findViewById(R.id.imgProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String motPass = etPassword.getText().toString().trim();
                String ConfirmPass = etConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(motPass) || TextUtils.isEmpty(ConfirmPass)) {
                    Toast.makeText(inscreption.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (motPass.length() < 6) {
                    Toast.makeText(inscreption.this, "Le mot de passe doit faire au moins 6 caractères", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ConfirmPass.equals(motPass)) {
                    Toast.makeText(inscreption.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImageAndSignUp(email, motPass);
            }
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });
    }

    private void uploadImageAndSignUp(String email, String password) {
        if (selectedImageUri == null) {
            sign_up(email, password, "");
            return;
        }

        Toast.makeText(this, "Téléchargement de l'image...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(selectedImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        sign_up(email, password, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(inscreption.this, "Erreur upload: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    public void sign_up(String email, String password, String imageUrl) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", etNom.getText().toString());
                        user.put("email", email);
                        user.put("imageUrl", imageUrl);
                        user.put("score", 0);

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(inscreption.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(inscreption.this, "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(inscreption.this, "Erreur d'inscription: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}
