package com.vjtechsolution.kurir.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vjtechsolution.kurir.R;
import com.vjtechsolution.kurir.api.AuthApi;
import com.vjtechsolution.kurir.model.Customer;
import com.vjtechsolution.kurir.network.ApiClient;
import com.vjtechsolution.kurir.util.AlertUtil;
import com.vjtechsolution.kurir.util.PrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private View v;
    private Context context;
    private Activity activity;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private NavController navController;
    private AuthApi authApi;
    private CompositeDisposable disposable;
    private AlertUtil alertUtil;

    private EditText textName, textEmail, textPhone;
    private RadioGroup radioSex;
    private RadioButton selectedSex;
    private Button btnRegister;
    private ProgressBar progressBar;
    private List<EditText> editTexts = new ArrayList<>();

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    private static final String TAG = "okhttp";
    private String sex = "Pria";

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        init(view);
    }

    private void init(View view) {
        v = view;
        context = getContext();
        activity = getActivity();
        dialogBuilder  = new AlertDialog.Builder(activity);
        navController = Navigation.findNavController(view);
        disposable = new CompositeDisposable();
        authApi = ApiClient.getClient(context).create(AuthApi.class);

        textName = view.findViewById(R.id.nama_lengkap);
        textPhone = view.findViewById(R.id.phone_number);
        textEmail = view.findViewById(R.id.email);
        radioSex = view.findViewById(R.id.jenis_kelamin);
        btnRegister = view.findViewById(R.id.sent_register);
        progressBar = view.findViewById(R.id.progress);

        //add edittext to list for validation
        editTexts.add(textName);
        editTexts.add(textPhone);

        radioSex.setOnCheckedChangeListener((radioGroup, i) -> {
            selectedSex = view.findViewById(i);
            sex = selectedSex.getText().toString();
        });

        btnRegister.setOnClickListener(view1 -> {
            showProgress();

            //validate input
            if(validateInput()) {
                validateCustomer("62"+textPhone.getText().toString());
            }
        });
    }

    private boolean validateInput() {
        boolean status = true;
        for (EditText input:editTexts){
            if(input.getText().toString().equals("")){
                hideProgress();

                input.requestFocus();
                input.setError("Field tidak boleh kosong");
                status = false;
                break;
            }
        }

        return status;
    }

    private void validateCustomer(String keyword) {
        disposable.add(
                authApi
                    .getCustomer(keyword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Customer>() {
                        @Override
                        public void onSuccess(Customer customer) {
                            if(customer.getName() == null){
                                //add new customer to database
                                //addCustomer();
                                sentCodeToPhone();
                            } else {
                                hideProgress();
                                //alert("Nomor Sudah Terdaftar", "Gunakan nomor lain atau login menggunakan nomor ini");
                                textPhone.requestFocus();
                                textPhone.setError("Nomor sudah terdaftar, gunakan nomor lain atau login menggunakan nomor ini");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgress();
                            alert("Error", e.getLocalizedMessage());
                        }
                    })
        );
    }

    private void addCustomer() {
        disposable.add(
                authApi
                    .newCustomer(textName.getText().toString(), sex, "62"+textPhone.getText().toString(), PrefUtil.getCustomerPref(context, "CUSTOMER_CITY"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Customer>() {
                        @Override
                        public void onSuccess(Customer customer) {
                            if(customer.getId() != null){
                                sentCodeToPhone();
                            } else {
                                hideProgress();
                                alert("Gagal Menambah Kustomer", "Mohon coba beberapa saat lagi");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            //alertUtil.alert(activity, "Error", e.getLocalizedMessage());
                        }
                    })
        );
    }

    private void sentCodeToPhone() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62"+textPhone.getText().toString(),
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
                        alert("Error", e.getLocalizedMessage());
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
                        bundle.putString("phone", "+62"+textPhone.getText().toString());
                        navController.navigate(R.id.action_registerFragment_to_validationFragment, bundle);
                    }
                });
    }

    private void showProgress() {
        btnRegister.setEnabled(false);
        btnRegister.setBackgroundResource(R.drawable.bottom_button_disable);
        btnRegister.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        btnRegister.setEnabled(true);
        btnRegister.setBackgroundResource(R.drawable.bottom_button_enable);
        btnRegister.setText(R.string.daftar);
        progressBar.setVisibility(View.GONE);
    }

    public void alert(String title, String msg) {
        dialogBuilder
                .setTitle(title)
                .setMessage(msg);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
