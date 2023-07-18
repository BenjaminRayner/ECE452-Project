package com.example.tripgen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentDateBinding;
import com.example.tripgen.databinding.FragmentItineraryBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ItineraryFragment extends Fragment {

    private FragmentItineraryBinding binding;
    private static final Event[] EVENTS = {
            new Event(R.drawable.cn_tower, "Event 1", "Description 1", "1:00 PM", "2:00 PM"),
            new Event(R.drawable.cn_tower, "Event 2", "Description 2", "3:00 PM", "5:00 PM"),
            new Event(R.drawable.cn_tower, "Event 3", "Description 3", "9:00 PM", "10:00 PM")
    };

    private Event[] events;
    String [] location_names = {"CN Tower", "Casa Loma", "Royal Ontario Museum", "Ripley's Aquarium"};
    int [] location_images = {R.drawable.cn_tower, R.drawable.casa_loma, R.drawable.rom, R.drawable.ripleys};

    List<String> choosen_location_names  = new ArrayList<String>();

    List<Integer> choosen_location_images = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItineraryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        events = EVENTS;

//        EventAdapter adapter = new EventAdapter(requireActivity(), events);
//        binding.listView.setAdapter(adapter);


        ListView listViewMenu = (ListView) view.findViewById(R.id.listViewChoose);
        ProgramAdapter programAdapter = new ProgramAdapter(getActivity(), location_names, location_images);
        listViewMenu.setAdapter(programAdapter);

        ListView listViewChoosen = (ListView) view.findViewById(R.id.list_view_display);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, programAdapter.getPlace());
        listViewChoosen.setAdapter(adapter1);

        //Keep listview synced with firebase DB
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Places");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                programAdapter.choosen_location_names.add(snapshot.getKey());
                adapter1.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                programAdapter.choosen_location_names.remove(snapshot.getKey());
                adapter1.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        listViewChoosen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("Clicked: ", programAdapter.getPlace().get(position));
//            }
//        });

        listViewChoosen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavHostFragment.findNavController(ItineraryFragment.this)
                        .navigate(R.id.action_ItineraryFragment_to_thirdFragment);
            }
        });
        //Remove list item when held down
        listViewChoosen.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseDatabase.getInstance().getReference().child("Places").child(programAdapter.choosen_location_names.get(position)).removeValue();
                return false;
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
