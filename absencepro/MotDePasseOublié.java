package com.example.absencepro;

import android.app.Activity;
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

public class MotDePasseOublié extends AppCompatActivity {

    private Button suivant;
    private EditText email;
    String userEmail;
    private ImageView imageretour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        suivant = findViewById(R.id.suiv);
        email = findViewById(R.id.editTextTextEmailAddress);

        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MotDePasseOublié.this, Connextion.class);
                startActivity(intent);
            }
        });

        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validerEmail()) {
                    return;
                } else {
                    userEmail = email.getText().toString().trim();
                    DatabaseReference referenceUti = FirebaseDatabase.getInstance().getReference("utilisateur");

                    changePassword(referenceUti, userEmail);
                }
                return;
            }
        });
    }

    public Boolean validerEmail() {
        String val = email.getText().toString();
        if (val.isEmpty()) {
            email.setError("Veuillez saisir votre email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    public void changePassword(DatabaseReference reference, final String userUsername) {
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(MotDePasseOublié.this, ChangerMotPasse.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MotDePasseOublié.this, "Utilisateur introuveble", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MotDePasseOublié.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
            }
        });
    }
}