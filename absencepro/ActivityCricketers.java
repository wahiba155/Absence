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


public class ActivityCricketers extends AppCompatActivity {

    RecyclerView recyclerCricketers;
    SharedPreferences sharedPreferences;

    private ImageView imageretour;
    private ImageView profil;
    private ImageView home;

    ArrayList<Cricketer> cricketersList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricketers);
        profil = findViewById(R.id.photo1);
        home = findViewById(R.id.photo3);


        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCricketers.this, Profile.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCricketers.this, Admin.class);
                startActivity(intent);
            }
        });

        imageretour = findViewById(R.id.retour);

            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityCricketers.this, GererProf1.class);
                    startActivity(intent);
                }
            });

        recyclerCricketers = findViewById(R.id.recycler_cricketers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerCricketers.setLayoutManager(layoutManager);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("professeur");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cricketersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Professeur professeur = snapshot.getValue(Professeur.class);
                    if (professeur != null) {
                        DatabaseReference matiereRef = FirebaseDatabase.getInstance().getReference().child("matiére");
                        matiereRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Matiere matiere = snapshot.getValue(Matiere.class);
                                    if (matiere != null) {
                                        String nomC = professeur.getPrenom() + " " + professeur.getNom();
                                        if (matiere.getNomProf().equals(nomC)) {
                                            Cricketer cricketer = new Cricketer(professeur.getPrenom(), professeur.getNom(), matiere.getLibellé(), professeur.getTitre());
                                            cricketersList.add(cricketer);
                                            break;
                                        }
                                    }
                                }

                                // Une fois que toutes les données sont récupérées, définissez l'adaptateur du RecyclerView
                                recyclerCricketers.setAdapter(new CricketerAdapter(cricketersList, ActivityCricketers.this));                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Gérer l'annulation de la récupération des données depuis la base de données
                            }
                        });
                    }
                }
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
        Intent intent = new Intent(ActivityCricketers.this, Connextion.class);
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
            Intent intent = new Intent(ActivityCricketers.this, ModifierPasse.class);
            startActivity(intent);
            return true;
        }

        return false;
    }
}
