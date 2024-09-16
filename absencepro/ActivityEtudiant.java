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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityEtudiant extends AppCompatActivity {

    RecyclerView recyclerCricketers;
    private ImageView imageretour;
    private ImageView profil;
    private ImageView home;



    ArrayList<CricketerET> cricketersList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant);

        recyclerCricketers = findViewById(R.id.recycler_etudiant);
        imageretour = findViewById(R.id.retour);
        profil = findViewById(R.id.photo1);
        home = findViewById(R.id.photo3);


        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityEtudiant.this, Profile.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityEtudiant.this, Admin.class);
                startActivity(intent);
            }
        });
        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityEtudiant.this, GererEtud.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerCricketers.setLayoutManager(layoutManager);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("etudiant");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cricketersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Etud etudiant = snapshot.getValue(Etud.class);
                    if (etudiant != null) {
                        CricketerET cricketer = new CricketerET(etudiant.getPrenom(), etudiant.getNom(), etudiant.getNapogé(), etudiant.getSexe());
                        cricketersList.add(cricketer);
                    }
                }

                // Une fois que toutes les données sont récupérées, définissez l'adaptateur du RecyclerView
                recyclerCricketers.setAdapter(new EtudiantAdapter(cricketersList, ActivityEtudiant.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer l'annulation de la récupération des données depuis la base de données
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
        Intent intent = new Intent(ActivityEtudiant.this, Connextion.class);
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
            Intent intent = new Intent(ActivityEtudiant.this, ModifierPasse.class);
            startActivity(intent);
            return true;
        }

        return false;
    }
}