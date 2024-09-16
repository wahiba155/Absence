package com.example.absencepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class ModifierEtud extends AppCompatActivity {
    private EditText editCricketerName;
    private EditText editCricketerNom;
    private EditText editCricketernumero;
    private Button buttonModifier;
    private Spinner spinnerTitre;
    private ArrayAdapter<String> titreAdapter;
    private List<String> titreList;


    String prenomP;
    String prenom;
    String nom;
    String titre;
    String numero;
    private ImageView imageretour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyetud);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifierEtud.this, ActivityEtudiant.class);
                startActivity(intent);
            }
        });

        // Associez les variables aux éléments de la vue
        editCricketerName = findViewById(R.id.edit_cricketer_name);
        editCricketerNom = findViewById(R.id.edit_cricketer_nom);
        editCricketernumero = findViewById(R.id.edit_cricketer_numero);
        buttonModifier = findViewById(R.id.button);
        spinnerTitre = findViewById(R.id.spinner_titre);
        titreList = Arrays.asList(getResources().getStringArray(R.array.titres_array_sexe));
        titreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titreList);
        titreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTitre.setAdapter(titreAdapter);

        // Récupérez les données passées à partir de l'intent
        Intent intent = getIntent();
        if (intent != null) {
            prenom = intent.getStringExtra("prenom");
            nom = intent.getStringExtra("nom");
            numero = intent.getStringExtra("numero");
            titre = intent.getStringExtra("sexe"); // Ajoutez cette ligne pour récupérer le titre


            // Affichez les données dans les EditText
            editCricketerName.setText(prenom);
            editCricketerNom.setText(nom);
            editCricketernumero.setText(numero);
            // Sélectionnez le titre dans le spinner
            int titrePosition = titreAdapter.getPosition(titre);
            spinnerTitre.setSelection(titrePosition);

        }

        // Ajoutez un écouteur de clic pour le bouton de modification
        buttonModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifierProfesseur();
            }
        });
    }

    private void modifierProfesseur() {
        String prenomM = editCricketerName.getText().toString();
        String nomM = editCricketerNom.getText().toString();
        String numeroM = editCricketernumero.getText().toString();
        String titreM = spinnerTitre.getSelectedItem().toString();


        // Mettre à jour les informations du professeur dans la base de données Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("etudiant");
        Query query = databaseRef.orderByChild("nom").equalTo(nom);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    prenomP = snapshot.child("prenom").getValue(String.class);
                    if (prenomP.equals(prenom)) {
                        // Mettre à jour les valeurs des attributs du professeur
                        snapshot.getRef().child("prenom").setValue(prenomM);
                        snapshot.getRef().child("nom").setValue(nomM);
                        snapshot.getRef().child("sexe").setValue(titreM);
                        snapshot.getRef().child("Napogé").setValue(numeroM);
                        snapshot.getRef().child("email").setValue(prenomM + "." + nomM + "22@ump.ac.ma");

                    }
                }


                Toast.makeText(ModifierEtud.this, "Modification réussie", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifierEtud.this, ActivityEtudiant.class);
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'annulation de la mise à jour du professeur dans la base de données
            }
        });
    }
}
