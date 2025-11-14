package com.campuscare.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        testFirebaseConnection();
    }

    private void testFirebaseConnection() {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Log.d("MainActivity", "Firebase initialized successfully");
            Toast.makeText(this, "Firebase connected successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MainActivity", "Firebase initialization failed", e);
            Toast.makeText(this, "Firebase connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                // Direct to SubmitItemActivity for testing
                Intent intent = new Intent(MainActivity.this, SubmitItemActivity.class);
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
