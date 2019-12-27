package com.vjtechsolution.kurir.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vjtechsolution.kurir.R;
import com.vjtechsolution.kurir.util.PrefUtil;

import java.util.List;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.rx.ObservableFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private View v;
    private Context context;
    private Activity activity;
    private Button login, register, facebook, google;
    private ProgressBar gProgress, fProgress;
    private LoginButton facebookGone;
    private NavController navController;

    //Auth Object
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser, prevUser;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private CompositeDisposable disposable;
    private static final int google_intent_rc = 1;

    private static final String TAG = "firebase";

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

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
        activity = getActivity();
        navController = Navigation.findNavController(v);

        //bind view etc
        initView();
        //auth
        initAuth();
        //check permission
        checkPermission();
    }

    private void checkPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(context, permissions)){
            Log.d("permission", "Permission already accepted");
            getLocation();
        } else {
            EasyPermissions.requestPermissions(activity, "Permission", 1, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("permission", "Permission Granted :"+perms);
        getLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("permission", "Permission Denied :"+perms);

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void initView() {
        login = v.findViewById(R.id.auth_login_button);
        register = v.findViewById(R.id.auth_register_button);
        facebook = v.findViewById(R.id.auth_fb_login_button);
        facebookGone = v.findViewById(R.id.auth_fb_login_button_gone);
        google = v.findViewById(R.id.auth_google_login_button);
        gProgress = v.findViewById(R.id.g_progress);
        fProgress = v.findViewById(R.id.f_progress);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        facebook.setOnClickListener(this);
        google.setOnClickListener(this);

        builder = new AlertDialog.Builder(activity);
        disposable = new CompositeDisposable();
    }

    private void initAuth() {
        //facebook auth init
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        facebookGone.setPermissions("email", "public_profile", "user_friends");
        facebookGone.setFragment(this);
        facebookGone.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                hideFProgress();
            }

            @Override
            public void onError(FacebookException error) {
                hideFProgress();
                showLoginErrorDialog("");
            }
        });

        // Configure goole sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id_for_google))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
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

            case R.id.auth_fb_login_button:
                showFProgress();

                facebookGone.performClick();
                break;

            case R.id.auth_google_login_button:
                showGProgress();

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, google_intent_rc);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == google_intent_rc) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if(googleSignInAccount != null) {
                    firebaseAuthWithGoogle(googleSignInAccount);
                }

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                if(e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_FAILED){
                    showLoginErrorDialog("");
                }

                hideGProgress();
            }
        }

        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if(task.isSuccessful()){
                        // Sign in success, update UI with the signed-in user's information
                        currentUser = task.getResult().getUser();

                        loginSuccess();
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception exception = task.getException();
                        if(exception instanceof FirebaseAuthUserCollisionException){
                            showLoginErrorDialog("");
                        }

                        //facebook logout
                        loginManager.logOut();

                        hideFProgress();
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        currentUser = task.getResult().getUser();

                        loginSuccess();
                    } else {
                        // If sign in fails, display a message to the user.
                        //showLoginErrorDialog(task.getException().getLocalizedMessage());
                        showLoginErrorDialog("");
                    }
                });
    }

    private void loginSuccess() {
        navController.navigate(R.id.action_authFragment_to_homeFragment);
    }

    private void getLocation() {
        LocationParams.Builder locationConfig = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(0)
                .setInterval(1000);
        SmartLocation.LocationControl smartLocation = SmartLocation.with(context)
                .location()
                .continuous()
                .config(locationConfig.build());

        Observable<Location> locationObservable = ObservableFactory.from(smartLocation);
        disposable.add(
                locationObservable
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribeWith(new DisposableObserver<Location>() {
                            @Override
                            public void onNext(Location location) {
                                Log.d("location_update", location.getLatitude() +", "+ location.getLongitude());

                                SmartLocation.with(context).geocoding()
                                        .reverse(location, (location1, list) -> {
                                            for (Address address : list) {
                                                Log.d("location_update", address.getAddressLine(0));
                                                Log.d("location_update", address.getCountryName());
                                                Log.d("location_update", address.getLocality());
                                                Log.d("location_update", address.getSubAdminArea());

                                                PrefUtil.storeCustomerCity(context, address.getSubAdminArea());
                                            }
                                        });
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("location_update", e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.d("location_update", "COMPLETE");
                            }
                        })
        );
    }

    private void showLoginErrorDialog(String msg) {
        msg = msg.equals("") ? "Cek kondisi jaringan anda atau coba gunakan akun dan metode lain untuk login":msg;
        builder
             .setTitle("Login Gagal")
             .setMessage(msg);
        dialog = builder.create();
        dialog.show();
    }

    private void showGProgress() {
        google.setEnabled(false);
        google.setText("");
        gProgress.setVisibility(View.VISIBLE);
    }

    private void hideGProgress() {
        google.setEnabled(true);
        google.setText(R.string.login_menggunakan_google);
        gProgress.setVisibility(View.GONE);
    }

    private void showFProgress() {
        facebook.setEnabled(false);
        facebook.setText("");
        fProgress.setVisibility(View.VISIBLE);
    }

    private void hideFProgress() {
        facebook.setEnabled(true);
        facebook.setText(R.string.login_menggunakan_facebook);
        fProgress.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
