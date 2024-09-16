package com.example.absencepro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AjouterEtud extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layoutList;
    Button buttonAdd;
    Button buttonSubmitList;
    private DatabaseReference databaseRef;
    SharedPreferences sharedPreferences;

    private ImageView imageretour;



    List<String> teamList = new ArrayList<>();
    ArrayList<CricketerET> cricketersList = new ArrayList<>();
    ArrayList<Etud> EtudiantList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actuvity_ajouetud);
        databaseRef = FirebaseDatabase.getInstance().getReference();


        layoutList = findViewById(R.id.layout_list);

        buttonAdd = findViewById(R.id.button_add);
        buttonSubmitList = findViewById(R.id.button_submit_list);
        imageretour = findViewById(R.id.retour);


        buttonAdd.setOnClickListener(this);
        buttonSubmitList.setOnClickListener(this);

        teamList.add("M");
        teamList.add("F");



            imageretour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AjouterEtud.this, GererEtud.class);
                    startActivity(intent);
                }
            });





    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add) {
            addView();
        } else if (v.getId() == R.id.button_submit_list) {
            if (checkIfValidAndRead()) {
                for (Etud etudiant : EtudiantList) {
                    databaseRef.child("etudiant").push().setValue(etudiant);
                }
                Intent intent = new Intent(AjouterEtud.this, ActivityEtudiant.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", cricketersList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    private boolean checkIfValidAndRead() {
        cricketersList.clear();
        boolean result = true;

        for (int i = 0; i < layoutList.getChildCount(); i++) {
            View cricketerView = layoutList.getChildAt(i);

            EditText editTextName = cricketerView.findViewById(R.id.edit_cricketer_name);
            EditText editTextnumero = cricketerView.findViewById(R.id.edit_cricketer_numero);
            EditText editTextNom = cricketerView.findViewById(R.id.edit_cricketer_nom);

            AppCompatSpinner spinnerTeam = cricketerView.findViewById(R.id.spinner_team);

            CricketerET cricketer = new CricketerET();

            if (!editTextName.getText().toString().equals("") && !editTextnumero.getText().toString().equals("") && !editTextNom.getText().toString().equals("")  ) {
                String nom = editTextNom.getText().toString();
                String prenom = editTextName.getText().toString();
                String numero = editTextnumero.getText().toString();
                String email = prenom + "." + nom + "22@ump.ac.ma";
                String sexe = spinnerTeam.getSelectedItem().toString();
                String mdp = numero;
                String NomC = prenom + " " + nom;

                Etud etudiant = new Etud(numero,nom,prenom,email,mdp,"Etudiant(e)",sexe, "inconnu.png");
                EtudiantList.add(etudiant);

            } else {
                result = false;
                break;
            }

            if (spinnerTeam.getSelectedItemPosition() != -1) {
                cricketer.setTeamName(teamList.get(spinnerTeam.getSelectedItemPosition()));

            } else {
                result = false;
                break;
            }

            cricketersList.add(cricketer);
        }


        if (cricketersList.size() == -1) {
            result = false;
            Toast.makeText(this, "Ajoutez tous les informations du professeur !", Toast.LENGTH_SHORT).show();
        } else if (!result) {
            Toast.makeText(this, "Entrez tous les détails correctement !", Toast.LENGTH_SHORT).show();
        } else {
            // Succès : toutes les informations sont valides et prêtes à être ajoutées à la base de données
            Toast.makeText(this, "Informations ajoutées avec succès !", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void addView() {
        final View cricketerView = getLayoutInflater().inflate(R.layout.row_add_etud, null, false);

        EditText editText = cricketerView.findViewById(R.id.edit_cricketer_name);
        EditText editText2 = cricketerView.findViewById(R.id.edit_cricketer_numero);
        EditText editText3 = cricketerView.findViewById(R.id.edit_cricketer_nom);



        AppCompatSpinner spinnerTeam = cricketerView.findViewById(R.id.spinner_team);
        ImageView imageClose = cricketerView.findViewById(R.id.image_remove);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamList);
        spinnerTeam.setAdapter(arrayAdapter);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(cricketerView);
            }
        });

        layoutList.addView(cricketerView);
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }
}

