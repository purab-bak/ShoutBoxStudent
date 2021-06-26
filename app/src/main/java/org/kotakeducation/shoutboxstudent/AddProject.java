package org.kotakeducation.shoutboxstudent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddProject extends AppCompatActivity {

    private EditText ProjectTitle,ProjectDesc;
    private ImageView projectImage;
    private Uri imageUri;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    private Button UploadProject;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        ProjectTitle=findViewById(R.id.ProjectTitle);
        ProjectDesc=findViewById(R.id.ProjectDescription);
        projectImage=findViewById(R.id.ProjectImage);
        UploadProject=findViewById(R.id.AddProject);
        db=FirebaseFirestore.getInstance();

        projectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallerayIntent = new Intent();
                gallerayIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallerayIntent.setType("image/*");
                startActivityForResult(gallerayIntent,2);
            }
        });

        UploadProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectTitle=ProjectTitle.getText().toString();
                String projectDesc=ProjectDesc.getText().toString();
                // check for empty fields
                if(imageUri== null)
                    Toast.makeText(AddProject.this, "Please add an Image", Toast.LENGTH_SHORT).show();
                else if(projectTitle.isEmpty())
                    Toast.makeText(AddProject.this, "Please add a Title", Toast.LENGTH_SHORT).show();
                else if(projectDesc.isEmpty())
                    Toast.makeText(AddProject.this, "Please add a Description", Toast.LENGTH_SHORT).show();
                else
                    uploadToStorage(imageUri,projectTitle,projectDesc);
                //saveToUserList(projectTitle,projectDesc,userID);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            projectImage.setImageURI(imageUri);
        }
    }

    private void uploadToStorage(Uri uri,String projectTitle,String projectDesc){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID= currentUser.getUid();

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String ImageId=userID+" "+ts;

        StorageReference fileref=reference.child(ImageId);
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL=uri.toString();
                        saveToUserList(projectTitle,projectDesc,userID,imageURL,ImageId);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProject.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToUserList(String projectTitle,String projectDesc,String userID,String imageURL,String ImageId){
        Map<String, Object> map = new HashMap<>();
        map.put("Project Title",projectTitle);
        map.put("Project Desc",projectDesc);
        map.put("Project Image",imageURL);
        //map.put("Image ID",ImageId);

//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String userID= currentUser.getUid();

        db.collection(userID)
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String projectID = documentReference.getId();
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        saveToCommonList(userID,projectID,projectTitle,date,ImageId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProject.this, "Project Couldn't Be Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToCommonList(String userID,String projectID,String projectTitle,String date,String ImageID){
        Map<String, Object> map = new HashMap<>();
        map.put("User ID",userID);
        map.put("Project Id",projectID);
        map.put("Project Title",projectTitle);
        map.put("Date of Upload",date);
        map.put("Image ID",ImageID);

        db.collection("Projects")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddProject.this, "Project Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddProject.this,ProjectFeed.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProject.this, "Project Couldn't Be Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}