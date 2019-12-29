package com.vjtechsolution.kurir.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private RelativeLayout container;
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
        container = findViewById(R.id.toolbar_content_container);
        toolbarShadow = findViewById(R.id.toolbar_shadow);

        NavigationUI.setupWithNavController(toolbar, navController, appbarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            switch (destination.getId())
            {
                case R.id.homeFragment:
                    homeNav();
                    break;

                case R.id.orderFragment:
                    mainNav("Orderan");
                    break;

                case R.id.chatFragment:
                    mainNav("Chat");
                    break;

                case R.id.inboxFragment:
                    mainNav("Inbox");
                    break;

                case R.id.accountFragment:
                    mainNav("Akun");
                    break;

                case R.id.trackPacket:
                    secondaryNav("Lacak Paket");
                    break;
            }
        });

        trackPacket.setOnClickListener(view -> {
            navController.navigate(R.id.action_homeFragment_to_trackPacket);
        });
    }

    public void homeNav(){
        toolbar.setTitle("");
        container.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        toolbarShadow.setVisibility(View.GONE);
    }

    public void mainNav(String title){
        container.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        toolbarShadow.setVisibility(View.VISIBLE);
    }

    public void secondaryNav(String title){
        container.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        toolbarShadow.setVisibility(View.VISIBLE);
    }
}
