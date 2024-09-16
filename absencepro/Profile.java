package com.example.absencepro;

import static android.content.ContentValues.TAG;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class Profile extends AppCompatActivity {
    private TextView textViewNom;
    private TextView textViewPrenom;
    private TextView textViewEmail;

    private ImageView imageViewProfil;

    private ImageView imageretour;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    SharedPreferences sharedPreferences;

    String collectionName;
    String userId;
    DatabaseReference reference;
    ImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        textViewNom = findViewById(R.id.textViewNom);
        textViewPrenom = findViewById(R.id.textViewPrenom);
        imageViewProfil = findViewById(R.id.profile);
        textViewEmail = findViewById(R.id.textViewEmail);
        imageretour = findViewById(R.id.retour);
        ImageButton Editer = findViewById(R.id.edit);


        Editer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStoragePermission();

            }
        });


        // Initialisation des SharedPreferences et de DatabaseReference
        sharedPreferences = getSharedPreferences("collection", MODE_PRIVATE);

        collectionName = sharedPreferences.getString("collectionName", "");
        profile = findViewById(R.id.profile);

        sharedPreferences= getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sessionId", "");
        reference = FirebaseDatabase.getInstance().getReference().child(userId).child(collectionName);


        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profil = dataSnapshot.child("image").getValue(String.class);

                    if (profil != null && !profil.isEmpty()) {
                        Glide.with(Profile.this)
                                .load(profil)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageViewProfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Une erreur s'est produite. Veuillez réessayer plus tard.", Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve user data from SharedPreferences
        String nom = sharedPreferences.getString("nom", "");
        String prenom = sharedPreferences.getString("prenom", "");
        String image = sharedPreferences.getString("image", "");
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role", "");

        textViewNom.setText(nom);
        textViewPrenom.setText(prenom);
        textViewEmail.setText(email);


        // Load and display user image using G lide library
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Optional, to disable caching
                .skipMemoryCache(false); // Optional, to disable caching
        Glide.with(this)
                .load(image)
                .apply(requestOptions)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfil);


        if (role.equals("Professeur")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Profile.this, Prof.class);
                    startActivity(intent);
                }
            });
        } else if (role.equals("Admin")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Profile.this, Admin.class);
                    startActivity(intent);
                }
            });
        } else {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Profile.this, Etudiant.class);
                    startActivity(intent);
                }
            });
        }


    }
    public void Menu(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
        unregisterForContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_profile, menu);
    }
    // Code pour la déconnexion
    private void logout() {
        // Effacez les données de session utilisateur
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirigez vers l'écran de connexion ou l'écran d'accueil avec le drapeau FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK
        Intent intent = new Intent(Profile.this, Connextion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Fermez l'activité actuelle pour empêcher l'utilisateur de revenir en arrière
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        }
        if (item.getItemId() == R.id.menu_change_password) {
            Intent intent = new Intent(Profile.this, ModifierPasse.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            openGallery();

        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                Uri imageUri = data.getData();
                String filePath = getRealPathFromURI(imageUri);
                if (filePath != null) {
                    Glide.with(Profile.this)
                            .load(new File(filePath))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewProfil);
                    uploadImageToDatabase(imageUri);

                    // Mettre à jour les préférences partagées avec l'URL de l'image
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("image", imageUri.toString());
                    editor.apply();
                } else {
                    Toast.makeText(this, "Erreur: Impossible de récupérer le chemin du fichier", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    private void uploadImageToDatabase(Uri imageUri) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child(collectionName).child(userId).child("image").setValue(imageUri.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Mettre à jour les préférences partagées avec la nouvelle URL de l'image
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("image", imageUri.toString());
                            editor.apply();
                            // Mettre à jour l'image de profil
                            Glide.with(Profile.this)
                                    .load(imageUri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewProfil);


                            Log.d(TAG, "Image ajoutée avec succès dans la base de données en temps réel");
                        } else {
                            Log.e(TAG, "Erreur lors de l'ajout de l'image dans la base de données en temps réel", task.getException());
                        }
                    }
                });
    }
}