package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentDateBinding;
import com.example.tripgen.databinding.FragmentItineraryBinding;


public class ItineraryFragment extends Fragment {

    private FragmentItineraryBinding binding;
    private static final Event[] EVENTS = {
            new Event(R.drawable.cn_tower, "Event 1", "Description 1", "1:00 PM", "2:00 PM"),
            new Event(R.drawable.cn_tower, "Event 2", "Description 2", "3:00 PM", "5:00 PM"),
            new Event(R.drawable.cn_tower, "Event 3", "Description 3", "9:00 PM", "10:00 PM")
    };

    private Event[] events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItineraryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        events = EVENTS;

        EventAdapter adapter = new EventAdapter(requireActivity(), events);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavHostFragment.findNavController(ItineraryFragment.this)
                        .navigate(R.id.action_ItineraryFragment_to_thirdFragment);
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
