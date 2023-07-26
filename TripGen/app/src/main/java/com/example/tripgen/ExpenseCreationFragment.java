package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentExpenseCreationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ExpenseCreationFragment extends Fragment {

    private FragmentExpenseCreationBinding binding;
    private BudgetViewModel budgetViewModel;
    private Budget.Expense selectedExpense = null;
    private String activityID = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedExpense")) {
            selectedExpense = (Budget.Expense) args.getSerializable("selectedExpense");
        }

        if (selectedExpense == null && args != null && args.containsKey("activityID")) {
            activityID = args.getString("activityID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseCreationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MainActivity mainActivity = (MainActivity) requireActivity();


        //TODO: Remove static activity ID
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());

        binding.expenseCategorySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Budget.Category.values()));


        // Modifying an existing expense
        if (mainActivity.currentExpense != null) {
            db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).document(mainActivity.currentActivity).collection("Expenses").document(mainActivity.currentExpense).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                 @Override
                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                     Expense exp = documentSnapshot.toObject(Expense.class);
                     int categoryIndex = 0;
                     if (exp.category.equals("TRANSPORTATION")) {
                        categoryIndex = 0;
                     }
                     if (exp.category.equals("ACCOMMODATION")) {
                         categoryIndex = 1;
                     }
                     if (exp.category.equals("FOOD")) {
                         categoryIndex = 2;
                     }
                     if (exp.category.equals("ACTIVITIES")) {
                         categoryIndex = 3;
                     }
                     binding.expenseCategorySpinner.setSelection(categoryIndex);
                     binding.expenseAmountInput.setText(String.format("%.2f", exp.price));
                     binding.submitExpenseButton.setText("Update Expense");

                     binding.deleteExpenseButton.setEnabled(true);
                     binding.deleteExpenseButton.setVisibility(View.VISIBLE);
                 }
             });
        } else {
            binding.deleteExpenseButton.setEnabled(false);
            binding.deleteExpenseButton.setVisibility(View.GONE);
        }

        binding.deleteExpenseButton.setOnClickListener(v -> {

            db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).document(mainActivity.currentActivity).collection("Expenses").document(mainActivity.currentExpense).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot1) {
                    Expense exp = documentSnapshot1.toObject(Expense.class);
                    db.collection("Trips").document(mainActivity.currentTrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            Trip trip = documentSnapshot2.toObject(Trip.class);
                            if (exp.category.equals("TRANSPORTATION")) {
                                trip.transportationExpenses -= exp.price;
                                db.collection("Trips").document(mainActivity.currentTrip).update("transportationExpenses", trip.transportationExpenses);
                            }
                            if (exp.category.equals("ACCOMMODATION")) {
                                trip.accommodationExpenses -= exp.price;
                                db.collection("Trips").document(mainActivity.currentTrip).update("accommodationExpenses", trip.accommodationExpenses);
                            }
                            if (exp.category.equals("FOOD")) {
                                trip.foodExpenses -= exp.price;
                                db.collection("Trips").document(mainActivity.currentTrip).update("foodExpenses", trip.foodExpenses);
                            }
                            if (exp.category.equals("ACTIVITIES")) {
                                trip.activityExpenses -= exp.price;
                                db.collection("Trips").document(mainActivity.currentTrip).update("activityExpenses", trip.activityExpenses);
                            }
                            documentSnapshot1.getReference().delete();
                        }
                    });
                }
            });

            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
        });

        binding.submitExpenseButton.setOnClickListener(v -> {
            double amount = 0f;
            String amountText = binding.expenseAmountInput.getText().toString();

            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Please input an amount", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String[] splitAmount = amountText.split("\\.");
                if (splitAmount.length >= 2 && splitAmount[1].length() > 2) {
                    Toast.makeText(requireContext(), "Please input an amount with up two decimal places", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                amount = Double.parseDouble(amountText);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Please input an valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Budget.Category category = Budget.Category.values()[binding.expenseCategorySpinner.getSelectedItemPosition()];


            Expense expense = new Expense(category.name(), amount);
            db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).document(mainActivity.currentActivity).collection("Expenses").add(expense);

            double finalAmount = amount;
            db.collection("Trips").document(mainActivity.currentTrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Trip trip = documentSnapshot.toObject(Trip.class);
                    if (category.name().equals("TRANSPORTATION")) {
                        trip.transportationExpenses += finalAmount;
                        db.collection("Trips").document(mainActivity.currentTrip).update("transportationExpenses", trip.transportationExpenses);
                    }
                    if (category.name().equals("ACCOMMODATION")) {
                        trip.accommodationExpenses += finalAmount;
                        db.collection("Trips").document(mainActivity.currentTrip).update("accommodationExpenses", trip.accommodationExpenses);
                    }
                    if (category.name().equals("FOOD")) {
                        trip.foodExpenses += finalAmount;
                        db.collection("Trips").document(mainActivity.currentTrip).update("foodExpenses", trip.foodExpenses);
                    }
                    if (category.name().equals("ACTIVITIES")) {
                        trip.activityExpenses += finalAmount;
                        db.collection("Trips").document(mainActivity.currentTrip).update("activityExpenses", trip.activityExpenses);
                    }
                }
            });

            if (mainActivity.currentExpense != null) {
                db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).document(mainActivity.currentActivity).collection("Expenses").document(mainActivity.currentExpense).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot1) {
                        Expense exp = documentSnapshot1.toObject(Expense.class);
                        db.collection("Trips").document(mainActivity.currentTrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                Trip trip = documentSnapshot2.toObject(Trip.class);
                                if (exp.category.equals("TRANSPORTATION")) {
                                    trip.transportationExpenses -= exp.price;
                                    db.collection("Trips").document(mainActivity.currentTrip).update("transportationExpenses", trip.transportationExpenses);
                                }
                                if (exp.category.equals("ACCOMMODATION")) {
                                    trip.accommodationExpenses -= exp.price;
                                    db.collection("Trips").document(mainActivity.currentTrip).update("accommodationExpenses", trip.accommodationExpenses);
                                }
                                if (exp.category.equals("FOOD")) {
                                    trip.foodExpenses -= exp.price;
                                    db.collection("Trips").document(mainActivity.currentTrip).update("foodExpenses", trip.foodExpenses);
                                }
                                if (exp.category.equals("ACTIVITIES")) {
                                    trip.activityExpenses -= exp.price;
                                    db.collection("Trips").document(mainActivity.currentTrip).update("activityExpenses", trip.activityExpenses);
                                }
                                documentSnapshot1.getReference().delete();
                            }
                        });
                    }
                });
            }

            // Updating expense
//            if (selectedExpense != null) {
//                selectedExpense = budgetViewModel.updateExpense(selectedExpense, amount, category);
//
//            } else {
//                Budget.Expense expense = new Budget.Expense(category, amount, activityID);
//                budgetViewModel.addExpense(expense);
//            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();

        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
