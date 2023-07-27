package com.example.tripgen;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentExpenseViewBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpenseViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentExpenseViewBinding binding;
    private BudgetViewModel budgetViewModel;
    private List<Budget.Expense> expenseList;
    private String activityID;

    ArrayList<Expense> expenses = new ArrayList<>();
    ArrayList<String> expenseIDs = new ArrayList<>();
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        binding.listView.setOnItemClickListener(this);

        ExpenseAdapter adapter = new ExpenseAdapter(requireActivity(), expenses);
        binding.listView.setAdapter(adapter);

        mainActivity = (MainActivity) requireActivity();
        mainActivity.currentExpense = null;
        db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).document(mainActivity.currentActivity).collection("Expenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                expenses.clear();
                expenseIDs.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    expenseIDs.add(document.getId());
                    expenses.add(document.toObject(Expense.class));
                }
                adapter.notifyDataSetChanged();
            }
        });


        binding.newExpenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("activityID", activityID);

                NavHostFragment.findNavController(ExpenseViewFragment.this)
                        .navigate(R.id.action_ExpenseViewFragment_to_ExpenseCreationFragment, args);
            }
        });

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.currentExpense = expenseIDs.get(position);

        Bundle args = new Bundle();
//        args.putSerializable("selectedExpense", String.valueOf(position));
        NavHostFragment.findNavController(ExpenseViewFragment.this)
                .navigate(R.id.action_ExpenseViewFragment_to_ExpenseCreationFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
