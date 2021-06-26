package org.kotakeducation.shoutboxstudent.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.kotakeducation.shoutboxstudent.R;

public class ProjectDisplay extends AppCompatActivity {

    private TextView projectTitle,projectDesc;
    private ImageView projectImage;
    private Button deleteProject,updateProject;
    private String UserID,ProjectID;
    private FirebaseFirestore db;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();

    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_display);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        projectTitle=findViewById(R.id.ProjectTitle);
        projectDesc=findViewById(R.id.ProjectDescription);
        projectImage=findViewById(R.id.ProjectImage);
        deleteProject=findViewById(R.id.DeleteProject);
        updateProject=findViewById(R.id.UpdateProject);
        db=FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        UserID=intent.getStringExtra("User ID");
        ProjectID=intent.getStringExtra("Project Id");


        //condition for students
        if (!mCurrentUser.getUid().equals(UserID)){
            Toast.makeText(ProjectDisplay.this, "Project belongs to current user", Toast.LENGTH_SHORT).show();

            deleteProject.setVisibility(View.GONE);
            updateProject.setVisibility(View.GONE);
        }

        display(UserID,ProjectID);

        deleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection(UserID).document(ProjectID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot snapshot : task.getResult()){
                                    if(snapshot.getString("User ID").equals(UserID) && snapshot.getString("Project Id").equals(ProjectID)){
                                        StorageReference fileref=reference.child(snapshot.getString("Image ID"));
                                        fileref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("Projects").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ProjectDisplay.this, "Project Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ProjectDisplay.this,ProjectFeed.class);
                                                        startActivity(intent);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProjectDisplay.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(ProjectDisplay.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProjectDisplay.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProjectDisplay.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void display(String UserID,String ProjectID){

        db.collection(UserID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot snapshot : task.getResult()){
                            if(ProjectID.equals(snapshot.getId())) {
                                projectTitle.setText(snapshot.getString("Project Title"));
                                projectDesc.setText(snapshot.getString("Project Desc"));
                                //Glide.with(ProjectDisplay.this).load(snapshot.getString("Project Image")).centerCrop().into(projectImage);
                                break;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProjectDisplay.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
}