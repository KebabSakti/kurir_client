package com.vjtechsolution.kurir.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.vjtechsolution.kurir.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() == R.id.splashFragment || destination.getId() == R.id.authFragment) {
                toolbar.setVisibility(View.GONE);
                //bottomNavigationView.setVisibility(View.GONE);
            } else if(destination.getId() == R.id.homeFragment){
                toolbar.setVisibility(View.GONE);
                //show bottom nav only
            } else if(destination.getId() == R.id.validationFragment){
                toolbar.setVisibility(View.VISIBLE);
                //show toolbar only
            } else {
                toolbar.setVisibility(View.VISIBLE);
                //show both
            }
        });
    }
}
