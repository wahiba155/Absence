package com.example.absencepro;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CricketerAdapter extends RecyclerView.Adapter<CricketerAdapter.CricketerView> {

    private ArrayList<Cricketer> cricketersList;
    private Context context;

    public CricketerAdapter(ArrayList<Cricketer> cricketersList, Context context) {
        this.cricketersList = cricketersList;
        this.context = context;
    }

    @NonNull
    @Override
    public CricketerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cricketer, parent, false);
        return new CricketerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CricketerView holder, int position) {
        Cricketer cricketer = cricketersList.get(position);
        holder.textTeamName.setText(cricketer.getTeamName());

        holder.textCricketermatiere.setText(cricketer.getCricketerMatiére());

        holder.textCricketernom.setText(cricketer.getCricketernom());
        holder.textCricketerName.setText(cricketer.getCricketerName());

        holder.imageViewModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cricketer cricketer = cricketersList.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, ModifierProf.class);
                // Passez les données nécessaires à l'activité ModifierProf
                intent.putExtra("prenom", cricketer.getCricketerName());
                intent.putExtra("nom", cricketer.getCricketernom());
                intent.putExtra("matiere", cricketer.getCricketerMatiére());
                intent.putExtra("titre",cricketer.getTeamName() ); // Remplacez "titre" par la variable contenant la valeur du titre
                context.startActivity(intent);
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Supprimer le professeur correspondant à la position de la liste
                deleteProfesseur(cricketer, holder.itemView.getContext());
            }
        });
    }

    private void deleteProfesseur(Cricketer cricketer, Context context) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("professeur");
        // Utiliser une requête pour trouver le professeur avec les détails correspondants (nom, prénom, etc.)
        Query query = databaseRef.orderByChild("nom").equalTo(cricketer.getCricketernom());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   String prenom = snapshot.child("prenom").getValue(String.class);;
                    if(prenom.equals(cricketer.getCricketerName())){
                    // Supprimer le professeur de la base de données
                    snapshot.getRef().removeValue();
                }
                // Afficher un toast pour indiquer que la suppression a été effectuée avec succès
                Toast.makeText(context, "Suppression réussie", Toast.LENGTH_SHORT).show();
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'annulation de la suppression du professeur depuis la base de données
            }
        });
    }

    @Override
    public int getItemCount() {
        return cricketersList.size();
    }

    public class CricketerView extends RecyclerView.ViewHolder {

        ImageView imageViewModifier;
        TextView textCricketerName, textCricketernom, textTeamName, textCricketermatiere;
        ImageView imageViewDelete;

        public CricketerView(@NonNull View itemView) {
            super(itemView);

            textCricketerName = itemView.findViewById(R.id.text_cricketer_name);
            textCricketernom = itemView.findViewById(R.id.text_cricketer_nom);
            textCricketermatiere = itemView.findViewById(R.id.text_cricketer_matiere);
            textTeamName = itemView.findViewById(R.id.text_team_name);
            imageViewDelete = itemView.findViewById(R.id.supprimer);
            imageViewModifier = itemView.findViewById(R.id.modifier);

        }
    }
}