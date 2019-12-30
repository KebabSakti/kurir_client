package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vjtechsolution.kurir.R;
import com.vjtechsolution.kurir.activity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private View v;
    private Context context;
    private Activity activity;
    private Button logout;
    private NavController navController;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient googleSignInClient;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        init(view);
        //firebase init
        firebaseInit();
    }

    private void init(View view) {
        v = view;
        context = view.getContext();
        activity = getActivity();
        logout = view.findViewById(R.id.auth_logout);

        //logout user
        logout.setOnClickListener(view1 -> logout());
    }

    private void firebaseInit() {
        // Configure goole sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id_for_google))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(v.getContext(), gso);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

    private void logout() {
        //firebase logout
        firebaseAuth.signOut();
        //facebook logout
        LoginManager.getInstance().logOut();
        //google logout
        googleSignInClient.signOut();

        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);

        activity.finish();
    }
}
