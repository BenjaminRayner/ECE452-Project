package com.example.tripgen;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.BudgetCategoryBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    Trip trip;
    BudgetViewModel budgetViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MainActivity mainActivity;


    public CategoryAdapter(Context context, Trip trip, MainActivity mainActivity) {
        this.trip = trip;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public interface OnItemClickListener {
        void onItemClick(Budget.Category category);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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

        int percentUsed = (int)Math.round((double) total / budget * 100);

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
        text.setText(String.format("%d / %d", Math.round(total), Math.round(budget)));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Budget.Category category = Budget.Category.values()[position];

        // Bind data to the view using View Binding
        int id = 0;
        if (category.name().equals("TRANSPORTATION")) {
            id = R.drawable.transportation;
        }
        if (category.name().equals("ACCOMMODATION")) {
            id = R.drawable.accommodation;
        }
        if (category.name().equals("FOOD")) {
            id = R.drawable.food;
        }
        if (category.name().equals("ACTIVITIES")) {
            id = R.drawable.activity;
        }
        holder.binding.categoryIcon.setImageResource(id);
        setCategoryProgress(holder.binding.categoryBar, category);
        setBudgetAmounts(holder.binding.budgetText, category);

        // Set click listener for the itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(category);
                }
            }
        });

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
