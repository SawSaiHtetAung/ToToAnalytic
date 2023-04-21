package com.safeseason.totoanalytic.RegisterNewUser;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.safeseason.totoanalytic.R;

public class RegisterMain extends DialogFragment{

    public int BACKSTACK_COUNT = 1;
    Button next, cancel;
    RegisterViewModel viewModel;

    //Set window to fixed on 90%
    @Override
    public void onResume() {
        Window window = getDialog().getWindow();;
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_main, container, false);

        cancel = view.findViewById(R.id.registerCancelBtn);
        next = view.findViewById(R.id.registerConfirmedBtn);

        //Start initial state
        next.setEnabled(false);

        next.setOnClickListener(view1 -> {
            BACKSTACK_COUNT++;
            if (BACKSTACK_COUNT >= 4)
                dismiss();
            registerWizard(BACKSTACK_COUNT);
        });

        cancel.setOnClickListener(view12 -> {
            BACKSTACK_COUNT--;
            registerWizard(BACKSTACK_COUNT);
        });

        return view;
    }

    private void registerWizard(int step) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (step){
            case 0:
            case 1:
                dismiss();
                break;
            case 2:
                RegisterSignUp signUp = new RegisterSignUp();
                transaction.replace(R.id.registerContainerView, signUp, "Register");
                transaction.commitAllowingStateLoss();
                break;
            case 3:
                viewModel.signUpClick();
                BACKSTACK_COUNT--;
                viewModel.checkLogin().observe(getViewLifecycleOwner(), reason ->{
                    if (TextUtils.equals(reason, "Success")){
                        RegisterFinished finished = new RegisterFinished();
                        transaction.replace(R.id.registerContainerView, finished, "Register");
                        transaction.commitAllowingStateLoss();
                        cancel.setEnabled(false);
                        next.setText("FINISHED");
                        BACKSTACK_COUNT++;
                    } else
                        Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
                });

                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);


        viewModel.userAgreementRet().observe(getViewLifecycleOwner(), list ->{
            next.setEnabled(Boolean.TRUE.equals(viewModel.userAgreementRet().getValue()));
        });
    }
}
