package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class TripFragment extends Fragment {

    ListView listView;
    String[] trips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        listView = view.findViewById(R.id.list_view);
        trips = getResources().getStringArray(R.array.trips);

        ListAdapter adapter = new ListAdapter(requireActivity(), trips);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavHostFragment.findNavController(TripFragment.this)
                        .navigate(R.id.action_TripFragment_to_DateFragment);
            }
        });

        return view;
    }
}
