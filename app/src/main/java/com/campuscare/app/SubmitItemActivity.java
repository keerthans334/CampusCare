package com.campuscare.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.campuscare.app.utils.RegistrationNumberValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SubmitItemActivity extends AppCompatActivity {
    
    private static final String TAG = "SubmitItemActivity";
    private static final int IMAGE_PICK_REQUEST = 1000;
    
    // UI Components
    private TextInputLayout tilRegistrationNumber;
    private TextInputLayout tilItemName;
    private TextInputLayout tilDescription;
    private TextInputLayout tilLocation;
    private TextInputEditText etRegistrationNumber;
    private TextInputEditText etItemName;
    private TextInputEditText etDescription;
    private AutoCompleteTextView actvLocation;
    private ImageView ivItemImage;
    private Button btnSelectImage;
    private Button btnSubmitItem;
    private ProgressBar progressBar;
    
    // Firebase
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    
    // Data
    private Uri selectedImageUri;
    private String[] campusLocations = {
        "Main Block - Ground Floor",
        "Main Block - First Floor", 
        "Main Block - Second Floor",
        "Library - Reading Hall",
        "Library - Computer Section",
        "Cafeteria - Dining Area",
        "Cafeteria - Kitchen Area",
        "Sports Complex - Gym",
        "Sports Complex - Basketball Court",
        "Sports Complex - Football Field",
        "Hostel - Block A",
        "Hostel - Block B", 
        "Hostel - Block C",
        "Parking Area - Two Wheeler",
        "Parking Area - Four Wheeler",
        "Laboratory - Computer Lab 1",
        "Laboratory - Computer Lab 2",
        "Laboratory - Physics Lab",
        "Laboratory - Chemistry Lab",
        "Administrative Block",
        "Auditorium",
        "Garden Area",
        "Bus Stop"
    };
    
    // Activity result launcher for image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    
    // Permission launcher
    private ActivityResultLauncher<String> permissionLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_item);
        
        initializeViews();
        initializeFirebase();
        initializeLaunchers();
        setupLocationDropdown();
        setupValidation();
        setupClickListeners();
        
        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Submit Lost Item");
        }
    }
    
    private void initializeViews() {
        tilRegistrationNumber = findViewById(R.id.til_registration_number);
        tilItemName = findViewById(R.id.til_item_name);
        tilDescription = findViewById(R.id.til_description);
        tilLocation = findViewById(R.id.til_location);
        etRegistrationNumber = findViewById(R.id.et_registration_number);
        etItemName = findViewById(R.id.et_item_name);
        etDescription = findViewById(R.id.et_description);
        actvLocation = findViewById(R.id.actv_location);
        ivItemImage = findViewById(R.id.iv_item_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSubmitItem = findViewById(R.id.btn_submit_item);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initializeFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    
    private void initializeLaunchers() {
        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage();
                }
            }
        );
        
        // Permission launcher
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void setupLocationDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_dropdown_item_1line, 
            campusLocations
        );
        actvLocation.setAdapter(adapter);
    }
    
    private void setupValidation() {
        // Registration number validation
        etRegistrationNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                validateRegistrationNumber(s.toString());
            }
        });
        
        // Item name validation
        etItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                validateItemName(s.toString());
            }
        });
    }
    
    private void validateRegistrationNumber(String registrationNumber) {
        RegistrationNumberValidator.ValidationResult result = 
            RegistrationNumberValidator.validate(registrationNumber);
        
        if (registrationNumber.isEmpty()) {
            tilRegistrationNumber.setError(null);
            tilRegistrationNumber.setHelperText("Format: 20231CSE0503");
        } else if (!result.isValid()) {
            tilRegistrationNumber.setError(result.getMessage());
            tilRegistrationNumber.setHelperText(null);
        } else {
            tilRegistrationNumber.setError(null);
            tilRegistrationNumber.setHelperText("✓ Valid registration number");
        }
    }
    
    private void validateItemName(String itemName) {
        if (itemName.isEmpty()) {
            tilItemName.setError(null);
        } else if (itemName.length() < 3) {
            tilItemName.setError("Item name must be at least 3 characters");
        } else if (itemName.length() > 50) {
            tilItemName.setError("Item name cannot exceed 50 characters");
        } else {
            tilItemName.setError(null);
        }
    }
    
    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> checkPermissionAndOpenImagePicker());
        btnSubmitItem.setOnClickListener(v -> submitItem());
    }
    
    private void checkPermissionAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            Glide.with(this)
                .load(selectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_upload)
                .into(ivItemImage);
            
            btnSelectImage.setText("Change Image");
            ivItemImage.setVisibility(View.VISIBLE);
        }
    }
    
    private void submitItem() {
        if (!validateForm()) {
            return;
        }
        
        showProgress(true);
        
        if (selectedImageUri != null) {
            uploadImageAndSubmitItem();
        } else {
            submitItemToFirestore(null);
        }
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate registration number
        String regNumber = etRegistrationNumber.getText().toString().trim();
        RegistrationNumberValidator.ValidationResult regResult = 
            RegistrationNumberValidator.validate(regNumber);
        if (!regResult.isValid()) {
            tilRegistrationNumber.setError(regResult.getMessage());
            isValid = false;
        }
        
        // Validate item name
        String itemName = etItemName.getText().toString().trim();
        if (itemName.isEmpty()) {
            tilItemName.setError("Item name is required");
            isValid = false;
        } else if (itemName.length() < 3) {
            tilItemName.setError("Item name must be at least 3 characters");
            isValid = false;
        }
        
        // Validate description
        String description = etDescription.getText().toString().trim();
        if (description.isEmpty()) {
            tilDescription.setError("Description is required");
            isValid = false;
        } else if (description.length() < 10) {
            tilDescription.setError("Description must be at least 10 characters");
            isValid = false;
        }
        
        // Validate location
        String location = actvLocation.getText().toString().trim();
        if (location.isEmpty()) {
            tilLocation.setError("Location is required");
            isValid = false;
        }
        
        return isValid;
    }
    
    private void uploadImageAndSubmitItem() {
        String fileName = "lost_items/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);
        
        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener(taskSnapshot -> 
                imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Log.d(TAG, "Image uploaded successfully: " + uri.toString());
                        submitItemToFirestore(uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL", e);
                        showProgress(false);
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    })
            )
            .addOnFailureListener(e -> {
                Log.e(TAG, "Image upload failed", e);
                showProgress(false);
                Toast.makeText(this, "Image upload failed: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void submitItemToFirestore(String imageUrl) {
        String registrationNumber = etRegistrationNumber.getText().toString().trim().toUpperCase();
        String itemName = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = actvLocation.getText().toString().trim();
        
        // Create item data
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("registrationNumber", registrationNumber);
        itemData.put("itemName", itemName);
        itemData.put("description", description);
        itemData.put("location", location);
        itemData.put("imageUrl", imageUrl);
        itemData.put("status", "lost");
        itemData.put("reportedDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        itemData.put("timestamp", System.currentTimeMillis());
        
        // Add extracted registration details
        itemData.put("year", RegistrationNumberValidator.extractYear(registrationNumber));
        itemData.put("semester", RegistrationNumberValidator.extractSemester(registrationNumber));
        itemData.put("department", RegistrationNumberValidator.extractDepartment(registrationNumber));
        itemData.put("rollNumber", RegistrationNumberValidator.extractRollNumber(registrationNumber));
        
        // Generate document ID
        String documentId = UUID.randomUUID().toString();
        
        firestore.collection("lost_items")
            .document(documentId)
            .set(itemData)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Item submitted successfully");
                showProgress(false);
                Toast.makeText(this, "Item submitted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to submit item", e);
                showProgress(false);
                Toast.makeText(this, "Failed to submit item: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSubmitItem.setEnabled(!show);
        btnSelectImage.setEnabled(!show);
        
        if (show) {
            btnSubmitItem.setText("Submitting...");
        } else {
            btnSubmitItem.setText("Submit Item");
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
