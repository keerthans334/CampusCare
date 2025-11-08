package com.campuscare.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class SubmitActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSION = 2;

    private AutoCompleteTextView spinnerCategory, spinnerSubType;
    private TextInputEditText etTitle, etDescription, etLocation, etRollNumber;
    private MaterialCardView cardImageUpload;
    private ImageView ivPreview;
    private LinearLayout layoutUploadPrompt;
    private MaterialButton btnSubmit, btnCancel;
    private TextView tvTitle;

    private String currentCategory = "lost_found";
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        initializeViews();
        setupSpinners();
        setupClickListeners();

        // Handle intent data
        handleIntentData();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSubType = findViewById(R.id.spinnerSubType);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etRollNumber = findViewById(R.id.etRollNumber);
        cardImageUpload = findViewById(R.id.cardImageUpload);
        ivPreview = findViewById(R.id.ivPreview);
        layoutUploadPrompt = findViewById(R.id.layoutUploadPrompt);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        tvTitle = findViewById(R.id.tvTitle);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Category spinner
        String[] categories = {"Lost & Found", "Repair/Replace"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setText("Lost & Found", false);

        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            currentCategory = position == 0 ? "lost_found" : "repair_replace";
            updateSubTypeSpinner();
            updateUI();
        });

        updateSubTypeSpinner();
    }

    private void updateSubTypeSpinner() {
        String[] subTypes;
        if ("lost_found".equals(currentCategory)) {
            subTypes = new String[]{"Lost Item", "Found Item"};
        } else {
            subTypes = new String[]{"Repair Request", "Replacement Request"};
        }

        ArrayAdapter<String> subTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, subTypes);
        spinnerSubType.setAdapter(subTypeAdapter);
        spinnerSubType.setText("", false);
    }

    private void updateUI() {
        if ("lost_found".equals(currentCategory)) {
            tvTitle.setText("Lost & Found Report");
            btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
        } else {
            tvTitle.setText("Repair/Replacement Request");
            btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.accent));
        }
    }

    private void setupClickListeners() {
        cardImageUpload.setOnClickListener(v -> requestImagePicker());

        btnCancel.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type != null) {
            if ("lost_found".equals(type)) {
                currentCategory = "lost_found";
                spinnerCategory.setText("Lost & Found", false);
            } else if ("repair_replace".equals(type)) {
                currentCategory = "repair_replace";
                spinnerCategory.setText("Repair/Replace", false);
            }
            updateSubTypeSpinner();
            updateUI();
        }
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
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivPreview.setImageURI(selectedImageUri);
                ivPreview.setVisibility(View.VISIBLE);
                layoutUploadPrompt.setVisibility(View.GONE);
            }
        }
    }

    private void submitReport() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String rollNumber = etRollNumber.getText().toString().trim();
        String subType = getSubTypeValue();

        // Validation
        if (title.isEmpty() || description.isEmpty() || location.isEmpty() ||
                rollNumber.isEmpty() || subType.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidRollNumber(rollNumber)) {
            Toast.makeText(this, "Invalid roll number format (e.g., CSE2021001)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save report
        Report report = new Report(subType, title, description, location, rollNumber);
        if (selectedImageUri != null) {
            report.setImageUrl(selectedImageUri.toString());
        }

        DataManager.getInstance().addReport(report);

        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String getSubTypeValue() {
        String subTypeText = spinnerSubType.getText().toString();
        if ("Lost Item".equals(subTypeText)) return "lost";
        if ("Found Item".equals(subTypeText)) return "found";
        if ("Repair Request".equals(subTypeText)) return "repair";
        if ("Replacement Request".equals(subTypeText)) return "replace";
        return "";
    }
}
