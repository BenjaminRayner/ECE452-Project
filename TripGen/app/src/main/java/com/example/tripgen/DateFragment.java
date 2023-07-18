package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentDateBinding;

//TODO: Override back button to always go back to TripFragment
//TODO: After creation of a new trip, currently it goes back to TripCreationFragment

public class DateFragment extends Fragment {

    private FragmentDateBinding binding;
    private String[] dates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        dates = getResources().getStringArray(R.array.dates);

        ListAdapter adapter = new ListAdapter(requireActivity(), dates);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavHostFragment.findNavController(DateFragment.this)
                        .navigate(R.id.action_DateFragment_to_ItineraryFragment);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
