package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeEtudiant extends AppCompatActivity {
    private ImageView profil;
    private List<TableRow> checkedRows = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private TextView matiereTextView;
    private TextView séanceTextView;
    private int seanceCount; // Variable pour suivre le nombre de séances
    private int numeroAbsence = 1;
    String seanceLabel;
    private int derniéreSéance;

    private ImageView imageretour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10);

        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListeEtudiant.this, Prof.class);
                startActivity(intent);
            }
        });
        Button enregistrerButton = findViewById(R.id.enregistrer_button);
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        derniéreSéance = sharedPreferences.getInt("seanceCount", 0);

        String nomp = sharedPreferences.getString("nom", "");
        String prenomp = sharedPreferences.getString("prenom", "");
        String nomCompletProf = prenomp + " " + nomp;

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        matiereTextView = findViewById(R.id.TextViewMatiére);
        séanceTextView = findViewById(R.id.TextViewSéance);
        DatabaseReference derniereSeanceRef = database.getReference("séance").child(nomCompletProf).child("Nseance");
        derniereSeanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer derniereSeance = dataSnapshot.getValue(Integer.class);
                    if (derniereSeance != null) {
                        derniéreSéance = derniereSeance;
                        seanceLabel = "Séance : " + derniéreSéance;
                        séanceTextView.setText(seanceLabel);
                    }                    // Utiliser la valeur de la dernière séance récupérée comme nécessaire
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs éventuelles lors de la récupération de la dernière séance
            }
        });

            seanceLabel = "Séance : " + derniéreSéance;
            séanceTextView.setText(seanceLabel);


// Obtenir une référence à la base de données Firebase


        // Get reference to the "matiere" reference in the database
        DatabaseReference matiereRef = database.getReference("matiére");


        enregistrerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectLabel = matiereTextView.getText().toString(); // Get the subject label from the TextView
                enregistrerAbsences(subjectLabel, seanceLabel);
                derniéreSéance++; // Incrémenter le nombre de séances
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("seanceCount", derniéreSéance);
                editor.apply();
                seanceLabel = "Séance : " + derniéreSéance;
                séanceTextView.setText(seanceLabel); // Mettre à jour le TextView avec la nouvelle valeur de seanceLabel
                DatabaseReference derniereSeanceRef = database.getReference("séance").child(nomCompletProf).child("Nseance");
                derniereSeanceRef.setValue(derniéreSéance);

            }
        });

        // Obtenir une référence à la référence "étudiant"
        DatabaseReference etudiantRef = database.getReference("etudiant");
        etudiantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // La méthode onDataChange est appelée chaque fois que les données de la référence "étudiant" sont modifiées

                // Parcourir les enfants (étudiants) de la référence "étudiant"
                for (DataSnapshot etudiantSnapshot : dataSnapshot.getChildren()) {
                    // Récupérer les valeurs des champs "numéro", "nom" et "prénom"
                    String numero = etudiantSnapshot.child("Napogé").getValue(String.class);
                    String nom = etudiantSnapshot.child("nom").getValue(String.class);
                    String prenom = etudiantSnapshot.child("prenom").getValue(String.class);

                    // Afficher les données dans le tableau en ajoutant une nouvelle rangée
                    addTableRow(numero, nom, prenom);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs éventuelles lors de la récupération des données
            }
        });
        matiereRef.orderByChild("nomProf").equalTo(nomCompletProf).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if there is a matching subject for the professor
                if (dataSnapshot.exists()) {
                    // Retrieve the first matching subject
                    DataSnapshot subjectSnapshot = dataSnapshot.getChildren().iterator().next();
                    // Retrieve the label of the subject
                    String subjectLabel = subjectSnapshot.child("libellé").getValue(String.class);
                    matiereTextView.setText(subjectLabel);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur while retrieving the subject
            }
        });
        // Récupérer la dernière séance depuis la base de données


    }


    private void addTableRow(String numero, String nom, String prenom) {
        TableLayout tableLayout = findViewById(R.id.table_layout);

        // Ajouter la date actuel
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        TextView dateActuel = findViewById(R.id.TextViewDate);
        dateActuel.setText(currentDate);
        // Créer une nouvelle rangée
        TableRow tableRow = new TableRow(this);

        // Créer les TextView pour le numéro, le nom et le prénom
        TextView numeroTextView = new TextView(this);
        numeroTextView.setText(numero);
        numeroTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        numeroTextView.setPadding(10, 10, 10, 10);
        numeroTextView.setTextSize(12);
        numeroTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView nomTextView = new TextView(this);
        nomTextView.setText(nom);
        nomTextView.setTextColor(getResources().getColor(android.R.color.black));
        nomTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        nomTextView.setPadding(10, 10, 10, 10);
        nomTextView.setTextSize(14);
        nomTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView prenomTextView = new TextView(this);
        prenomTextView.setText(prenom);
        prenomTextView.setTextColor(getResources().getColor(android.R.color.black));
        prenomTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        prenomTextView.setPadding(10, 10, 10, 10);
        prenomTextView.setTextSize(14);
        prenomTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Créer une nouvelle CheckBox
        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 4f));
        checkBox.setPadding(10, 10, 10, 10); // Ajouter un espacement à gauche de la CheckBox
        checkBox.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams caseParams = (TableRow.LayoutParams) checkBox.getLayoutParams();
        caseParams.setMargins(30, 0, -70, 0); // Définissez les marges souhaitées (gauche, haut, droite, bas)
        checkBox.setLayoutParams(caseParams);
        // Ajouter un OnCheckedChangeListener à la CheckBox
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableRow row = (TableRow) buttonView.getParent();
                if (isChecked) {
                    checkedRows.add(row);
                } else {
                    checkedRows.remove(row);
                }
            }
        });

        // Ajouter les TextView et la CheckBox à la rangée
        tableRow.addView(numeroTextView);
        tableRow.addView(nomTextView);
        tableRow.addView(prenomTextView);
        tableRow.addView(checkBox);

        // Ajouter la rangée à la table
        tableLayout.addView(tableRow);
    }

    private void enregistrerAbsences(String subjectLabel, String seanceLabel) {
        if (checkedRows.isEmpty()) {
            Toast.makeText(ListeEtudiant.this, "Aucune Etudiant sélectionnée.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtenir une référence à la base de données Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Obtenir une référence à la référence "Absence"
        DatabaseReference absenceRef = database.getReference("Absence");

        // Obtenir la date actuelle
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        absenceRef.orderByChild("numeroAbsence").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int dernierNumeroAbsence = snapshot.child("numeroAbsence").getValue(Integer.class);
                        numeroAbsence = dernierNumeroAbsence + 1;
                    }
                } else {
                    // Aucune absence existante, commencer par l'ID 1
                    numeroAbsence = 1;
                }

                // Parcourir les lignes cochées et enregistrer les données dans la référence "Absence"
                for (TableRow row : checkedRows) {
                    TextView numeroTextView = (TextView) row.getChildAt(0);
                    TextView nomTextView = (TextView) row.getChildAt(1);
                    TextView prenomTextView = (TextView) row.getChildAt(2);

                    String numero = numeroTextView.getText().toString();
                    String nom = nomTextView.getText().toString();
                    String prenom = prenomTextView.getText().toString();
                     String nomC = prenom + " " + nom;

                    // Créer un nouvel objet Absence avec le numéro, la date, l'état et la matière
                    Absence absence = new Absence(numeroAbsence, numero, nomC, currentDate, "abnj", subjectLabel, seanceLabel);

                    // Enregistrer l'objet Absence dans la référence "Absence" avec le numéro d'absence comme ID
                    absenceRef.child(String.valueOf(numeroAbsence)).setValue(absence);

                    numeroAbsence++;
                }

                // Effacer les lignes cochées
                checkedRows.clear();

                // Décocher les cases à cocher
                TableLayout tableLayout = findViewById(R.id.table_layout);
                int rowCount = tableLayout.getChildCount();
                for (int i = 2; i < rowCount; i++) { // Commencez à l'indice 2 pour éviter les lignes d'en-tête
                    View view = tableLayout.getChildAt(i);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;
                        CheckBox checkBox = (CheckBox) row.getChildAt(3);
                        checkBox.setChecked(false);
                    }
                }

                // Afficher un Toast "Enregistré avec succès"
                Toast.makeText(ListeEtudiant.this, "Enregistré avec succès", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs éventuelles lors de l'annulation de l'événement
            }
        });
    }



}
