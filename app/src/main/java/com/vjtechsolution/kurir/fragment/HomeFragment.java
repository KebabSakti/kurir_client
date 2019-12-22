package com.vjtechsolution.kurir.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vjtechsolution.kurir.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        // Configure goole sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id_for_google))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Button logout = view.findViewById(R.id.auth_logout);
        logout.setOnClickListener(view1 -> {
            //firebase logout
            firebaseAuth.signOut();
            //facebook logout
            LoginManager.getInstance().logOut();
            //google logout
            googleSignInClient.signOut();

            user.unlink(user.getProviderId())
                    .addOnCompleteListener(getActivity(), task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "SUKSES UNLINK", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "GAGAL UNLINK", Toast.LENGTH_SHORT).show();
                        }
                    });

            navController.navigate(R.id.action_homeFragment_to_authFragment);
        });
    }
}
