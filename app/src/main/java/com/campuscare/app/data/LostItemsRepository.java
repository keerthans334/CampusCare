package com.campuscare.app.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.campuscare.app.models.LostItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for handling Firestore operations with real-time updates
 * Provides LiveData for observing changes in lost items collection
 */
public class LostItemsRepository {
    
    private static final String TAG = "LostItemsRepository";
    private static final String COLLECTION_LOST_ITEMS = "lost_items";
    
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    
    // LiveData for real-time updates
    private MutableLiveData<List<LostItem>> lostItemsLiveData;
    private MutableLiveData<LostItem> newItemLiveData;
    private MutableLiveData<LostItem> updatedItemLiveData;
    private MutableLiveData<String> removedItemLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> loadingLiveData;
    
    public LostItemsRepository() {
        firestore = FirebaseFirestore.getInstance();
        lostItemsLiveData = new MutableLiveData<>();
        newItemLiveData = new MutableLiveData<>();
        updatedItemLiveData = new MutableLiveData<>();
        removedItemLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
    }
    
    /**
     * Get LiveData for all lost items
     */
    public LiveData<List<LostItem>> getLostItemsLiveData() {
        return lostItemsLiveData;
    }
    
    /**
     * Get LiveData for newly added items
     */
    public LiveData<LostItem> getNewItemLiveData() {
        return newItemLiveData;
    }
    
    /**
     * Get LiveData for updated items
     */
    public LiveData<LostItem> getUpdatedItemLiveData() {
        return updatedItemLiveData;
    }
    
    /**
     * Get LiveData for removed items
     */
    public LiveData<String> getRemovedItemLiveData() {
        return removedItemLiveData;
    }
    
    /**
     * Get LiveData for errors
     */
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    
    /**
     * Get LiveData for loading state
     */
    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }
    
    /**
     * Start listening for real-time updates
     */
    public void startListening() {
        if (listenerRegistration != null) {
            return; // Already listening
        }
        
        loadingLiveData.setValue(true);
        
        listenerRegistration = firestore.collection(COLLECTION_LOST_ITEMS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                loadingLiveData.setValue(false);
                
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    errorLiveData.setValue("Failed to load data: " + e.getMessage());
                    return;
                }
                
                if (queryDocumentSnapshots != null) {
                    handleDocumentChanges(queryDocumentSnapshots.getDocumentChanges());
                    
                    // Update complete list
                    List<LostItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        LostItem item = convertDocumentToLostItem(doc);
                        if (item != null) {
                            items.add(item);
                        }
                    }
                    lostItemsLiveData.setValue(items);
                }
            });
    }
    
    /**
     * Stop listening for real-time updates
     */
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
    
    /**
     * Load lost items with filters
     */
    public void loadLostItemsWithFilter(String status, String department, String location) {
        loadingLiveData.setValue(true);
        
        Query query = firestore.collection(COLLECTION_LOST_ITEMS)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        
        // Apply filters
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            query = query.whereEqualTo("status", status);
        }
        
        if (department != null && !department.isEmpty() && !department.equals("all")) {
            query = query.whereEqualTo("department", department);
        }
        
        if (location != null && !location.isEmpty() && !location.equals("all")) {
            query = query.whereEqualTo("location", location);
        }
        
        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                loadingLiveData.setValue(false);
                List<LostItem> items = new ArrayList<>();
                
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    LostItem item = convertDocumentToLostItem(doc);
                    if (item != null) {
                        items.add(item);
                    }
                }
                
                lostItemsLiveData.setValue(items);
            })
            .addOnFailureListener(e -> {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Error loading filtered items", e);
                errorLiveData.setValue("Failed to load filtered data: " + e.getMessage());
            });
    }
    
    /**
     * Search lost items by item name or description
     */
    public void searchLostItems(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            startListening(); // Load all items
            return;
        }
        
        loadingLiveData.setValue(true);
        String query = searchQuery.toLowerCase().trim();
        
        firestore.collection(COLLECTION_LOST_ITEMS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                loadingLiveData.setValue(false);
                List<LostItem> filteredItems = new ArrayList<>();
                
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    LostItem item = convertDocumentToLostItem(doc);
                    if (item != null && matchesSearchQuery(item, query)) {
                        filteredItems.add(item);
                    }
                }
                
                lostItemsLiveData.setValue(filteredItems);
            })
            .addOnFailureListener(e -> {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Error searching items", e);
                errorLiveData.setValue("Search failed: " + e.getMessage());
            });
    }
    
    /**
     * Update item status (e.g., mark as found or claimed)
     */
    public void updateItemStatus(String itemId, String newStatus) {
        firestore.collection(COLLECTION_LOST_ITEMS)
            .document(itemId)
            .update("status", newStatus)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Item status updated successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating item status", e);
                errorLiveData.setValue("Failed to update status: " + e.getMessage());
            });
    }
    
    /**
     * Delete a lost item
     */
    public void deleteItem(String itemId) {
        firestore.collection(COLLECTION_LOST_ITEMS)
            .document(itemId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Item deleted successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting item", e);
                errorLiveData.setValue("Failed to delete item: " + e.getMessage());
            });
    }
    
    /**
     * Handle document changes for real-time updates
     */
    private void handleDocumentChanges(List<DocumentChange> documentChanges) {
        for (DocumentChange dc : documentChanges) {
            QueryDocumentSnapshot doc = dc.getDocument();
            LostItem item = convertDocumentToLostItem(doc);
            
            if (item != null) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "New item: " + item.getItemName());
                        newItemLiveData.setValue(item);
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified item: " + item.getItemName());
                        updatedItemLiveData.setValue(item);
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed item: " + item.getItemName());
                        removedItemLiveData.setValue(item.getId());
                        break;
                }
            }
        }
    }
    
    /**
     * Convert Firestore document to LostItem object
     */
    private LostItem convertDocumentToLostItem(QueryDocumentSnapshot doc) {
        try {
            LostItem item = new LostItem();
            item.setId(doc.getId());
            item.setRegistrationNumber(doc.getString("registrationNumber"));
            item.setItemName(doc.getString("itemName"));
            item.setDescription(doc.getString("description"));
            item.setLocation(doc.getString("location"));
            item.setImageUrl(doc.getString("imageUrl"));
            item.setStatus(doc.getString("status"));
            item.setReportedDate(doc.getString("reportedDate"));
            
            // Handle timestamp
            Object timestamp = doc.get("timestamp");
            if (timestamp instanceof Long) {
                item.setTimestamp((Long) timestamp);
            } else {
                item.setTimestamp(System.currentTimeMillis());
            }
            
            // Handle extracted registration details
            Object year = doc.get("year");
            if (year instanceof Long) {
                item.setYear(((Long) year).intValue());
            }
            
            Object semester = doc.get("semester");
            if (semester instanceof Long) {
                item.setSemester(((Long) semester).intValue());
            }
            
            item.setDepartment(doc.getString("department"));
            
            Object rollNumber = doc.get("rollNumber");
            if (rollNumber instanceof Long) {
                item.setRollNumber(((Long) rollNumber).intValue());
            }
            
            return item;
        } catch (Exception e) {
            Log.e(TAG, "Error converting document to LostItem", e);
            return null;
        }
    }
    
    /**
     * Check if item matches search query
     */
    private boolean matchesSearchQuery(LostItem item, String query) {
        return (item.getItemName() != null && item.getItemName().toLowerCase().contains(query)) ||
               (item.getDescription() != null && item.getDescription().toLowerCase().contains(query)) ||
               (item.getLocation() != null && item.getLocation().toLowerCase().contains(query)) ||
               (item.getRegistrationNumber() != null && item.getRegistrationNumber().toLowerCase().contains(query)) ||
               (item.getDepartment() != null && item.getDepartment().toLowerCase().contains(query));
    }
}
