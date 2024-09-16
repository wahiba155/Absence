package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeAbsences extends AppCompatActivity {

    String nom;
    String prenom;
    String subjectLabel;
    SharedPreferences sharedPreferences;
    private ImageView imageretour;


    private DatabaseReference absenceRef;
    private DatabaseReference matiereRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);

        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListeAbsences.this, Prof.class);
                startActivity(intent);
            }
        });
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        String nomp = sharedPreferences.getString("nom", "");
        String prenomp = sharedPreferences.getString("prenom", "");
        String nomCompletProf = prenomp + " " + nomp;

        // Obtenir une référence à la base de données Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        absenceRef = firebaseDatabase.getReference("Absence");
        matiereRef = firebaseDatabase.getReference("matiére");

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

                    // Récupérer les absences de la référence "Absence"
                    absenceRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Parcourir toutes les absences
                            for (DataSnapshot absenceSnapshot : dataSnapshot.getChildren()) {
                                // Récupérer les données de l'absence
                                String numeroEtudiant = absenceSnapshot.child("numero").getValue(String.class);
                                String nomEtudiant = absenceSnapshot.child("nomEtud").getValue(String.class);
                                String date = absenceSnapshot.child("date").getValue(String.class);
                                String seance = absenceSnapshot.child("seance").getValue(String.class);
                                String matiere = absenceSnapshot.child("matiere").getValue(String.class);
                                String etat = absenceSnapshot.child("etat").getValue(String.class);


                                if (matiere != null && matiere.equals(subjectLabel)) {
                                    // La matière de l'absence correspond à subjectLabel
                                    addTableRow(numeroEtudiant,nomEtudiant, etat, seance, date, absenceSnapshot.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Gérer les erreurs de récupération des absences
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur while retrieving the subject
            }
        });
    }

    private void addTableRow(String numero,String nom, String etat, String seance, String date, final String absenceId) {
        TableLayout tableLayout = findViewById(R.id.table_layout);

        // Create a new row
        TableRow tableRow = new TableRow(this);

        ImageView suppressionImageView = new ImageView(this);
        suppressionImageView.setImageResource(R.drawable.supp);
        suppressionImageView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        suppressionImageView.setScaleType(ImageView.ScaleType.CENTER);
        suppressionImageView.setMaxWidth(10); // Set a maximum width for the icon
        suppressionImageView.setMaxHeight(10); // Set a maximum height for the icon

        // Create a TextView for each data

        TextView numTextView = new TextView(this);
        numTextView.setText(numero);
        numTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        numTextView.setGravity(Gravity.CENTER);

        TextView nomTextView = new TextView(this);
        nomTextView.setText(nom);
        nomTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        nomTextView.setGravity(Gravity.CENTER);



        TextView etatTextView = new TextView(this);
        etatTextView.setText(etat);
        etatTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        etatTextView.setGravity(Gravity.CENTER);

        TextView seanceTextView = new TextView(this);
        seanceTextView.setText(seance);
        seanceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        seanceTextView.setGravity(Gravity.CENTER);

        TextView dateTextView = new TextView(this);
        dateTextView.setText(date);
        dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        dateTextView.setGravity(Gravity.CENTER);

        // Add TextViews to the row
        tableRow.addView(numTextView);
        tableRow.addView(nomTextView);
        tableRow.addView(etatTextView);
        tableRow.addView(seanceTextView);
        tableRow.addView(dateTextView);
        tableRow.addView(suppressionImageView);

        // Add the row to the table
        tableLayout.addView(tableRow);
        Absence absence = new Absence();


        // Get the absenceId for the current row


        suppressionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Absence").child(absenceId);
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tableLayout.removeView(tableRow);
                        Toast.makeText(ListeAbsences.this, "Suppression réussie", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e("ListeAbsences", "Erreur lors de la suppression de l'absence: " + task.getException().getMessage());
                    }
                });
            }
        });





// Set the absenceId as a tag on the delete icon
        suppressionImageView.setTag(absenceId);
    }
}