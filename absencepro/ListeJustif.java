package com.example.absencepro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListeJustif  extends AppCompatActivity {
    private ImageView profil;
    boolean justificationValidee;
    private List<TableRow> checkedRows = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private ImageView imageretour;


    String subjectLabel;

    private DatabaseReference absenceRef;
    private DatabaseReference justifRef;
    private DatabaseReference matiereRef;
    String matiére;
    String seance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListeJustif.this, Prof.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);

        String nomp = sharedPreferences.getString("nom", "");
        String prenomp = sharedPreferences.getString("prenom", "");
        String nomCompletProf = prenomp + " " + nomp;


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        absenceRef = firebaseDatabase.getReference("Absence");
        matiereRef = firebaseDatabase.getReference("matiére");
        justifRef = firebaseDatabase.getReference("justificatif");

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
                    justifRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot justifSnapshot : dataSnapshot.getChildren()) {

                                String etat = justifSnapshot.child("etat").getValue(String.class);
                                int numjustif = justifSnapshot.child("numeroJustif").getValue(Integer.class);
                                int numAbsence = justifSnapshot.child("numeroAbsence").getValue(Integer.class);
                                String image = justifSnapshot.child("imageUrl").getValue(String.class);
                                String nomEtudiant = justifSnapshot.child("nomEtudiant").getValue(String.class);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference absencesRef = database.getReference("Absence");

                                Query query = absencesRef.orderByChild("numeroAbsence").equalTo(numAbsence);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            matiére = snapshot.child("matiere").getValue(String.class);
                                            seance = snapshot.child("seance").getValue(String.class);


                                        }


                                        if (matiére != null && matiére.equals(subjectLabel)) {
                                            // La matière de l'absence correspond à subjectLabel
                                            addTableRow(nomEtudiant, seance, image);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Gérer l'erreur d'annulation de la requête
                                    }
                                });
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

        Button valider = findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifierEtat();
                Toast.makeText(ListeJustif.this, "Justification validée " ,Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void addTableRow(String nomE, String seance, String imageUri) {
        TableLayout tableLayout = findViewById(R.id.table_layout);


        TableRow tableRow = new TableRow(this);

        // Créer les TextView pour le numéro, le nom et le prénom
        TextView nomTextView = new TextView(this);
        nomTextView.setText(nomE);
        nomTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        nomTextView.setPadding(10, 10, 10, 10);
        nomTextView.setTextSize(14);
        nomTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView seanceTextView = new TextView(this);
        seanceTextView.setText(seance);
        seanceTextView.setTextColor(getResources().getColor(android.R.color.black));
        seanceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        seanceTextView.setPadding(10, 10, 10, 10);
        seanceTextView.setTextSize(14);
        seanceTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        int imageSize = getResources().getDimensionPixelSize(R.dimen.selected_image_size);
        ; // Remplacez "selected_image_size" par la taille désirée
        TableRow.LayoutParams params = new TableRow.LayoutParams(imageSize, imageSize);
        Picasso.get().load(imageUri).into(imageView);
        imageView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        imageView.setPadding(10, 10, 10, 10);

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setMaxWidth(25); // Set a maximum width for the icon
        imageView.setMaxHeight(25);

        TableRow.LayoutParams imageParams = (TableRow.LayoutParams) imageView.getLayoutParams();
        imageParams.setMargins(25, 10, 0, 10); // Définissez les marges souhaitées (gauche, haut, droite, bas)
        imageView.setLayoutParams(imageParams);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(imageUri);
            }
        });


        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 4f));
        checkBox.setPadding(10, 10, 10, 10); // Ajouter un espacement à gauche de la CheckBox
        checkBox.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams caseParams = (TableRow.LayoutParams) checkBox.getLayoutParams();
        caseParams.setMargins(30, 10, -70, 10); // Définissez les marges souhaitées (gauche, haut, droite, bas)
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
        tableRow.addView(nomTextView);
        tableRow.addView(seanceTextView);
        tableRow.addView(imageView);
        tableRow.addView(checkBox);

        // Ajouter la rangée à la table
        tableLayout.addView(tableRow);
    }

    private void showImageDialog(String imageUri) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image); // Créez un fichier de mise en page XML "dialog_image.xml" pour le contenu du Dialog

        ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
        Picasso.get().load(imageUri).into(imageView);

        // Ajoutez un écouteur de clic pour fermer le Dialog lorsqu'il est cliqué
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void ModifierEtat() {
        if (checkedRows.isEmpty()) {
            Toast.makeText(ListeJustif.this, "Aucune justification sélectionnée.", Toast.LENGTH_SHORT).show();
            return;
        }

       justificationValidee = false;

        // Parcourir les lignes cochées et enregistrer les données dans la référence "Absence"
        for (TableRow row : checkedRows) {
            TextView nomTextView = (TextView) row.getChildAt(0);
            TextView SeanceTextView = (TextView) row.getChildAt(1);

            String nomE = nomTextView.getText().toString();
            String seanceA = SeanceTextView.getText().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference absencesRef = database.getReference("Absence");

            Query query = absencesRef.orderByChild("nomEtud").equalTo(nomE);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int numAbs = snapshot.child("numeroAbsence").getValue(Integer.class);
                        String seance = snapshot.child("seance").getValue(String.class);

                        if (seanceA.equals(seance)) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference absenceRef = databaseRef.child("Absence").child(String.valueOf(numAbs)).child("etat");
                            absenceRef.setValue("abj");

                            justificationValidee = true;

                            break; // Sortir de la boucle après avoir trouvé une correspondance
                        }
                    }

                    // Afficher le Toast "Justification validée" une seule fois
                    if (justificationValidee) {
                        Toast.makeText(ListeJustif.this, "Justification validée", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Gérer l'erreur d'annulation de la requête
                }
            });
        }
    }
}







