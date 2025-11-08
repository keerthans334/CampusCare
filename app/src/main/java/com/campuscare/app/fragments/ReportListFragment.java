package com.campuscare.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.campuscare.app.R;
import com.campuscare.app.adapters.ReportAdapter;
import com.campuscare.app.models.Report;
import java.util.List;

public class ReportListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_STATUS_FILTER = "status_filter";

    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private ReportAdapter adapter;
    private String category;
    private String statusFilter = "all";

    public static ReportListFragment newInstance(String category) {
        ReportListFragment fragment = new ReportListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter(getContext(), getFilteredReports());
        recyclerView.setAdapter(adapter);

        // Set up click listeners if parent activity implements the interface
        if (getActivity() instanceof ReportAdapter.OnReportClickListener) {
            adapter.setOnReportClickListener((ReportAdapter.OnReportClickListener) getActivity());
        }
    }

    public void updateStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
        updateReports();
    }

    public void updateReports() {
        List<Report> filteredReports = getFilteredReports();

        if (filteredReports.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.updateReports(filteredReports);
        }
    }

    private List<Report> getFilteredReports() {
        // This would typically come from a ViewModel or Repository
        // For now, we'll get it from the parent activity
        if (getActivity() instanceof ReportProvider) {
            return ((ReportProvider) getActivity()).getFilteredReports(category, statusFilter);
        }
        return java.util.Collections.emptyList();
    }

    public interface ReportProvider {
        List<Report> getFilteredReports(String category, String statusFilter);
    }
}
