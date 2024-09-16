package com.example.absencepro;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AbsenceEtud extends AppCompatActivity {

    String nom;
    String prenom;
    String nomComplet;
    private Uri selectedImageUri;
    SharedPreferences sharedPreferences;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private DatabaseReference notificationRef;

    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private String seanceA;
    private String dateA;
    private String matiereA;
    int numeroAbsence;
    int numero;

    private DatabaseReference absenceRef;
    private ImageView selectedImageView;
    private FirebaseStorage firebaseStorage;
    private int numeroJustif = 1;
    String seancee;
    String datee;
    String name;
    String matiere;
    int numABSe;
    private ImageView imageretour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);
        imageretour = findViewById(R.id.retour);

        imageretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AbsenceEtud.this, Etudiant.class);
                startActivity(intent);
            }
        });
        firebaseStorage = FirebaseStorage.getInstance();
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);


        nom = sharedPreferences.getString("nom", "");
        prenom = sharedPreferences.getString("prenom", "");

        nomComplet = prenom + " " + nom;

        // Obtenir une référence à la base de données Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        notificationRef = firebaseDatabase.getReference("notification"); // Référence à la base de données "notification"

        absenceRef = firebaseDatabase.getReference("Absence");

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
                    int numAbsence = absenceSnapshot.child("numeroAbsence").getValue(Integer.class);

                    if (nomComplet != null && nomComplet.equals(nomEtudiant)) {
                        // La matière de l'absence correspond à subjectLabel
                        addTableRow(matiere, etat, seance, date);
                        retrieveImagesFromFirebase(numAbsence);

                        // Après l'ajout des lignes à la table
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs de récupération des absences
            }
        });



        }

    private void addTableRow(String matiere, String etat, String seance, String date) {
        TableLayout tableLayout = findViewById(R.id.table_layout);

        ImageView plusImageView; // Déclaration déplacée ici

        // Create a new row
        TableRow tableRow = new TableRow(this);
        plusImageView = new ImageView(this);
        plusImageView.setImageResource(R.drawable.icon);
        plusImageView.setLayoutParams(new TableRow.LayoutParams(0, WRAP_CONTENT, 1f));
        int imageSize = getResources().getDimensionPixelSize(R.dimen.selected_image_size); // Remplacez "selected_image_size" par la taille désirée
        TableRow.LayoutParams params = new TableRow.LayoutParams(imageSize, imageSize);
        plusImageView.setLayoutParams(params);
        plusImageView.setScaleType(ImageView.ScaleType.CENTER);
        plusImageView.setMaxWidth(10); // Set a maximum width for the icon
        plusImageView.setMaxHeight(10); // Set a maximum height for the icon
        plusImageView.setTag(null); // Set the initial tag to null


        // Create a TextView for each data
        TextView matiereTextView = new TextView(this);
        matiereTextView.setText(matiere);
        matiereTextView.setLayoutParams(new TableRow.LayoutParams(0, WRAP_CONTENT, 1f));
        matiereTextView.setGravity(Gravity.CENTER);

        TextView seanceTextView = new TextView(this);
        seanceTextView.setText(seance);
        seanceTextView.setLayoutParams(new TableRow.LayoutParams(0, WRAP_CONTENT, 1f));
        seanceTextView.setGravity(Gravity.CENTER);

        TextView dateTextView = new TextView(this);
        dateTextView.setText(date);
        dateTextView.setLayoutParams(new TableRow.LayoutParams(0, WRAP_CONTENT, 1f));
        dateTextView.setGravity(Gravity.CENTER);


        TextView etatTextView = new TextView(this);
        etatTextView.setText(etat);
        etatTextView.setLayoutParams(new TableRow.LayoutParams(0, WRAP_CONTENT, 1f));
        etatTextView.setGravity(Gravity.CENTER);

        // Add the TextViews to the TableRow
        tableRow.addView(matiereTextView);
        tableRow.addView(etatTextView);
        tableRow.addView(seanceTextView);
        tableRow.addView(dateTextView);
        tableRow.addView(plusImageView);
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seanceA = seanceTextView.getText().toString();
                dateA = dateTextView.getText().toString();
                matiereA = matiereTextView.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference absenceRef = database.getReference("Absence");

// Effectuer une requête pour trouver l'entrée correspondante
                absenceRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String seance = snapshot.child("seance").getValue(String.class);
                            String date = snapshot.child("date").getValue(String.class);
                            String nom = snapshot.child("nomEtud").getValue(String.class);
                            String matiere = snapshot.child("matiere").getValue(String.class);
                             numero = snapshot.child("numeroAbsence").getValue(Integer.class);

                            if (seanceA.equals(seance) && dateA.equals(date) && nomComplet.equals(nom) &&  matiereA.equals(matiere) && numero!=0 ) {
                                if (plusImageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon).getConstantState())) {
                                    selectedImageView = (ImageView) v;

                                    showImageSourceDialog(plusImageView.getId());
                                } else {
                                    showConfirmationDialog(numero,tableRow);


                                }
                                break; // Sortir de la boucle si une correspondance est trouvée
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Gérer l'erreur d'annulation de la requête
                    }
                });


            }
        });


        // Add the TableRow to the TableLayout
        tableLayout.addView(tableRow);
    }

    private void showConfirmationDialog(int num, TableRow tr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cette justification ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("justificatif").child(String.valueOf(num));
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Drawable iconDrawable = ContextCompat.getDrawable(AbsenceEtud.this, R.drawable.icon);

                        // Récupérer l'ImageView existant de la ligne
                        ImageView imageView = (ImageView) tr.getChildAt(4);

                        // Remplacer l'image de l'ImageView avec l'icône
                        imageView.setImageDrawable(iconDrawable);




                        Toast.makeText(AbsenceEtud.this, "Suppression réussie" , Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ListeAbsences", "Erreur lors de la suppression de l'absence: " + task.getException().getMessage());
                    }
                });
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void showImageSourceDialog(int viewId) {
        final CharSequence[] options = {"Prendre une photo", "Choisir depuis la galerie", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter une justificatif");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Prendre une photo")) {
                    checkCameraPermission();
                } else if (options[item].equals("Choisir depuis la galerie")) {
                    openGallery();
                } else if (options[item].equals("Annuler")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        }
    }

    private void savetoFirebase(Uri imageUri) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the "justificatif" node
        DatabaseReference justificatifRef = database.getReference("justificatif");
        justificatifRef.orderByChild("numeroJustif").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int dernierNumeroJustif = snapshot.child("numeroJustif").getValue(Integer.class);
                        numeroJustif = dernierNumeroJustif + 1;
                    }
                }
                // Create a new Justificatif object with the image URL and other details
                Justificatif justificatif = new Justificatif(numeroJustif, numero, imageUri.toString(), nomComplet);

                // Set the value of the Justificatif object using the numeroJustif as the ID
                justificatifRef.child(String.valueOf(numeroJustif)).setValue(justificatif)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("Justification ajoutée");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("Échec de l'insertion : " + e.getMessage());
                            }
                        });
                numeroJustif++;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImageUri = getImageUri(this, imageBitmap); // Crée l'URI après capture
            if (selectedImageUri != null) {
                selectedImageView.setImageBitmap(imageBitmap);
                savetoFirebase(selectedImageUri);
            } else {
                Log.e("UploadError", "Image URI is null after capture");
                showToast("Échec du téléchargement : URI de l'image est null");
            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImageView.setImageURI(selectedImageUri);
                savetoFirebase(selectedImageUri);
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("UploadError", "Image URI is null after gallery selection");
                showToast("Échec du téléchargement : URI de l'image est null");
            }
        }
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "justificatif", null);
        if (path == null) {
            Log.e("GetImageUriError", "Path is null");
        } else {
            Log.d("GetImageUriInfo", "Image Path: " + path);
        }
        return Uri.parse(path);
    }


    private void retrieveImagesFromFirebase(int numAbs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference justificatifRef = database.getReference("justificatif");

        justificatifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot justificatifSnapshot : dataSnapshot.getChildren()) {
                    // Récupérer les détails de l'image justificatif
                    String imageUrl = justificatifSnapshot.child("imageUrl").getValue(String.class);

                    int numAbsence = justificatifSnapshot.child("numeroAbsence").getValue(Integer.class);

                    // Vérifier si l'image justificatif correspond à la séance et à la date
                    if (imageUrl != null && numAbsence == numAbs) {

                        displayImage(imageUrl, numAbsence);
                        break; // Sortir de la boucle après avoir trouvé l'image correspondante
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs de récupération des images
            }
        });
    }






    private void displayImage(String imageUri, int numAbs) {



        TableLayout tableLayout = findViewById(R.id.table_layout);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference absencesRef = database.getReference("Absence");
        absenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    seancee = snapshot.child("seance").getValue(String.class);
                    datee = snapshot.child("date").getValue(String.class);
                    matiere = snapshot.child("matiere").getValue(String.class);
                    name = snapshot.child("nomEtud").getValue(String.class);
                    numABSe = snapshot.child("numeroAbsence").getValue(Integer.class);

                    if (numAbs != 0 && numABSe ==  numAbs) {

                        for (int i = 0; i < tableLayout.getChildCount(); i++) {
                            View view = tableLayout.getChildAt(i);
                            if (view instanceof TableRow) {
                                TableRow tableRow = (TableRow) view;

                                // Vérifier si les TextViews de la ligne correspondent à la séance et à la date
                                TextView matiereTextView = (TextView) tableRow.getChildAt(0);

                                TextView seanceTextView = (TextView) tableRow.getChildAt(2);
                                TextView dateTextView = (TextView) tableRow.getChildAt(3);


                                if (seanceTextView != null && dateTextView != null) {
                                    String currentSeance = seanceTextView.getText().toString();
                                    String currentDate = dateTextView.getText().toString();
                                    String currentMatiere = matiereTextView.getText().toString();



                                    // Vérifier si la séance et la date correspondent
                                    if (currentSeance.equals(seancee) && currentDate.equals(datee) && nomComplet.equals(name) && currentMatiere.equals(matiere)) {
                                        // Trouvé l'emplacement correspondant, charger l'image avec Picasso

                                        ImageView imageView = (ImageView) tableRow.getChildAt(4);
                                        Picasso.get().load(imageUri).into(imageView);
                                        break; // Sortir de la boucle après avoir affiché l'image
                                    }
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer l'erreur d'annulation de la requête
            }
        });





    }
}
