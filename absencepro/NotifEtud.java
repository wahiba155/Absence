package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifEtud extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private boolean isReferencesInserted = false;

    private DatabaseReference absenceRef;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private DatabaseReference notificationRef;

    String nomComplet;
    private ImageView imageretour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifetud);
        imageretour = findViewById(R.id.retour);


        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        String role = sharedPreferences.getString("role", "");

        if (role.equals("Professeur")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NotifEtud.this, Prof.class);
                    startActivity(intent);
                }
            });
        } else if (role.equals("Admin")) {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NotifEtud.this, Admin.class);
                    startActivity(intent);
                }
            });
        } else {
            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NotifEtud.this, Etudiant.class);
                    startActivity(intent);
                }
            });
        }

        String nom = sharedPreferences.getString("nom", "");
        String prenom = sharedPreferences.getString("prenom", "");
        nomComplet = prenom + " " + nom;

        // Obtenir une référence à la base de données Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        notificationRef = firebaseDatabase.getReference("notification"); // Référence à la base de données "notification"
        notificationList = new ArrayList<>(); // Initialisation de la liste des notifications

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        notificationRef.orderByChild("nom").equalTo(nomComplet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear(); // Vider la liste des notifications avant de la remplir à nouveau
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                notificationAdapter.notifyDataSetChanged(); // Notifier l'adaptateur du changement de données
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs si nécessaire
            }
        });



    }
}