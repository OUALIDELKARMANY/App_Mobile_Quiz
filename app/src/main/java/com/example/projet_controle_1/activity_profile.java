package com.example.projet_controle_1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_profile extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    CircleImageView imgProfile;
    EditText etName, etEmail;
    TextView tvScore, tvLocation;
    Button btnChangeImage, btnSave, btnCommencer, btnClassement, btnGPS;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FusedLocationProviderClient fusedLocationClient;

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
        tvLocation = findViewById(R.id.tvLocation);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        btnCommencer = findViewById(R.id.btnCommencer);
        btnClassement = findViewById(R.id.btnClassement);
        btnGPS = findViewById(R.id.btnGPS);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

                        Long score = value.getLong("score");
                        if (score != null) {
                            tvScore.setText("Mon Score : " + score + " / 5");
                        }

                        String imgUrl = value.getString("imageUrl");
                        if (imgUrl != null && !imgUrl.isEmpty()) {
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

        btnClassement.setOnClickListener(v -> {
            Intent i = new Intent(activity_profile.this, Classement.class);
            startActivity(i);
        });

        btnSave.setOnClickListener(v -> {
            Map<String, Object> update = new HashMap<>();
            update.put("name", etName.getText().toString());

            db.collection("users").document(uid)
                    .update(update)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(activity_profile.this, "Profil mis à jour", Toast.LENGTH_SHORT).show()
                    );
        });

        btnGPS.setOnClickListener(v -> getLastLocation());
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED 
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(activity_profile.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String cityName = addresses.get(0).getLocality();
                                String countryName = addresses.get(0).getCountryName();
                                
                                if (cityName == null) {
                                    cityName = addresses.get(0).getAdminArea(); // Fallback vers la région
                                }
                                
                                String displayLocation = "Position : " + (cityName != null ? cityName : "Lieu inconnu") + ", " + countryName;
                                tvLocation.setText(displayLocation);
                            } else {
                                tvLocation.setText("Position : Coordonnées trouvées (Ville inconnue)");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            tvLocation.setText("Position : Lat " + location.getLatitude() + ", Lon " + location.getLongitude());
                        }
                    } else {
                        Toast.makeText(activity_profile.this, "Position introuvable. Activez le GPS.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }
}