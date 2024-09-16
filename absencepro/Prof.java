package com.example.absencepro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prof extends AppCompatActivity {
    private ImageView profil;
    private  ImageView notif;
    private TextView box1;
    private TextView box2;
    private TextView box3;
    String subjectLabel;

    private TextView count;
    SharedPreferences sharedPreferences;
    private DatabaseReference matiereRef;

    private DatabaseReference absenceRef;


    private DatabaseReference notificationRef;
    private boolean isReferencesInserted = false;
    private TextView countTextView;


    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    String nomCompletProf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        profil = findViewById(R.id.photo1);
        notif = findViewById(R.id.photo2);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        count = findViewById(R.id.notif);
        box3 = findViewById(R.id.box3);
        count = findViewById(R.id.notif);

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);


        String nomp = sharedPreferences.getString("nom", "");
        String prenomp = sharedPreferences.getString("prenom", "");
         nomCompletProf = prenomp + " " + nomp;
// Obtenir une référence à la base de données Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        absenceRef = firebaseDatabase.getReference("Absence");
        matiereRef = firebaseDatabase.getReference("matiére");
        notificationRef = firebaseDatabase.getReference("notification"); // Référence à la base de données "notification"


        // Récupérer la matière
        matiereRef.orderByChild("nomProf").equalTo(nomCompletProf).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if there is a matching subject for the professor
                if (dataSnapshot.exists()) {
                    // Retrieve the first matching subject
                    DataSnapshot subjectSnapshot = dataSnapshot.getChildren().iterator().next();
                    // Retrieve the label of the subject
                    subjectLabel = subjectSnapshot.child("libellé").getValue(String.class);
                    checkAbsences();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur while retrieving the subject
            }
        });
        notificationRef.orderByChild("nom").equalTo(nomCompletProf).addValueEventListener(new ValueEventListener() {
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

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Prof.this, NotifEtud.class);
                startActivity(intent);
            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Prof.this, Profile.class);
                startActivity(intent);
            }
        });

        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Prof.this, ListeEtudiant.class);
                startActivity(intent);
            }
        });
        box2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Prof.this, ListeAbsences.class);
                startActivity(intent);
            }
        });
        box3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Prof.this, ListeJustif.class);
                startActivity(intent);
            }
        });
    }


    private void checkAbsences() {
        absenceRef.orderByChild("matiere").equalTo(subjectLabel).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> absencesCountMap = new HashMap<>();
                Map<String, String> notificationsMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Absence absence = snapshot.getValue(Absence.class);
                    if (absence != null) {
                        String nomEtudiant = absence.getNomEtud();

                        // Incrémenter le compteur d'absences pour l'étudiant
                        int absencesCount = absencesCountMap.getOrDefault(nomEtudiant, 0);
                        absencesCount++;
                        absencesCountMap.put(nomEtudiant, absencesCount);

                        if (absencesCount > 3) {
                            // L'étudiant a dépassé 3 absences, ajouter une notification
                            String notification = "L'étudiant(e) " + nomEtudiant + " a dépassé 3 absences.";
                            Notification newNotification = new Notification(nomCompletProf, notification);

                            // Vérifier si une notification identique existe déjà dans la base de données
                            notificationRef.orderByChild("nom").equalTo(nomCompletProf).addListenerForSingleValueEvent(new ValueEventListener() {
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
        Intent intent = new Intent(Prof.this, Connextion.class);
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
            Intent intent = new Intent(Prof.this, ModifierPasse.class);
            startActivity(intent);
            return true;
        }

        return false;
    }
    }




