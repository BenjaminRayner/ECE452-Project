package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.tripgen.databinding.FragmentDateBinding;

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
                Toast.makeText(requireContext(), "" + dates[i], Toast.LENGTH_SHORT).show();
//                NavHostFragment.findNavController(DateFragment.this)
//                        .navigate(R.id.action_DateFragment_to_TripFragment);
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
