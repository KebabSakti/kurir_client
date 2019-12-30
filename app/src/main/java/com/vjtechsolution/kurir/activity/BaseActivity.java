package com.vjtechsolution.kurir.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vjtechsolution.kurir.R;

public class BaseActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appbarConfiguration;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private TextView trackPacket;
    private ConstraintLayout container;
    private View toolbarShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        navController = Navigation.findNavController(this, R.id.second_nav_host_fragment);
        appbarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.orderFragment,
                R.id.chatFragment,
                R.id.inboxFragment,
                R.id.accountFragment
        ).build();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);
        trackPacket = findViewById(R.id.track_packet);
        container = findViewById(R.id.home_toolbar);
        toolbarShadow = findViewById(R.id.toolbar_shadow);

        NavigationUI.setupWithNavController(toolbar, navController, appbarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() == R.id.homeFragment){
                mainNav();
            } else if(destination.getId() == R.id.trackPacket) {
                childNav();
            } else {
                secondaryNav();
            }
        });

        trackPacket.setOnClickListener(view -> navController.navigate(R.id.action_homeFragment_to_trackPacket));
    }

    public void mainNav(){
        toolbar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        toolbarShadow.setVisibility(View.INVISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void secondaryNav(){
        toolbar.setVisibility(View.VISIBLE);
        toolbarShadow.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    public void childNav(){
        toolbar.setVisibility(View.VISIBLE);
        toolbarShadow.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }
}
