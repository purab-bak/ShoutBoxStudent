package org.kotakeducation.shoutboxstudent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.kotakeducation.shoutboxstudent.Adapters.FormAdapter;
import org.kotakeducation.shoutboxstudent.Models.EnquiryProjectModel;
import org.kotakeducation.shoutboxstudent.Models.FormModel;
import org.kotakeducation.shoutboxstudent.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEnquiryDetailsActivity extends AppCompatActivity {

    RecyclerView formRV;

    List<FormModel> formModelList;
    FormAdapter adapter;

    TextView tempTV;
    Button confirmBtn;

    HashMap<String, Object> bodyTextHashmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enquiry_details);

        init();

        formModelList.add(new FormModel("Question", "Use curiosity, wonder, need or interest to ask rich questions"));
        formModelList.add(new FormModel("Predict", "Think about what will happen"));
        formModelList.add(new FormModel("Plan", "Identify methods and materials. Seek information"));
        formModelList.add(new FormModel("Investigate", "Observe objects, places, events. Sort, classify, compare, contrast, test. "));
        formModelList.add(new FormModel("Record", "Document observations and data from investigation"));
        formModelList.add(new FormModel("Analyze & Interpret", "Make meaning, Explain patterns in data"));
        formModelList.add(new FormModel("Connect", "Connect prior knowledge and new knowledge. Reflect on learning."));

        adapter.notifyDataSetChanged();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bodyTextHashmap = adapter.getBodyTextHashmap();
                tempTV.setText(adapter.getBodyTextHashmap().toString());

                try {
                    EnquiryProjectModel enquiryProjectModel = new EnquiryProjectModel();
                    enquiryProjectModel.setQuestion(adapter.getBodyTextHashmap().get("Question").toString());
                    enquiryProjectModel.setPredict(adapter.getBodyTextHashmap().get("Predict").toString());
                    enquiryProjectModel.setPlan(adapter.getBodyTextHashmap().get("Plan").toString());
                    enquiryProjectModel.setInvestigate(adapter.getBodyTextHashmap().get("Investigate").toString());
                    enquiryProjectModel.setRecord(adapter.getBodyTextHashmap().get("Record").toString());
                    enquiryProjectModel.setAnalyze(adapter.getBodyTextHashmap().get("Analyze & Interpret").toString());
                    enquiryProjectModel.setConnect(adapter.getBodyTextHashmap().get("Connect").toString());
                    tempTV.setText(adapter.getBodyTextHashmap().get("Connect").toString());

                    Intent mIntent = new Intent(AddEnquiryDetailsActivity.this, AddProjectActivity.class);
                    mIntent.putExtra("activity", "enquiry");
                    mIntent.putExtra("enquiryModel", enquiryProjectModel);
                    startActivity(mIntent);
                    finish();


                } catch (Exception e) {
                    Toast.makeText(AddEnquiryDetailsActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init() {
        formRV = findViewById(R.id.formRV);
        formModelList = new ArrayList<>();
        adapter = new FormAdapter(formModelList, AddEnquiryDetailsActivity.this);
        formRV.setLayoutManager(new LinearLayoutManager(this));
        formRV.setAdapter(adapter);
        tempTV = findViewById(R.id.tempTv);
        confirmBtn = findViewById(R.id.confirmBtn);

    }
}