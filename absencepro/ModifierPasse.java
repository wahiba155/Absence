package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ModifierPasse extends AppCompatActivity {

    private Button valider;
    private EditText pass1, pass2;
    private EditText AncienPass;
    private String userPass1;

    private String userPass2;
    private String ancienPass;


    private ImageView imageretour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);

        imageretour = findViewById(R.id.retour);
        valider = findViewById(R.id.valider);
        pass1 = findViewById(R.id.editTextpass);
        pass2 = findViewById(R.id.editTextCpass);
        AncienPass = findViewById(R.id.editTextApass);


        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        String role = sharedPreferences.getString("role", "");


        if (role.equals("Professeur")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ModifierPasse.this, Prof.class);
                    startActivity(intent);
                }
            });
        } else if (role.equals("Admin")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ModifierPasse.this, Admin.class);
                    startActivity(intent);
                }
            });
        } else {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ModifierPasse.this, Etudiant.class);
                    startActivity(intent);
                }
            });
        }


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validerPass1() || !validerPass2() || !egale() || !validerPass() ) {
                    return;
                } else {
                    userPass1 = pass1.getText().toString().trim();
                    userPass2 = pass2.getText().toString().trim();
                    ancienPass = AncienPass.getText().toString().trim();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("utilisateur");
                    changePassword(reference, ancienPass);

                }
            }
        });

    }

    public void changePassword(DatabaseReference reference, final String userUsername) {
        if (userPass1.equals(userPass2)) {
            if (PassValid(userPass1)) {
                Query checkUserDatabase = reference.orderByChild("mdp").equalTo(userUsername);
                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                // Obtenez l'ID de l'enfant
                                String collectionId = childSnapshot.getKey();

                                // Utilisez cet ID pour mettre à jour le mot de passe
                                reference.child(collectionId).child("mdp").setValue(userPass1);
                                Toast.makeText(ModifierPasse.this, "Mot de passe est changé avec succès", Toast.LENGTH_LONG).show();
                                break;
                            }
                        } else {
                            Toast.makeText(ModifierPasse.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gérez l'annulation ici
                        Toast.makeText(ModifierPasse.this, "Annulation de la modification du mot de passe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public Boolean validerPass1() {
        String val = pass1.getText().toString();
        if (val.isEmpty()) {
            pass1.setError("Veuillez saisir un nouveau mot de passe");
            return false;
        } else {
            pass1.setError(null);
            return true;
        }
    }
    public Boolean validerPass() {
        String val = AncienPass.getText().toString();
        if (val.isEmpty()) {
            AncienPass.setError("Veuillez saisir l'ancien mot de passe");
            return false;
        } else {
            AncienPass.setError(null);
            return true;
        }
    }

    public Boolean validerPass2() {
        String val = pass2.getText().toString();
        if (val.isEmpty()) {
            pass2.setError("Veuillez saisir une confirmation de mot de passe");
            return false;
        } else {
            pass2.setError(null);
            return true;
        }
    }

                    public Boolean egale() {
                        String val1 = pass1.getText().toString();
                        String val2 = pass2.getText().toString();

                        if (!val1.equals(val2)) {
                            pass2.setError("Les mots de passe ne correspondent pas !");
                            return false;
                        } else {
                            pass2.setError(null);
                            return true;
                        }
                    }

                    public boolean PassValid(String password) {
                        // Ajoutez ici votre logique de validation du mot de passe
                        return password.length() >= 3;
                    }





}

