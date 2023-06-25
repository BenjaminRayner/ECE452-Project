package com.example.tripgen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.FragmentAIChatBinding;
import com.example.tripgen.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.Iterator;

public class AIChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText inputText;
    private Button sendButton;
    private ArrayList<Message> messages = new ArrayList<>();
    private MessagesAdapter messagesAdapter = new MessagesAdapter(messages);
    private FragmentAIChatBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAIChatBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set reverse layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(messagesAdapter); // Add this line

        tempBot("initial"); // initial bot message

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new message at the end of the list
                String userMessage = binding.inputText.getText().toString();
                //print user message
                Log.d("user message", userMessage);
                if(!userMessage.isEmpty()) {
                    messages.add(new Message(userMessage,"",null, Message.TYPE_USER));
                    binding.inputText.setText("");
                    messagesAdapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(messages.size() - 1); // This will automatically scroll to the end when a new message is added
                    hideKeyboard(v);
                    tempBot("country");
                }
            }
        });

        binding.recyclerView.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 hideKeyboard(v);
                 return false;
             }
         });



       
         messagesAdapter.setOnOptionClickListener(new MessagesAdapter.OnOptionClickListener(){
                @Override
                public void onOptionClick(int position){
                    Message clickedOption = messages.get(position);
                    //print clicked option

                    //remove options
                    Iterator<Message> iter = messages.iterator();
                    while (iter.hasNext()) {
                        Message m = iter.next();
                        if (m.getType() == Message.TYPE_OPTIONS && m != clickedOption) {
                            iter.remove();
                        }
                    }
                    messagesAdapter.notifyDataSetChanged();
                    tempBot("placeToVisit");

                }
         });


        messagesAdapter.setOnPlaceClickListener(new MessagesAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(String placeName) {
                // Handle place click
                Toast.makeText(getContext(), "Place: " + placeName + " added to itinerary", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void tempBot(String value){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(value == "initial"){
                    messages.add( new Message("Hi user, please select an option below to continue","",null, Message.TYPE_AI));
                    messages.add( new Message("Discover recommended places","",null, Message.TYPE_OPTIONS));
                    messages.add( new Message("Locate nearby attractions", "",null,Message.TYPE_OPTIONS));
                    messages.add( new Message("Learn about specific place","",null, Message.TYPE_OPTIONS));
                    messagesAdapter.notifyDataSetChanged();
                }
                if(value == "placeToVisit"){
                    // Enable the keyboard after a delay
                    messages.add(new Message("Please type a city or country name","",null,Message.TYPE_AI));
                    messagesAdapter.notifyDataSetChanged();
                }
                if(value == "country" || value == "city"){
                    messages.add(new Message("Here are the recommended destinations, you can click on place to add to itinerary","",null, Message.TYPE_AI));
                    messages.add(new Message("CN Tower","Tower in Toronto,Ontario",R.drawable.cn_tower, Message.TYPE_LISTS));
                    messages.add(new Message("Casa Loma","Mansion in Toronto,Ontario",R.drawable.casa_loma, Message.TYPE_LISTS));
                    messages.add(new Message("Royal Ontario Museum","Museum in Toronto,Ontario",R.drawable.rom,Message.TYPE_LISTS));
                    messagesAdapter.notifyDataSetChanged();
                }
            }
        }, 1000); // Delay of 3 seconds


    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
