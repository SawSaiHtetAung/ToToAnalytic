package com.safeseason.totoanalytic.Helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.safeseason.totoanalytic.R;

public class PurchaseDialog extends Dialog implements View.OnClickListener {
    public Context mContext;
    public PurchaseDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.proversion_warning);
        MaterialButton confirm = findViewById(R.id.confirmBtn);

        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
