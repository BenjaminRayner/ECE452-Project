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
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetCreationBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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

        EditText[] editTexts = {
                binding.transportationEditText,
                binding.accommodationEditText,
                binding.activityEditText,
                binding.foodEditText
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

        binding.budgetCreationButton.setOnClickListener(v -> {
            float transportationBudget = parse_value(binding.transportationEditText);
            float accommodationBudget = parse_value(binding.accommodationEditText);
            float activityBudget = parse_value(binding.activityEditText);
            float foodBudget = parse_value(binding.foodEditText);

            budgetViewModel.setBudget("Test1", transportationBudget, accommodationBudget, activityBudget, foodBudget);

            NavHostFragment.findNavController(BudgetCreationFragment.this)
                    .navigate(R.id.action_BudgetCreationFragment_to_DateFragment);

        });

        return view;
    }

    private float parse_value(EditText textObject) {
        String budgetText = textObject.getText().toString();

        float budget = 0f;
        if (!budgetText.isEmpty()) {
            budget = Float.parseFloat(budgetText);
        }

        return budget;
    }

    private void updatePieChart() {

        float transportationBudget = parse_value(binding.transportationEditText);
        float accommodationBudget = parse_value(binding.accommodationEditText);
        float activityBudget = parse_value(binding.activityEditText);
        float foodBudget = parse_value(binding.foodEditText);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(transportationBudget, "Transportation"));
        entries.add(new PieEntry(accommodationBudget, "Accommodation"));
        entries.add(new PieEntry(activityBudget, "Activity"));
        entries.add(new PieEntry(foodBudget, "Food"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLACK);

        PieChart pieChart = binding.categoryDistChart;
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
