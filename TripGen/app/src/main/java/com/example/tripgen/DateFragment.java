package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class DateFragment extends Fragment {

    ListView listView;
    String[] dates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        listView = view.findViewById(R.id.list_view);
        dates = getResources().getStringArray(R.array.dates);

        ListAdapter adapter = new ListAdapter(requireActivity(), dates);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(requireContext(), "" + dates[i], Toast.LENGTH_SHORT).show();
//                NavHostFragment.findNavController(DateFragment.this)
//                        .navigate(R.id.action_DateFragment_to_TripFragment);
            }
        });

        return view;
    }
}
