package com.example.tripgen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.FragmentAIChatBinding;
import com.example.tripgen.databinding.FragmentFirstBinding;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AIChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText inputText;
    private Button sendButton;
    private ArrayList<Message> messages = new ArrayList<>();
    private MessagesAdapter messagesAdapter = new MessagesAdapter(messages);
    private FragmentAIChatBinding binding;

    private int optionSelected;

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
        OpenAiApi chatbot =  new OpenAiApi();
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(messagesAdapter); // Add this line

        tempBot("initial"); // initial bot message

        GoogleApi googleApi = new GoogleApi(getActivity());

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new message at the end of the list
                String userMessage = binding.inputText.getText().toString();
                //print user message
                Log.d("user message", userMessage);

                if(optionSelected == 1){ // Recommended places
                    Log.d("OptionSelected","1");
                }
                if(optionSelected == 2){ // Locate Nearby Attractions (No need to talk to chat gpt)
                    Log.d("OptionSelected","2");

                    return;
                }
                if(optionSelected == 3){ // Discover Places
                    Log.d("OptionSelected","3");
                }
                if(!userMessage.isEmpty() && optionSelected == 1) {
                    messages.add(new Message(userMessage,"",null, Message.TYPE_USER));
                    binding.inputText.setText("");
                    messagesAdapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(messages.size() - 1); // This will automatically scroll to the end when a new message is added
                    hideKeyboard(v);
                    chatbot.getRecommendedPlaces(userMessage).thenAccept(placeList -> {
                        for (String place : placeList) {


                            googleApi.getPictureOfLocationToManipulate(place, new GoogleApi.FetchPictureCallback() {
                                @Override
                                public void onPictureFetched(Bitmap bitmap) {
                                    // Do whatever you need with the bitmap here
                                    messages.add(new Message(place,"",bitmap,Message.TYPE_LISTS));
                                    Log.d("Debug", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

                                    // Notify the adapter on the main/UI thread
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        activity.runOnUiThread(() -> {
                                            messagesAdapter.notifyDataSetChanged();
                                            binding.recyclerView.smoothScrollToPosition(messages.size() - 1); // This will automatically scroll to the end when a new message is added
                                        });
                                    }
                                }

                                @Override
                                public void onFetchFailure(Exception exception) {
                                    // Handle the exception here
                                    Log.d("Debug", "Error while getting the image");
                                }
                            });
                        }
                    });

                }

                if(!userMessage.isEmpty() && optionSelected == 3) {
                    messages.add(new Message(userMessage,"",null, Message.TYPE_USER));
                    binding.inputText.setText("");
                    messagesAdapter.notifyDataSetChanged();
                    binding.recyclerView.smoothScrollToPosition(messages.size() - 1); // This will automatically scroll to the end when a new message is added
                    hideKeyboard(v);
                    chatbot.getPlaceDetail(userMessage).thenAccept(details -> {
                        // This code will be executed when the CompletableFuture returned by getPlaceDetails is completed
                        // You can use the list of places here, for example, you could display it in your app's UI

                        Log.d("OpenAiApi", "Recommended place: " + details);
                        messages.add(new Message(details,"",null,Message.TYPE_AI));


                        // Notify the adapter on the main/UI thread
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
                        }
                    });
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
                optionSelected = position;

                if(position == 2){
                    googleApi.makeNearbyPlaceRequestToManipulate(new GoogleApi.FetchPlaceArrayListCallback(){

                        @Override
                        public void onPlaceArrayListFetched(ArrayList<Place> places){
                            for (Place place : places) {

                                String placeName = place.getName();

                                googleApi.getPictureOfLocationToManipulate(placeName, new GoogleApi.FetchPictureCallback() {
                                    @Override
                                    public void onPictureFetched(Bitmap bitmap) {
                                        // Do whatever you need with the bitmap here
                                        messages.add(new Message(placeName,"",bitmap,Message.TYPE_LISTS));
                                        Log.d("Debug", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

                                        // Notify the adapter on the main/UI thread
                                        Activity activity = getActivity();
                                        if (activity != null) {
                                            activity.runOnUiThread(() -> {
                                                messagesAdapter.notifyDataSetChanged();
                                                binding.recyclerView.smoothScrollToPosition(messages.size() - 1); // This will automatically scroll to the end when a new message is added
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFetchFailure(Exception exception) {
                                        // Handle the exception here
                                        Log.d("Debug", "Error while getting the image");
                                    }
                                });
                                Log.i("Place", "Nearby-Place Name: " + place.getName());
                                Log.i("Place", "Nearby-Place Address: " + place.getAddress());
                            }
                        }
                        @Override
                        public void onFetchPlaceFailure(Exception exception){
                            Log.d("Debug", "Error while getting near by locations");

                        }

                    });
                }else {

                    //remove options
//                    Iterator<Message> iter = messages.iterator();
//                    while (iter.hasNext()) {
//                        Message m = iter.next();
//                        if (m.getType() == Message.TYPE_OPTIONS && m != clickedOption) {
//                            iter.remove();
//                        }
//                    }
                    messagesAdapter.notifyDataSetChanged();
                    tempBot("placeToVisit");
                }

            }
        });


        messagesAdapter.setOnPlaceClickListener(new MessagesAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(String placeName) {
                // Handle place click
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int startTimeHour = hourOfDay;
                        int startTimeMin = minute;
                        // End Time dialog

                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int endTimeHour = hourOfDay;
                                int endTimeMin = minute;
                                // add activity to itinerary
                                System.out.println(startTimeHour); // start time hour
                                System.out.println(startTimeMin); // start time min
                                System.out.println(endTimeHour); // end time hour
                                System.out.println(endTimeMin); // end time min
                                Toast.makeText(getContext(), "Place: " + placeName + " added to itinerary", Toast.LENGTH_SHORT).show();
//                                FirebaseDatabase.getInstance().getReference().child("Places").child(placeName).setValue(true);
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, startTimeHour, startTimeMin, true);
                        timePickerDialog.setTitle("Select End Time");
                        timePickerDialog.show();
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, 0, 0, true);
                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();


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
                if(value == "placeToVisit" && optionSelected == 1){
                    // Enable the keyboard after a delay
                    messages.add(new Message("Please type a city or country name","",null,Message.TYPE_AI));
                    messagesAdapter.notifyDataSetChanged();
                }
                if(value == "placeToVisit" && optionSelected == 3){
                    // Enable the keyboard after a delay
                    messages.add(new Message("Please type a question","",null,Message.TYPE_AI));
                    messagesAdapter.notifyDataSetChanged();
                }
                if(value == "country" || value == "city"){
                    messages.add(new Message("Here are the recommended destinations, you can click on place to add to itinerary","",null, Message.TYPE_AI));
//                    messages.add(new Message("CN Tower","Tower in Toronto,Ontario",R.drawable.cn_tower, Message.TYPE_LISTS));
//                    messages.add(new Message("Casa Loma","Mansion in Toronto,Ontario",R.drawable.casa_loma, Message.TYPE_LISTS));
//                    messages.add(new Message("Royal Ontario Museum","Museum in Toronto,Ontario",R.drawable.rom,Message.TYPE_LISTS));
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