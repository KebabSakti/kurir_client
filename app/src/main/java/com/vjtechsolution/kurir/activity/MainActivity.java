package com.vjtechsolution.kurir.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vjtechsolution.kurir.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.splashFragment,
                R.id.authFragment,
                R.id.testingFragment,
                R.id.testingOrderFragment,
                R.id.testingChatFragment
                /*
                R.id.homeFragment,
                R.id.orderFragment,
                R.id.inboxFragment,
                R.id.accountFragment*/
        ).build();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() == R.id.splashFragment || destination.getId() == R.id.authFragment) {
                //hide toolbar and navbar
                toolbar.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
            } else if(destination.getId() == R.id.testingFragment){
                //show bottom nav only
                toolbar.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else if(destination.getId() == R.id.validationFragment || destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment){
                toolbar.setVisibility(View.VISIBLE);
                //show toolbar only
               bottomNavigationView.setVisibility(View.GONE);
            } else {
                //toolbar.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }
}
