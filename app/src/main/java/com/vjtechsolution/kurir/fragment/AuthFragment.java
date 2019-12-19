package com.vjtechsolution.kurir.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vjtechsolution.kurir.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment implements View.OnClickListener {

    View v;
    Context context;
    Button login, register, facebook, google;
    NavController navController;

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        context = v.getContext();
        navController = Navigation.findNavController(v);

        login = v.findViewById(R.id.auth_login_button);
        register = v.findViewById(R.id.auth_register_button);
        facebook = v.findViewById(R.id.auth_fb_login_button);
        google = v.findViewById(R.id.auth_google_login_button);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.auth_login_button:
                navController.navigate(R.id.action_authFragment_to_loginFragment);
                break;

            case R.id.auth_register_button:
                navController.navigate(R.id.action_authFragment_to_registerFragment);
                break;
        }
    }
}
