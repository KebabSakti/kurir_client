package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vjtechsolution.kurir.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ValidationFragment extends Fragment {

    private View v;
    private Context context;
    private Activity activity;
    private Button resendValidation;
    private EditText code;

    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private PhoneAuthProvider.ForceResendingToken token;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;

    private static final String TAG = "firebase";
    private String verficationId;
    private String phone;

    public ValidationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init view
        init(view);
        //start coundown for code resend to phone
        initCountdown();
    }

    private void init(View view) {
        v = view;
        context = getContext();
        activity = getActivity();
        resendValidation = view.findViewById(R.id.validation_resend);
        code = view.findViewById(R.id.validation_code);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        dialogBuilder = new AlertDialog.Builder(activity);

        verficationId = getArguments().getString("verification_id");
        phone = getArguments().getString("phone");

        resendValidation.setOnClickListener(view1 -> {
            //resend code
            resendValidationCode();
            //restart timer
            resendCodeNotReady();
        });

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 6) {
                    //firebase login with phone number
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verficationId, code.getText().toString());
                    firebaseAuthWithPhone(phoneAuthCredential);
                }
            }
        });
    }

    private void initCountdown() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendValidation.setText("00:"+ String.format((Locale) null, "%02d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                resendCodeReady();
            }
        }.start();
    }

    private void resendCodeReady() {
        resendValidation.setBackgroundResource(R.drawable.bottom_button_enable);
        resendValidation.setText(R.string.kirim_ulang);
        resendValidation.setEnabled(true);
    }

    private void resendCodeNotReady() {
        initCountdown();
        resendValidation.setBackgroundResource(R.drawable.bottom_button_disable);
        resendValidation.setEnabled(false);
    }

    private void resendValidationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
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
                    }

                    @Override
                    public void onCodeSent(@NonNull String verId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken newToken) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verId);

                        // Save verification ID and resending token so we can use them later
                        verficationId = verId;
                        token = newToken;
                    }
                });
    }

    private void firebaseAuthWithPhone(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(context, "logged in user id: "+user.getUid(), Toast.LENGTH_LONG).show();

                        navController.navigate(R.id.action_validationFragment_to_homeFragment);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            dialogBuilder
                                    .setTitle("Error")
                                    .setMessage("Kode yang anda masukkan salah, masukkan kode yang dikirimkan ke anda atau kirim ulang kode untuk mendapatkan kode yang baru");
                            dialog = dialogBuilder.create();
                            dialog.show();
                        }
                    }
                });
    }
}
