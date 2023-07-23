package com.example.tripgen;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.BudgetCategoryBinding;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private BudgetViewModel budgetViewModel;


    public CategoryAdapter(Context context, BudgetViewModel budgetViewModel) {
        this.budgetViewModel = budgetViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BudgetCategoryBinding binding = BudgetCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    public void setCategoryProgress(ProgressBar progressBar, Budget.Category category) {
        int progressBarColor;
        int backgroundTintColor;

        int percentUsed = (int)Math.round(budgetViewModel.getTotal(category) / budgetViewModel.getBudget(category) * 100);

        if (percentUsed > 100) {
            progressBarColor = ContextCompat.getColor(context, R.color.progressColorOverBudget);
            backgroundTintColor = ContextCompat.getColor(context, R.color.progressBackgroundColorOverBudget);
            percentUsed -= 100;
        } else {
            progressBarColor = ContextCompat.getColor(context, R.color.progressColorUnderBudget);
            backgroundTintColor = ContextCompat.getColor(context, R.color.progressBackgroundColorUnderBudget);
        }

        progressBar.setProgressTintList(ColorStateList.valueOf(progressBarColor));
        progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(backgroundTintColor));
        progressBar.setProgress(percentUsed);
    }

    public void setBudgetAmounts(TextView text, Budget.Category category) {
        text.setText(String.format("%d / %d", Math.round(budgetViewModel.getTotal(category)), Math.round(budgetViewModel.getBudget(category))));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Budget.Category category = Budget.Category.values()[position];

        // Bind data to the view using View Binding
        holder.binding.categoryIcon.setImageResource(budgetViewModel.getIcon(category));
        setCategoryProgress(holder.binding.categoryBar, category);
        setBudgetAmounts(holder.binding.budgetText, category);
    }


    @Override
    public int getItemCount() {
        return Budget.Category.values().length;
    }

    // Use View Binding to hold the view references
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        BudgetCategoryBinding binding;

        public CategoryViewHolder(@NonNull BudgetCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
