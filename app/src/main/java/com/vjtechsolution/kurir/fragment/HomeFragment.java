package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vjtechsolution.kurir.R;
import com.vjtechsolution.kurir.adapter.HomeSliderAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Context context;
    private View v = null;
    private Activity activity;
    private Boolean exit = false;
    private SliderView sliderView;
    private ScrollView scrollView;
    private View scrollbarShadow;
    private NavController navController;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient googleSignInClient;

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

        //init
        init(view);
        //firebase init
        firebaseInit();
        //slider
        startImageSlider();
    }


    private void init(View view) {
        activity = getActivity();
        context = getContext();
        v = view;
        navController = Navigation.findNavController(v);
        sliderView = v.findViewById(R.id.image_slider_container);
        scrollView = v.findViewById(R.id.home_scroll_view);
        scrollbarShadow = activity.findViewById(R.id.toolbar_shadow);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Toast.makeText(activity, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                if(exit){
                    activity.finish();
                }
                exit = true;
                exitRule();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        //add toolbar shadow on scroll
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if(scrollView != null){
                if(scrollView.getScrollY() > 0){
                    scrollbarShadow.setVisibility(View.VISIBLE);
                } else {
                    scrollbarShadow.setVisibility(View.GONE);
                }
            }
        });
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

    private void startImageSlider() {
        sliderView.setSliderAdapter(new HomeSliderAdapter(getContext()));
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(30);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.colorAccent));
        sliderView.startAutoCycle();
    }

    private void exitRule() {
        new android.os.Handler().postDelayed(
                () -> exit = false, 2000);
    }

}
