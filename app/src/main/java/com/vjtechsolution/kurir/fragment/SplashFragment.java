package com.vjtechsolution.kurir.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vjtechsolution.kurir.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    View v;
    NavController navController;

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
        v = view;
        navController = Navigation.findNavController(v);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        navController.navigate(R.id.action_splashFragment_to_authFragment);
                    }
                }, 2000);
    }
}
