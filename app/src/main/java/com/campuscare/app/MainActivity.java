package com.campuscare.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {
        MaterialButton btnSubmitReport = findViewById(R.id.btnSubmitReport);
        MaterialButton btnViewDashboard = findViewById(R.id.btnViewDashboard);
        MaterialButton btnLostFound = findViewById(R.id.btnLostFound);
        MaterialButton btnRepairReplace = findViewById(R.id.btnRepairReplace);

        btnSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubmitActivity.class);
                startActivity(intent);
            }
        });

        btnViewDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        btnLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubmitActivity.class);
                intent.putExtra("type", "lost_found");
                startActivity(intent);
            }
        });

        btnRepairReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubmitActivity.class);
                intent.putExtra("type", "repair_replace");
                startActivity(intent);
            }
        });
    }
}
