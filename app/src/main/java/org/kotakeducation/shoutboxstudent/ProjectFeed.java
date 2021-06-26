package org.kotakeducation.shoutboxstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProjectFeed extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    //private FirebaseAuth mAuth;
    private AdapterProjectFeed adapter;
    //private ImageView log_out;
    private List<ModelForProjectFeed> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_feed);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db=FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        adapter= new AdapterProjectFeed(this,list);
        recyclerView.setAdapter(adapter);
        showData();

        floatingActionButton=findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectFeed.this,AddProject.class);
                startActivity(intent);
            }
        });

    }

    public void showData(){
        db.collection("Projects").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for(DocumentSnapshot snapshot : task.getResult()){
                            ModelForProjectFeed model =new ModelForProjectFeed(snapshot.getString("Project Title"),snapshot.getString("User ID"),snapshot.getString("Date of Upload"),snapshot.getString("User ID"),snapshot.getString("Project Id"));
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProjectFeed.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}