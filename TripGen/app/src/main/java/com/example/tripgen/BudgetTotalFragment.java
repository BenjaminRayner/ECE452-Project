package com.example.tripgen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetTotalBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class BudgetTotalFragment extends Fragment {

    private FragmentBudgetTotalBinding binding;
//    private BudgetViewModel budgetViewModel;
    private ListenerRegistration listenerRegistration;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetTotalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        PieChart pieChart = binding.categoryDistChart;
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(64f);
        pieChart.setRotationEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(15f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(true);

        MainActivity mainActivity = (MainActivity) requireActivity();
        ListenerRegistration listenerRegistration =  db.collection("Trips").document(mainActivity.currentTrip).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Trip trip = value.toObject(Trip.class);

                int numCategories = Budget.Category.values().length;

                double[] totals = new double[numCategories];
                int [] budgets = new int[numCategories];
                for (int i=0; i<numCategories; i++) {
                    Budget.Category category = Budget.Category.values()[i];
                    int total = 0;
                    int budget = 0;
                    if (category.name().equals("TRANSPORTATION")) {
                        total = trip.transportationExpenses;
                        budget = trip.transportationBudget;
                    }
                    if (category.name().equals("ACCOMMODATION")) {
                        total = trip.accommodationExpenses;
                        budget = trip.accommodationBudget;
                    }
                    if (category.name().equals("FOOD")) {
                        total = trip.foodExpenses;
                        budget = trip.foodBudget;
                    }
                    if (category.name().equals("ACTIVITIES")) {
                        total = trip.activityExpenses;
                        budget = trip.activityBudget;
                    }
                    totals[i] = total;
                    budgets[i] = budget;
                }

                updatePieChart(totals);


                TextView[] textViews = {
                        binding.transportationText,
                        binding.accommodationText,
                        binding.activityText,
                        binding.foodText
                };

                for (int i=0; i<textViews.length; i++) {
                    if (totals[i] > budgets[i]) {
                        textViews[i].setTextColor(Color.RED);
                    } else {
                        textViews[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.underBudgetText));
                    }


                    String formattedTotal;
                    if ((totals[i] * 100) % 100 != 0) {
                        formattedTotal = String.format("%.2f", totals[i]);
                    } else {
                        formattedTotal = String.format("%.0f", totals[i]);
                    }
                    textViews[i].setText(formattedTotal + " / " + Integer.toString(budgets[i]));
                }
            }
        });

        binding.editBudgetFab.setOnClickListener(v -> {
            listenerRegistration.remove();
            NavHostFragment.findNavController(BudgetTotalFragment.this)
                    .navigate(R.id.action_BudgetTotalFragment_to_BudgetCreationFragment);
        });



        return view;
    }


    private void updatePieChart(double[] totals) {
        PieChart pieChart = binding.categoryDistChart;

        int numCategories = Budget.Category.values().length;
        List<PieEntry> entries = new ArrayList<>();
        int[] chartColors;


        boolean nothingSpent = true;

        for (int i=0; i<totals.length; i++) {
            if (totals[i] != 0) {
                nothingSpent = false;
            }
        }


        if (nothingSpent) {
            chartColors = new int[]{
                    Color.parseColor("#808080")
            };

            entries.add(new PieEntry(100f, "No Expenses"));
        } else {
            chartColors = new int[]{
                    Color.parseColor("#00BFFF"),
                    Color.parseColor("#FFA500"),
                    Color.parseColor("#FF3F3B"),
                    Color.parseColor("#32CD32")
            };

            for (int i=0; i<numCategories; i++) {
                Budget.Category category = Budget.Category.values()[i];
                if (totals[i] > 0) entries.add(new PieEntry((float)totals[i], category.toString()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(chartColors);
        dataSet.setSliceSpace(2f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (Float.compare(value, 0f) != 0) {
                    return String.format("%.0f", value);
                } else {
                    return ""; // Hide label for values equal to 0
                }
            }
        });

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(15f);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
