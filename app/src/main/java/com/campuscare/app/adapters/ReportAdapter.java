package com.campuscare.app.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.campuscare.app.R;
import com.campuscare.app.models.Report;
import com.campuscare.app.utils.Utils;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> reports;
    private Context context;
    private OnReportClickListener listener;

    public interface OnReportClickListener {
        void onClaimClick(Report report);
        void onMarkDoneClick(Report report);
    }

    public ReportAdapter(Context context, List<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    public void setOnReportClickListener(OnReportClickListener listener) {
        this.listener = listener;
    }

    public void updateReports(List<Report> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivTypeIcon;
        private TextView tvTypeBadge, tvStatusBadge, tvTitle, tvDescription;
        private TextView tvLocation, tvDate, tvRollNumber;
        private MaterialButton btnAction;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            tvTypeBadge = itemView.findViewById(R.id.tvTypeBadge);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRollNumber = itemView.findViewById(R.id.tvRollNumber);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Report report) {
            // Set icon based on type
            if ("lost".equals(report.getType()) || "found".equals(report.getType())) {
                ivTypeIcon.setImageResource(R.drawable.ic_search);
            } else {
                ivTypeIcon.setImageResource(R.drawable.ic_repair);
            }

            // Set type badge
            tvTypeBadge.setText(report.getTypeDisplayText());
            setBackgroundTint(tvTypeBadge, Utils.getTypeColor(report.getType()));

            // Set status badge
            tvStatusBadge.setText(report.getStatusDisplayText());
            setBackgroundTint(tvStatusBadge, Utils.getStatusColor(report.getStatus()));

            // Set content
            tvTitle.setText(report.getTitle());
            tvDescription.setText(report.getDescription());
            tvLocation.setText(report.getLocation());
            tvDate.setText(Utils.formatDate(report.getDate()));
            tvRollNumber.setText("Roll: " + report.getRollNumber());

            // Set action button
            setupActionButton(report);
        }

        private void setupActionButton(Report report) {
            boolean showButton = false;
            String buttonText = "";
            View.OnClickListener clickListener = null;

            if (("lost".equals(report.getType()) || "found".equals(report.getType()))
                    && "pending".equals(report.getStatus())) {
                showButton = true;
                buttonText = "Claim Item";
                clickListener = v -> {
                    if (listener != null) {
                        listener.onClaimClick(report);
                    }
                };
            } else if (("repair".equals(report.getType()) || "replace".equals(report.getType()))
                    && "in_progress".equals(report.getStatus())) {
                showButton = true;
                buttonText = "Mark as Complete";
                clickListener = v -> {
                    if (listener != null) {
                        listener.onMarkDoneClick(report);
                    }
                };
                btnAction.setBackgroundTintList(
                        ContextCompat.getColorStateList(context, R.color.success));
            }

            if (showButton) {
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText(buttonText);
                btnAction.setOnClickListener(clickListener);
            } else {
                btnAction.setVisibility(View.GONE);
            }
        }

        private void setBackgroundTint(TextView textView, int color) {
            GradientDrawable background = (GradientDrawable) textView.getBackground().mutate();
            background.setColor(color);
        }
    }
}
