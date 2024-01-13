package com.example.GuildWakayama2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.GuildWakayama2.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplication(), FirebaseUIActivity.class);
        startActivity(intent);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // NavHostFragmentを取得
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_mypage)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);

        // NavControllerを取得
        NavController navController = navHostFragment.getNavController();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}