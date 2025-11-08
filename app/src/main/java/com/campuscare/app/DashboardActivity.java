package com.campuscare.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.campuscare.app.adapters.ReportAdapter;
import com.campuscare.app.data.DataManager;
import com.campuscare.app.fragments.ReportListFragment;
import com.campuscare.app.models.Report;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements ReportAdapter.OnReportClickListener, ReportListFragment.ReportProvider {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AutoCompleteTextView spinnerStatus;
    private String currentStatusFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupViewPager();
        setupStatusFilter();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnSubmitNew = findViewById(R.id.btnSubmitNew);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnBack.setOnClickListener(v -> finish());
        btnSubmitNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubmitActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Reports");
                    break;
                case 1:
                    tab.setText("Lost & Found");
                    break;
                case 2:
                    tab.setText("Repair/Replace");
                    break;
            }
        }).attach();
    }

    private void setupStatusFilter() {
        String[] statusOptions = {"All Statuses", "Pending", "In Progress", "Resolved"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statusOptions);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setText("All Statuses", false);

        spinnerStatus.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    currentStatusFilter = "all";
                    break;
                case 1:
                    currentStatusFilter = "pending";
                    break;
                case 2:
                    currentStatusFilter = "in_progress";
                    break;
                case 3:
                    currentStatusFilter = "resolved";
                    break;
            }
            updateAllFragments();
        });
    }

    private void updateAllFragments() {
        DashboardPagerAdapter adapter = (DashboardPagerAdapter) viewPager.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                ReportListFragment fragment = adapter.getFragment(i);
                if (fragment != null) {
                    fragment.updateStatusFilter(currentStatusFilter);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAllFragments();
    }

    @Override
    public void onClaimClick(Report report) {
        Intent intent = new Intent(this, ClaimItemActivity.class);
        intent.putExtra("report_id", report.getId());
        intent.putExtra("report_title", report.getTitle());
        startActivity(intent);
    }

    @Override
    public void onMarkDoneClick(Report report) {
        Intent intent = new Intent(this, MarkRepairDoneActivity.class);
        intent.putExtra("report_id", report.getId());
        intent.putExtra("report_title", report.getTitle());
        startActivity(intent);
    }

    @Override
    public List<Report> getFilteredReports(String category, String statusFilter) {
        List<Report> allReports = DataManager.getInstance().getAllReports();
        List<Report> filteredReports = new ArrayList<>();

        for (Report report : allReports) {
            // Filter by status
            if (!"all".equals(statusFilter) && !statusFilter.equals(report.getStatus())) {
                continue;
            }

            // Filter by category
            if ("all".equals(category)) {
                filteredReports.add(report);
            } else if ("lost_found".equals(category) &&
                    ("lost".equals(report.getType()) || "found".equals(report.getType()))) {
                filteredReports.add(report);
            } else if ("repair_replace".equals(category) &&
                    ("repair".equals(report.getType()) || "replace".equals(report.getType()))) {
                filteredReports.add(report);
            }
        }

        return filteredReports;
    }

    private class DashboardPagerAdapter extends FragmentStateAdapter {
        private ReportListFragment[] fragments = new ReportListFragment[3];

        public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            String category;
            switch (position) {
                case 0:
                    category = "all";
                    break;
                case 1:
                    category = "lost_found";
                    break;
                case 2:
                    category = "repair_replace";
                    break;
                default:
                    category = "all";
            }

            ReportListFragment fragment = ReportListFragment.newInstance(category);
            fragments[position] = fragment;
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public ReportListFragment getFragment(int position) {
            return fragments[position];
        }
    }
}
