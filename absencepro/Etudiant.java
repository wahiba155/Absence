package com.example.absencepro;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

;import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Etudiant extends AppCompatActivity {


    private ImageView profil;
    private ImageView notif;
    private TextView box1;
    private TextView count;
    SharedPreferences sharedPreferences;
    private DatabaseReference absenceRef;


    private DatabaseReference notificationRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        profil = findViewById(R.id.photo1);
        box1 = findViewById(R.id.box1);
        notif = findViewById(R.id.photo2);
        box1 = findViewById(R.id.box1);
        count = findViewById(R.id.notif);
        notificationRef = FirebaseDatabase.getInstance().getReference("notification");
        absenceRef = FirebaseDatabase.getInstance().getReference("Absence");

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        String nomp = sharedPreferences.getString("nom", "");
        String prenomp = sharedPreferences.getString("prenom", "");
        String nomComplet = prenomp + " " + nomp;

        notificationRef.orderByChild("nom").equalTo(nomComplet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int notificationCount = (int) dataSnapshot.getChildrenCount();
                count.setText(String.valueOf(notificationCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs de lecture depuis la base de données Firebase, si nécessaire
            }
        });
        absenceRef.orderByChild("nomEtud").equalTo(nomComplet).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> absencesCountMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Absence absence = snapshot.getValue(Absence.class);
                    if (absence != null) {
                        String matiere = absence.getMatiere();

                        // Incrémenter le compteur d'absences pour l'étudiant
                        int absencesCount = absencesCountMap.getOrDefault(matiere, 0);
                        absencesCount++;
                        absencesCountMap.put(matiere, absencesCount);

                        if (absencesCount > 3) {
                            // L'étudiant a dépassé 3 absences, ajouter une notification
                            String notification = "Vous avez dépassé 3 absences dans la matière de " + matiere;
                            Notification newNotification = new Notification(nomComplet, notification);

                            // Vérifier si une notification identique existe déjà dans la base de données
                            notificationRef.orderByChild("nom").equalTo(nomComplet).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean isNotificationExists = false;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Notification existingNotification = snapshot.getValue(Notification.class);
                                        if (existingNotification != null && existingNotification.getMessage().equals(notification)) {
                                            isNotificationExists = true;
                                            break;
                                        }
                                    }

                                    if (!isNotificationExists) {
                                        notificationRef.push().setValue(newNotification);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Gérer les erreurs
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs
            }
        });


        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Etudiant.this, NotifEtud.class);
                startActivity(intent);
            }
        });


        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Etudiant.this, Profile.class);
                startActivity(intent);
            }
        });
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Etudiant.this, AbsenceEtud.class);
                startActivity(intent);
            }
        });
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
        Intent intent = new Intent(Etudiant.this, Connextion.class);
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
            Intent intent = new Intent(Etudiant.this, ModifierPasse.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

}