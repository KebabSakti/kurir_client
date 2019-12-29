package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vjtechsolution.kurir.R;
import com.vjtechsolution.kurir.api.AuthApi;
import com.vjtechsolution.kurir.model.Customer;
import com.vjtechsolution.kurir.network.ApiClient;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View v;
    private Context context;
    private Activity activity;
    private Button submit, resendCode;
    private EditText phone;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private NavController navController;
    private CompositeDisposable disposable;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    private static final String TAG = "firebase phone";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        context = v.getContext();
        activity = getActivity();
        navController = Navigation.findNavController(view);
        disposable = new CompositeDisposable();
        dialogBuilder = new AlertDialog.Builder(activity);

        phone = view.findViewById(R.id.phone_number);
        submit = view.findViewById(R.id.sent_register);
        progressBar = view.findViewById(R.id.progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("id");

        submit.setOnClickListener(view1 -> {
            //show progress
            showProgress();
            //sent code
            validatePhone();
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 9) {
                    submit.setBackgroundResource(R.drawable.bottom_button_enable);
                    submit.setEnabled(true);
                }else {
                    submit.setBackgroundResource(R.drawable.bottom_button_disable);
                    submit.setEnabled(false);
                }
            }
        });
    }

    private void showProgress() {
        submit.setEnabled(false);
        submit.setBackgroundResource(R.drawable.bottom_button_disable);
        submit.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        submit.setEnabled(true);
        submit.setBackgroundResource(R.drawable.bottom_button_enable);
        submit.setText(R.string.kirim);
        progressBar.setVisibility(View.GONE);
    }

    private void validatePhone() {
        final String NETWORK_TAG = "okhttp";
        AuthApi authApi = ApiClient.getClient(context).create(AuthApi.class);
        disposable.add(
                //get phone number
                authApi.getCustomer("62"+phone.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Customer>() {
                            @Override
                            public void onSuccess(Customer customer) {
                                if(customer.getName() != null){
                                    sentCodeToPhone();
                                } else {
                                    hideProgress();
                                    dialogBuilder
                                            .setTitle("Error")
                                            .setMessage("Nomor ini belum terdaftar, anda harus mendaftar terlebih dahulu atau gunakan metode lain untuk login");
                                    alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideProgress();

                                Log.d(NETWORK_TAG, e.getLocalizedMessage());
                                dialogBuilder
                                        .setTitle("Error")
                                        .setMessage("Koneksi bermasalah, coba beberapa saat lagi");
                                alertDialog = dialogBuilder.create();
                                alertDialog.show();
                            }
                        })
        );
    }

    private void sentCodeToPhone() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62"+phone.getText().toString(),
                60,
                TimeUnit.SECONDS,
                activity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationComplete");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d(TAG, "onVerificationFailed:" + e.getLocalizedMessage());
                        hideProgress();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId);

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        //navigate to validation fragment with vervication id
                        Bundle bundle = new Bundle();
                        bundle.putString("verification_id", verificationId);
                        bundle.putString("phone", "+62"+phone.getText().toString());
                        navController.navigate(R.id.action_loginFragment_to_validationFragment, bundle);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
