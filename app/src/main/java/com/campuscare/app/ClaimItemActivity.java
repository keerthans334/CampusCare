package com.campuscare.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.campuscare.app.data.DataManager;
import com.campuscare.app.models.Report;
import com.campuscare.app.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ClaimItemActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSION = 2;

    private TextView tvItemTitle;
    private TextInputEditText etRollNumber, etDescription;
    private MaterialCardView cardIdUpload;
    private ImageView ivIdPreview;
    private LinearLayout layoutUploadPrompt;
    private MaterialButton btnSubmit, btnCancel;

    private String reportId;
    private String reportTitle;
    private Uri selectedIdCardUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_item);

        initializeViews();
        handleIntentData();
        setupClickListeners();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvItemTitle = findViewById(R.id.tvItemTitle);
        etRollNumber = findViewById(R.id.etRollNumber);
        etDescription = findViewById(R.id.etDescription);
        cardIdUpload = findViewById(R.id.cardIdUpload);
        ivIdPreview = findViewById(R.id.ivIdPreview);
        layoutUploadPrompt = findViewById(R.id.layoutUploadPrompt);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        btnBack.setOnClickListener(v -> finish());
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        reportId = intent.getStringExtra("report_id");
        reportTitle = intent.getStringExtra("report_title");

        if (reportTitle != null) {
            tvItemTitle.setText("Claiming: " + reportTitle);
        }
    }

    private void setupClickListeners() {
        cardIdUpload.setOnClickListener(v -> requestImagePicker());

        btnCancel.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitClaim());
    }

    private void requestImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedIdCardUri = data.getData();
            if (selectedIdCardUri != null) {
                ivIdPreview.setImageURI(selectedIdCardUri);
                ivIdPreview.setVisibility(View.VISIBLE);
                layoutUploadPrompt.setVisibility(View.GONE);
            }
        }
    }

    private void submitClaim() {
        String rollNumber = etRollNumber.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation
        if (rollNumber.isEmpty() || description.isEmpty() || selectedIdCardUri == null) {
            Toast.makeText(this, "Please fill in all required fields and upload your ID card",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidRollNumber(rollNumber)) {
            Toast.makeText(this, "Invalid roll number format (e.g., CSE2021001)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, you would send this to a backend for admin verification
        // For now, we'll just show a success message
        Toast.makeText(this, "Claim request submitted! Admin will verify and contact you.",
                Toast.LENGTH_LONG).show();

        // Navigate back to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
