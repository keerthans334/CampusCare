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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class MarkRepairDoneActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSION = 2;

    private TextView tvRepairTitle;
    private TextInputEditText etTechnicianName, etTechnicianId, etCompletionNotes;
    private MaterialCardView cardImageUpload;
    private ImageView ivPreview;
    private LinearLayout layoutUploadPrompt;
    private MaterialButton btnSubmit, btnCancel;

    private String reportId;
    private String reportTitle;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_repair_done);

        initializeViews();
        handleIntentData();
        setupClickListeners();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvRepairTitle = findViewById(R.id.tvRepairTitle);
        etTechnicianName = findViewById(R.id.etTechnicianName);
        etTechnicianId = findViewById(R.id.etTechnicianId);
        etCompletionNotes = findViewById(R.id.etCompletionNotes);
        cardImageUpload = findViewById(R.id.cardImageUpload);
        ivPreview = findViewById(R.id.ivPreview);
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
            tvRepairTitle.setText("Completing: " + reportTitle);
        }
    }

    private void setupClickListeners() {
        cardImageUpload.setOnClickListener(v -> requestImagePicker());

        btnCancel.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> markAsComplete());
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

    private void markAsComplete() {
        String technicianName = etTechnicianName.getText().toString().trim();
        String technicianId = etTechnicianId.getText().toString().trim();
        String completionNotes = etCompletionNotes.getText().toString().trim();

        // Validation
        if (technicianName.isEmpty() || technicianId.isEmpty() || completionNotes.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the report status
        DataManager.getInstance().updateReportStatus(reportId, "resolved");

        Toast.makeText(this, "Repair marked as complete!", Toast.LENGTH_SHORT).show();

        // Navigate back to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
