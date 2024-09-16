package com.example.absencepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GererProf1 extends AppCompatActivity {
    private TextView box1;

    private TextView box2;
    private ImageView imageretour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererProf1.this, Admin.class);
                startActivity(intent);
            }
        });
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererProf1.this, GestionProf.class);
                startActivity(intent);
            }
        });

        box2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GererProf1.this, ActivityCricketers.class);
                startActivity(intent);
            }
        });

    }
}

