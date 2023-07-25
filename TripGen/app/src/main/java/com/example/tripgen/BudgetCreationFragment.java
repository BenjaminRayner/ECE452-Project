package com.example.tripgen;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetCreationBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class BudgetCreationFragment extends Fragment {

    private FragmentBudgetCreationBinding binding;
    private BudgetViewModel budgetViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetCreationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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


        EditText[] editTexts = {
                binding.transportationEditText,
                binding.accommodationEditText,
                binding.foodEditText,
                binding.activityEditText
        };

        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePieChart();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());

        // Must be editing budget
        if (budgetViewModel.validBudget()) {
            binding.transportationEditText.setText(Integer.toString(budgetViewModel.getBudget(Budget.Category.TRANSPORTATION)));
            binding.accommodationEditText.setText(Integer.toString(budgetViewModel.getBudget(Budget.Category.ACCOMMODATION)));
            binding.foodEditText.setText(Integer.toString(budgetViewModel.getBudget(Budget.Category.FOOD)));
            binding.activityEditText.setText(Integer.toString(budgetViewModel.getBudget(Budget.Category.ACTIVITIES)));

            binding.budgetCreationButton.setText(R.string.updateBudgetButton);
        }

        updatePieChart();

        binding.budgetCreationButton.setOnClickListener(v -> {
            int transportationBudget = parse_value(binding.transportationEditText);
            int accommodationBudget = parse_value(binding.accommodationEditText);
            int foodBudget = parse_value(binding.foodEditText);
            int activityBudget = parse_value(binding.activityEditText);

            // Must be updating budget
            if (budgetViewModel.validBudget()) {
                budgetViewModel.updateBudget(transportationBudget, accommodationBudget, activityBudget, foodBudget);

                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
            } else {
                // TODO: Remove static trip ID
                budgetViewModel.createBudget("Test1", transportationBudget, accommodationBudget, activityBudget, foodBudget);
                NavHostFragment.findNavController(BudgetCreationFragment.this)
                        .navigate(R.id.action_BudgetCreationFragment_to_DateFragment);
            }
        });

        return view;
    }

    private int parse_value(EditText textObject) {
        String budgetText = textObject.getText().toString();

        int budget = 0;
        if (!budgetText.isEmpty()) {
            budget = Integer.parseInt(budgetText);
        }

        return budget;
    }

    private void updatePieChart() {

        int transportationBudget = parse_value(binding.transportationEditText);
        int accommodationBudget = parse_value(binding.accommodationEditText);
        int activityBudget = parse_value(binding.activityEditText);
        int foodBudget = parse_value(binding.foodEditText);

        PieChart pieChart = binding.categoryDistChart;

        int[] chartColors;
        List<PieEntry> entries = new ArrayList<>();

        if (transportationBudget == 0 && accommodationBudget == 0 && activityBudget == 0 && foodBudget == 0) {
            chartColors = new int[]{
                    Color.parseColor("#808080")
            };

            entries.add(new PieEntry(100f, "Empty Budget"));
        } else {
            chartColors = new int[]{
                    Color.parseColor("#00BFFF"),
                    Color.parseColor("#FFA500"),
                    Color.parseColor("#FF0000"),
                    Color.parseColor("#32CD32")
            };

            if (transportationBudget > 0) entries.add(new PieEntry(transportationBudget, "Transportation"));
            if (accommodationBudget > 0) entries.add(new PieEntry(accommodationBudget, "Accommodation"));
            if (activityBudget > 0) entries.add(new PieEntry(activityBudget, "Activity"));
            if (foodBudget > 0) entries.add(new PieEntry(foodBudget, "Food"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(chartColors);
        dataSet.setSliceSpace(2f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (Float.compare(value, 0f) != 0) {
                    return String.valueOf((int) value);
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
