package com.example.absencepro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChangerMotPasse extends AppCompatActivity {

    private Button enregistrer;
    private EditText pass, Cpass;
    private ImageView imageretour;


    private String userPass;
    private String userPassC;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangerMotPasse.this, MotDePasseOublié.class);
                startActivity(intent);
            }
        });

        enregistrer = findViewById(R.id.enreg);
        pass = findViewById(R.id.editTextPassword);
        Cpass = findViewById(R.id.editTextPasswordC);

        enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validerPass1() || !validerPass2() || !egale()) {
                    return;
                } else {
                    userPass = pass.getText().toString().trim();
                    userPassC = Cpass.getText().toString().trim();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("utilisateur");

                    if (userPass.equals(userPassC)) {
                        if (PassValid(userPass)) {
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Parcourez les enfants de la référence
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        // Obtenez l'ID de l'enfant
                                        String collectionId = childSnapshot.getKey();

                                        // Utilisez cet ID pour mettre à jour le mot de passe
                                        reference.child(collectionId).child("mdp").setValue(userPass);
                                        Toast.makeText(ChangerMotPasse.this, "Mot de passe est changé avec succé ", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(ChangerMotPasse.this, Connextion.class);
                                        startActivity(intent);
                                        // Sortez de la boucle après avoir mis à jour le mot de passe pour le premier ID trouvé
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChangerMotPasse.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
                return;
            }
        });
    }

    public Boolean validerPass1() {
        String val = pass.getText().toString();
        if (val.isEmpty()) {
            pass.setError("Veuillez saisir un nouveau mot de passe");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    public Boolean validerPass2() {
        String val = Cpass.getText().toString();
        if (val.isEmpty()) {
            Cpass.setError("Veuillez saisir une confirmation de mot de passe");
            return false;
        } else {
            Cpass.setError(null);
            return true;
        }
    }

    public Boolean egale() {
        String val1 = pass.getText().toString();
        String val2 = Cpass.getText().toString();

        if (!val1.equals(val2)) {
            Cpass.setError("Les mots de passe ne correspondent pas !");
            return false;
        } else {
            Cpass.setError(null);
            return true;
        }
    }

    public boolean PassValid(String password) {
        // Ajoutez ici votre logique de validation du mot de passe
        return password.length() >= 6;
    }
}