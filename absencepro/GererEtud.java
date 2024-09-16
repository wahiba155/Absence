package com.example.absencepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GererEtud extends AppCompatActivity {
    private TextView box1;

    private TextView box2;
    private ImageView imageretour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etud);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererEtud.this, Admin.class);
                startActivity(intent);
            }
        });
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererEtud.this, AjouterEtud.class);
                startActivity(intent);
            }
        });

        box2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererEtud.this, ActivityEtudiant.class);
                startActivity(intent);
            }
        });

    }
}

