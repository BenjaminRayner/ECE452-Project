package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentTripBinding;

public class TripFragment extends Fragment {

    private FragmentTripBinding binding;
    private String[] trips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        trips = getResources().getStringArray(R.array.trips);

        ListAdapter adapter = new ListAdapter(requireActivity(), trips);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavHostFragment.findNavController(TripFragment.this)
                        .navigate(R.id.action_TripFragment_to_DateFragment);
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
