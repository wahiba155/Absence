package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Connextion extends AppCompatActivity {
    private TextView Moublie;
    private Button connexion;
    private EditText email, pass;
    String userPassword, userEmail;
    DatabaseReference referenceProf = FirebaseDatabase.getInstance().getReference("professeur");
    DatabaseReference referenceEtud = FirebaseDatabase.getInstance().getReference("etudiant");
    public String SESSION_ID_KEY = "sessionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        connexion = findViewById(R.id.button2);
        email = findViewById(R.id.editTextTextEmailAddress);
        pass = findViewById(R.id.editTextTextPassword);
        Moublie = findViewById(R.id.motoublié);

        Moublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Connextion.this, MotDePasseOublié.class);
                startActivity(intent);
            }
        });

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() || !validatePassword()) {
                    return;
                } else {
                    userEmail = email.getText().toString().trim();
                    userPassword = pass.getText().toString().trim();
                    DatabaseReference referenceUti = FirebaseDatabase.getInstance().getReference("utilisateur");

                    checkConnexion(referenceUti, userEmail);

                }
            }
        });
    }

    public Boolean validateEmail() {
        String val = email.getText().toString();
        if (val.isEmpty()) {
            email.setError("Veuillez saisir votre email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = pass.getText().toString();
        if (val.isEmpty()) {
            pass.setError("Veuillez saisir votre mot de passe");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    public void checkConnexion(DatabaseReference reference, final String userUsername) {
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = childSnapshot.child("mdp").getValue(String.class);
                        String nomFromDB = childSnapshot.child("nom").getValue(String.class);
                        String prenomFromDB = childSnapshot.child("prenom").getValue(String.class);
                        String roleFromDB = childSnapshot.child("role").getValue(String.class);
                        String imageFromDB = childSnapshot.child("image").getValue(String.class);
                        String collectionName = reference.getKey();
                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            // Inside checkConnexion() method, after successful login
                            String userId = snapshot.getChildren().iterator().next().getKey(); // Obtenez l'ID de l'utilisateur correspondant à l'e-mail
                            saveSessionId(userId); // Enregistrez l'ID de session dans SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("sessionId", userId);
                            editor.putString("nom", nomFromDB);
                            editor.putString("prenom", prenomFromDB);
                            editor.putString("image", imageFromDB);
                            editor.putString("email", userUsername);
                            editor.putString("role", roleFromDB);

                            editor.apply();

                            String message = "Bienvenue " + roleFromDB + " " + nomFromDB + " " + prenomFromDB + " dans votre espace";
                            Toast.makeText(Connextion.this, message, Toast.LENGTH_SHORT).show();
                            if (roleFromDB.equals("Professeur")) {
                                Intent intent = new Intent(Connextion.this, Prof.class);
                                startActivity(intent);
                            } else if (roleFromDB.equals("Admin")) {
                                Intent intent = new Intent(Connextion.this, Admin.class);
                                startActivity(intent);
                            } else if (roleFromDB.equals("Etudiant(e)")) {

                                Intent intent = new Intent(Connextion.this, Etudiant.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(Connextion.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // L'utilisateur n'est pas trouvé dans la référence "utilisateur"
                    // Recherche dans la référence "professeur"
                    Query checkProfesseurDatabase = referenceProf.orderByChild("email").equalTo(userUsername);
                    checkProfesseurDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot profSnapshot) {
                            if (profSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : profSnapshot.getChildren()) {
                                    String nomFromDB = childSnapshot.child("nom").getValue(String.class);
                                    String prenomFromDB = childSnapshot.child("prenom").getValue(String.class);
                                    String passwordFromDB = childSnapshot.child("mdp").getValue(String.class);
                                    String roleFromDB = "Professeur";

                                    String imageFromDB = childSnapshot.child("image").getValue(String.class);

                                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {


                                        DatabaseReference newUserRef = reference.push();
                                        newUserRef.child("email").setValue(userUsername);
                                        newUserRef.child("mdp").setValue(userPassword);
                                        newUserRef.child("role").setValue(roleFromDB);
                                        newUserRef.child("image").setValue(imageFromDB);
                                        newUserRef.child("nom").setValue(nomFromDB);
                                        newUserRef.child("prenom").setValue(prenomFromDB);

                                        String userId = profSnapshot.getChildren().iterator().next().getKey(); // Obtenez l'ID de l'utilisateur correspondant à l'e-mail
                                        saveSessionId(userId); // Enregistrez l'ID de session dans SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("sessionId", userId);
                                        editor.putString("nom", nomFromDB);
                                        editor.putString("prenom", prenomFromDB);
                                        editor.putString("image", imageFromDB);
                                        editor.putString("email", userUsername);
                                        editor.putString("role", roleFromDB);
                                        editor.apply();
                                        String message = "Bienvenue " + roleFromDB + " " + nomFromDB + " " + prenomFromDB + " dans votre espace";
                                        Toast.makeText(Connextion.this, message, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Connextion.this, Prof.class);
                                        startActivity(intent);





                                    } else {
                                        Toast.makeText(Connextion.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // L'utilisateur n'est pas trouvé dans la référence "utilisateur" ni dans la référence "professeur"
                                // Recherche dans la référence "etudiant"
                                Query checkEtudiantDatabase = referenceEtud.orderByChild("email").equalTo(userUsername);
                                checkEtudiantDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot etudSnapshot) {
                                        if (etudSnapshot.exists()) {
                                            for (DataSnapshot childSnapshot : etudSnapshot.getChildren()) {
                                                String nomFromDB = childSnapshot.child("nom").getValue(String.class);
                                                String prenomFromDB = childSnapshot.child("prenom").getValue(String.class);
                                                String passwordFromDB = childSnapshot.child("mdp").getValue(String.class);
                                                String roleFromDB = "Etudiant(e)";
                                                String imageFromDB = childSnapshot.child("image").getValue(String.class);

                                                if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                                                    DatabaseReference newUserRef = reference.push();
                                                    newUserRef.child("email").setValue(userUsername);
                                                    newUserRef.child("mdp").setValue(userPassword);
                                                    newUserRef.child("role").setValue(roleFromDB);
                                                    newUserRef.child("image").setValue(imageFromDB);
                                                    newUserRef.child("nom").setValue(nomFromDB);
                                                    newUserRef.child("prenom").setValue(prenomFromDB);


                                                    String userId = etudSnapshot.getChildren().iterator().next().getKey(); // Obtenez l'ID de l'utilisateur correspondant à l'e-mail
                                                    saveSessionId(userId); // Enregistrez l'ID de session dans SharedPreferences
                                                    SharedPreferences sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("sessionId", userId);
                                                    editor.putString("nom", nomFromDB);
                                                    editor.putString("prenom", prenomFromDB);
                                                    editor.putString("image", imageFromDB);
                                                    editor.putString("email", userUsername);
                                                    editor.putString("role", roleFromDB);
                                                    editor.apply();
                                                    String message = "Bienvenue " + roleFromDB + " " + nomFromDB + " " + prenomFromDB + " dans votre espace";
                                                    Toast.makeText(Connextion.this, message, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Connextion.this, Etudiant.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Connextion.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            // L'utilisateur n'est pas trouvé dans les références "utilisateur", "professeur" et "etudiant"
                                            Toast.makeText(Connextion.this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Connextion.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Connextion.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Connextion.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cette méthode enregistre le nom de la collection dans SharedPreferences
    private void saveCollectionName(String collectionName) {
        SharedPreferences sharedPreferences = getSharedPreferences("collection", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("collectionName", collectionName);
        editor.apply();
    }





    // Cette méthode enregistre l'ID de session dans SharedPreferences
    private void saveSessionId(String sessionId) {
        SharedPreferences sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_ID_KEY, sessionId);
        editor.apply();
    }

}
