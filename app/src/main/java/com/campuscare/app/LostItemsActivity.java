package com.campuscare.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscare.app.adapters.LostItemsAdapter;
import com.campuscare.app.data.LostItemsRepository;
import com.campuscare.app.models.LostItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity to display list of lost items with real-time updates from Firestore
 * Uses RecyclerView with LostItemsAdapter and implements search functionality
 */
public class LostItemsActivity extends AppCompatActivity implements LostItemsAdapter.OnItemClickListener {
    
    private static final String TAG = "LostItemsActivity";
    
    // UI Components
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddItem;
    private SearchView searchView;
    
    // Adapter and Repository
    private LostItemsAdapter adapter;
    private LostItemsRepository repository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_items);
        
        initializeViews();
        setupRecyclerView();
        setupRepository();
        observeData();
        setupClickListeners();
        
        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lost Items");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Start listening for real-time updates
        repository.startListening();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_lost_items);
        progressBar = findViewById(R.id.progress_bar);
        fabAddItem = findViewById(R.id.fab_add_item);
    }
    
    private void setupRecyclerView() {
        adapter = new LostItemsAdapter(this);
        adapter.setOnItemClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
    
    private void setupRepository() {
        repository = new LostItemsRepository();
    }
    
    private void observeData() {
        // Observe complete list updates
        repository.getLostItemsLiveData().observe(this, lostItems -> {
            if (lostItems != null) {
                adapter.updateItems(lostItems);
                updateEmptyState(lostItems.isEmpty());
            }
        });
        
        // Observe new items (for real-time additions)
        repository.getNewItemLiveData().observe(this, newItem -> {
            if (newItem != null) {
                // Item is already handled in the complete list update
                // This can be used for showing notifications or animations
                showToast("New item reported: " + newItem.getItemName());
            }
        });
        
        // Observe item updates
        repository.getUpdatedItemLiveData().observe(this, updatedItem -> {
            if (updatedItem != null) {
                adapter.updateItem(updatedItem);
                showToast("Item updated: " + updatedItem.getItemName());
            }
        });
        
        // Observe item removals
        repository.getRemovedItemLiveData().observe(this, removedItemId -> {
            if (removedItemId != null) {
                adapter.removeItem(removedItemId);
                showToast("Item removed");
            }
        });
        
        // Observe loading state
        repository.getLoadingLiveData().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // Observe errors
        repository.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                showToast("Error: " + error);
            }
        });
    }
    
    private void setupClickListeners() {
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubmitItemActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lost_items, menu);
        
        // Setup search view
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        
        if (searchView != null) {
            searchView.setQueryHint("Search items...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.trim().isEmpty()) {
                        repository.startListening(); // Show all items
                    } else {
                        performSearch(newText);
                    }
                    return true;
                }
            });
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (itemId == R.id.action_refresh) {
            refreshData();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onItemClick(LostItem item, int position) {
        // Handle item click - navigate to item details
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }
    
    @Override
    public void onItemLongClick(LostItem item, int position) {
        // Handle long press - show context menu
        showItemContextMenu(item, position);
    }
    
    private void performSearch(String query) {
        repository.searchLostItems(query);
    }
    
    private void showFilterDialog() {
        // TODO: Implement filter dialog
        // For now, just show a toast
        showToast("Filter functionality coming soon!");
    }
    
    private void refreshData() {
        repository.stopListening();
        repository.startListening();
    }
    
    private void showItemContextMenu(LostItem item, int position) {
        // TODO: Implement context menu for item actions
        // (Mark as found, Edit, Delete, etc.)
        showToast("Long pressed: " + item.getItemName());
    }
    
    private void updateEmptyState(boolean isEmpty) {
        // TODO: Show/hide empty state view
        if (isEmpty) {
            // Could show an empty state layout
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening to prevent memory leaks
        if (repository != null) {
            repository.stopListening();
        }
    }
}
