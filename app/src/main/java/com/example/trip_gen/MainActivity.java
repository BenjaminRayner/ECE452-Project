package com.example.trip_gen;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trip_gen.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Use toolbar as actionbar
        setSupportActionBar(binding.toolbar);

//        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    // If user is already logged in, skip login page
    @Override
    public void onStart()
    {
        super.onStart();

        // Get firebase authentication instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_LoginFragment_to_TripListFragment);
        }
    }

    // Adds items to the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Actionbar item events
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        // Go back to login page on logout
        if (id == R.id.action_logout) {
            auth.signOut();
            Navigation.findNavController(this, R.id.nav_host_fragment).popBackStack(R.id.LoginFragment, false);
        }

        return super.onOptionsItemSelected(item);
    }

}