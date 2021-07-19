package org.kotakeducation.shoutboxstudent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.kotakeducation.shoutboxstudent.R;

public class ProjectDisplayActivity extends AppCompatActivity {

    private TextView projectTitle,projectDesc;
    private ImageView projectImage;
    private Button deleteProject,updateProject, comments;
    private String UserID,ProjectID;
    private FirebaseFirestore db;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();

    TextView enquiryDetailsTV;

    ImageView likeButton;
    TextView likeCountTV;

    DatabaseReference likesRef;

    FirebaseUser mCurrentUser;
    long count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_display);

        projectTitle=findViewById(R.id.ProjectTitle);
        projectDesc=findViewById(R.id.ProjectDescription);
        projectImage=findViewById(R.id.ProjectImage);
        deleteProject=findViewById(R.id.DeleteProject);
        updateProject=findViewById(R.id.UpdateProject);
        comments=findViewById(R.id.Comments);
        db=FirebaseFirestore.getInstance();

        enquiryDetailsTV=findViewById(R.id.enquiryDetailsTV);

        likeButton = findViewById(R.id.likeImage);
        likeCountTV = findViewById(R.id.likeCount);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        Intent intent=getIntent();
        UserID=intent.getStringExtra("User ID");
        ProjectID=intent.getStringExtra("Project Id");

        likesRef = FirebaseDatabase.getInstance().getReference().child("project-likes").child(ProjectID);


        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                likeCountTV.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            likesRef.child(mCurrentUser.getUid()).setValue(mCurrentUser.getUid());
                            Toast.makeText(ProjectDisplayActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            likesRef.child(mCurrentUser.getUid()).removeValue();
                            Toast.makeText(ProjectDisplayActivity.this, "Like Removed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

        display(UserID,ProjectID);

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProjectDisplayActivity.this, Comments.class);
                intent.putExtra("User ID",UserID);
                intent.putExtra("Project Id",ProjectID);
                startActivity(intent);
            }
        });

        updateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProjectDisplayActivity.this, EditProjectActivity.class);
                intent.putExtra("User ID",UserID);
                intent.putExtra("Project Id",ProjectID);
                startActivity(intent);
                finish();
            }
        });

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
                                                                Toast.makeText(ProjectDisplayActivity.this, "Project Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ProjectDisplayActivity.this, ProjectFeedActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ProjectDisplayActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(ProjectDisplayActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProjectDisplayActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProjectDisplayActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
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

                                enquiryDetailsTV.setText(snapshot.get("enquiryDetails").toString());

                                Glide.with(ProjectDisplayActivity.this).load(snapshot.getString("Project Image")).centerCrop().into(projectImage);
                                break;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProjectDisplayActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });


    }

}