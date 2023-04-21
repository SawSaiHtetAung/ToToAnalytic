package com.safeseason.totoanalytic.RegisterNewUser;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.safeseason.totoanalytic.LoginActivity;
import com.safeseason.totoanalytic.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterSignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterSignUp extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;
    RegisterViewModel viewModel;
    EditText getEmail, getPassword, getConfirmed;

    public RegisterSignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterSignUp.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterSignUp newInstance(String param1, String param2) {
        RegisterSignUp fragment = new RegisterSignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_sign_up, container, false);

        //Create firebase console
        mAuth = FirebaseAuth.getInstance();

        getEmail = view.findViewById(R.id.registerEmailInput);
        getPassword = view.findViewById(R.id.registerPasswordInput);
        getConfirmed = view.findViewById(R.id.registerVerifyPasswordInput);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireParentFragment()).get(RegisterViewModel.class);

        viewModel.setSignUp().observe(getViewLifecycleOwner(), str -> {
            ProgressDialog serverStatus = ProgressDialog.show(view.getContext(), "","Register to server", true);
            String email, password, confirmed;

            email = getEmail.getText().toString();
            password = getPassword.getText().toString();
            confirmed = getConfirmed.getText().toString();

            //Get default status
            getEmail.setError(null);
            getPassword.setError(null);
            getConfirmed.setError(null);

            //Validation for input Email and Password
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                getEmail.setError("Email can't blank");
                serverStatus.dismiss();
                return;
            }

            //Check if input password and confirmed password
            if (!password.equals(confirmed)){
                getConfirmed.setError("Password does not match");
                serverStatus.dismiss();
                return;
            }

            //Create new user or register new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            viewModel.setLoginTask("Success");
                            getEmail.setFocusable(false);
                            getPassword.setFocusable(false);
                            getConfirmed.setFocusable(false);

                        } else {
                            viewModel.setLoginTask("Sign up fail");
                        }
                        serverStatus.dismiss();
                    });
        });
    }
}