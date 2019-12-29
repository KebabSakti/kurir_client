package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vjtechsolution.kurir.activity.BaseActivity;
import com.vjtechsolution.kurir.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    private NavController navController;
    private FirebaseAuth firebaseAuth;

    private Activity activity;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        activity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        new android.os.Handler().postDelayed(
                () -> {
                    if(firebaseUser != null) {
                        try {
                            //navController.navigate(R.id.action_splashFragment_to_homeFragment);
                            Intent intent = new Intent(activity, BaseActivity.class);
                            startActivity(intent);

                            activity.finish();
                        } catch (IllegalArgumentException e) {
                            //
                        }
                    } else {
                        try {
                            navController.navigate(R.id.action_splashFragment_to_authFragment);
                        } catch (IllegalArgumentException e) {
                            //
                        }
                    }
                }, 3000);
    }
}
