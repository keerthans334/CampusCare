package com.campuscare.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.campuscare.app.R;
import com.campuscare.app.models.LostItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying lost items from Firestore with real-time updates
 * Shows item name, location, date, and status indicator using CardView layout
 */
public class LostItemsAdapter extends RecyclerView.Adapter<LostItemsAdapter.LostItemViewHolder> {
    
    private Context context;
    private List<LostItem> lostItems;
    private OnItemClickListener onItemClickListener;
    
    // Date formatters
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    public interface OnItemClickListener {
        void onItemClick(LostItem item, int position);
        void onItemLongClick(LostItem item, int position);
    }
    
    public LostItemsAdapter(Context context) {
        this.context = context;
        this.lostItems = new ArrayList<>();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lost_report, parent, false);
        return new LostItemViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LostItemViewHolder holder, int position) {
        LostItem item = lostItems.get(position);
        holder.bind(item, position);
    }
    
    @Override
    public int getItemCount() {
        return lostItems.size();
    }
    
    /**
     * Update the entire list of lost items (for initial load)
     */
    public void updateItems(List<LostItem> newItems) {
        this.lostItems.clear();
        this.lostItems.addAll(newItems);
        notifyDataSetChanged();
    }
    
    /**
     * Add a new item to the list (for real-time additions)
     */
    public void addItem(LostItem item) {
        lostItems.add(0, item); // Add to top
        notifyItemInserted(0);
    }
    
    /**
     * Update an existing item (for real-time updates)
     */
    public void updateItem(LostItem updatedItem) {
        for (int i = 0; i < lostItems.size(); i++) {
            if (lostItems.get(i).getId().equals(updatedItem.getId())) {
                lostItems.set(i, updatedItem);
                notifyItemChanged(i);
                break;
            }
        }
    }
    
    /**
     * Remove an item from the list (for real-time deletions)
     */
    public void removeItem(String itemId) {
        for (int i = 0; i < lostItems.size(); i++) {
            if (lostItems.get(i).getId().equals(itemId)) {
                lostItems.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
    
    /**
     * Get item at specific position
     */
    public LostItem getItem(int position) {
        if (position >= 0 && position < lostItems.size()) {
            return lostItems.get(position);
        }
        return null;
    }
    
    /**
     * Clear all items
     */
    public void clearItems() {
        int size = lostItems.size();
        lostItems.clear();
        notifyItemRangeRemoved(0, size);
    }
    
    class LostItemViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView ivItemImage;
        private TextView tvItemName;
        private TextView tvLocation;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvStatus;
        private View statusIndicator;
        private TextView tvRegistrationNumber;
        private TextView tvDepartment;
        
        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            tvRegistrationNumber = itemView.findViewById(R.id.tv_registration_number);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(lostItems.get(position), position);
                    }
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemLongClick(lostItems.get(position), position);
                        return true;
                    }
                }
                return false;
            });
        }
        
        public void bind(LostItem item, int position) {
            // Set item name
            tvItemName.setText(item.getItemName());
            
            // Set location with icon
            tvLocation.setText("📍 " + item.getLocation());
            
            // Format and set date and time
            try {
                Date date = inputDateFormat.parse(item.getReportedDate());
                if (date != null) {
                    tvDate.setText(outputDateFormat.format(date));
                    tvTime.setText(timeFormat.format(date));
                }
            } catch (ParseException e) {
                tvDate.setText("Unknown date");
                tvTime.setText("");
            }
            
            // Set status with appropriate styling
            setStatusStyling(item.getStatus());
            
            // Set registration number
            tvRegistrationNumber.setText(item.getRegistrationNumber());
            
            // Set department
            tvDepartment.setText(item.getDepartment());
            
            // Load image with Glide
            loadItemImage(item.getImageUrl());
        }
        
        private void setStatusStyling(String status) {
            tvStatus.setText(status.toUpperCase());
            
            int statusColor;
            int backgroundColor;
            
            switch (status.toLowerCase()) {
                case "lost":
                    statusColor = context.getColor(R.color.status_lost);
                    backgroundColor = context.getColor(R.color.status_lost_background);
                    break;
                case "found":
                    statusColor = context.getColor(R.color.status_found);
                    backgroundColor = context.getColor(R.color.status_found_background);
                    break;
                case "claimed":
                    statusColor = context.getColor(R.color.status_claimed);
                    backgroundColor = context.getColor(R.color.status_claimed_background);
                    break;
                default:
                    statusColor = context.getColor(R.color.text_secondary);
                    backgroundColor = context.getColor(R.color.background_secondary);
                    break;
            }
            
            tvStatus.setTextColor(statusColor);
            statusIndicator.setBackgroundColor(statusColor);
            tvStatus.setBackgroundColor(backgroundColor);
        }
        
        private void loadItemImage(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_upload)
                    .error(R.drawable.ic_upload)
                    .into(ivItemImage);
            } else {
                // Set default image
                ivItemImage.setImageResource(R.drawable.ic_upload);
            }
        }
    }
}
