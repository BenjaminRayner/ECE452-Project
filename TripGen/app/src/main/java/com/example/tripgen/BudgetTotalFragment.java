package com.example.tripgen;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetCreationBinding;
import com.example.tripgen.databinding.FragmentBudgetTotalBinding;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BudgetTotalFragment extends Fragment {

    private FragmentBudgetTotalBinding binding;
    private BudgetViewModel budgetViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetTotalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        PieChart pieChart = binding.categoryDistChart;
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(64f);
        pieChart.setRotationEnabled(false);

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());



        int numCategories = Budget.Category.values().length;

        double[] totals = new double[numCategories];
        double[] budgets = new double[numCategories];
        for (int i=0; i<numCategories; i++) {
            Budget.Category category = Budget.Category.values()[i];
            totals[i] = budgetViewModel.getTotal(category);
            budgets[i] = budgetViewModel.getBudget(category);
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
                textViews[i].setTextColor(Color.GREEN);
            }
            textViews[i].setText(String.format("%.2f", totals[i]) + "/" + String.format("%.2f", budgets[i]));
        }



        binding.editBudgetFab.setOnClickListener(v -> {
            NavHostFragment.findNavController(BudgetTotalFragment.this)
                    .navigate(R.id.action_BudgetTotalFragment_to_BudgetCreationFragment);
        });

        return view;
    }


    private void updatePieChart(double[] totals) {
        PieChart pieChart = binding.categoryDistChart;

        int numCategories = Budget.Category.values().length;
        List<PieEntry> entries = new ArrayList<>();
        for (int i=0; i<numCategories; i++) {
            Budget.Category category = Budget.Category.values()[i];
            entries.add(new PieEntry((float)totals[i], category.toString()));
        }

        int[] chartColors = new int[]{
                Color.parseColor("#00BFFF"),  // Orange for Transportation
                Color.parseColor("#FFA500"),  // Deep Sky Blue for Accommodation
                Color.parseColor("#FF0000"),  // Red for Activity
                Color.parseColor("#32CD32")   // Lime Green for Food
        };

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(chartColors);
        dataSet.setSliceSpace(2f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (Float.compare(value, 0f) != 0) {
                    return String.format("%.2f", value);
                } else {
                    return ""; // Hide label for values equal to 0
                }
            }
        });

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
