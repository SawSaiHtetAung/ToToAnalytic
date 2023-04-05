package com.safeseason.totoanalytic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    //Make global variable
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    TextView loginEmail, loginPassword;
    CheckBox rememberMe;
    private SharedPreferences.Editor loginPrefsEditor;

    // [START onactivityresult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        LoginActivity.this.finish();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }
    // [END auth_with_google]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.loginButton);
        LinearLayout googleLoginBtn = findViewById(R.id.loginGoogle);
        TextView registerBtn = findViewById(R.id.registerBtn);
        TextView recoveryBtn = findViewById(R.id.forgetPasswordBtn);
        loginEmail = findViewById(R.id.inputEmail);
        loginPassword = findViewById(R.id.inputPassword);
        rememberMe = findViewById(R.id.loginRemember);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]

        //Create firebase console
        mAuth = FirebaseAuth.getInstance();

        //Set shared Preferences
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        //Check initial value
        boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin){
            loginEmail.setText(loginPreferences.getString("emailAddress", ""));
            loginPassword.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }

        loginBtn.setOnClickListener(view -> loginUserAccount(view.getContext()));

        googleLoginBtn.setOnClickListener(view -> loginGoogleAccount());

        registerBtn.setOnClickListener(view -> {
            AlertDialog register = registerNewUser();
            register.show();
        });

        recoveryBtn.setOnClickListener(view -> {
            AlertDialog recoveryPassword = recovery();
            recoveryPassword.show();
        });
    }

    private void loginGoogleAccount() {
        Intent googleSignIn = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignIn, RC_SIGN_IN);
    }


    private void loginUserAccount(Context mContext) {
        ProgressDialog serverStatus = ProgressDialog.show(mContext, "","Connecting to server", true);
        String email, password;

        email = loginEmail.getText().toString();
        password = loginPassword.getText().toString();

        //Validation for input Email and Password
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            serverStatus.dismiss();
            return;
        }

        //SignIn existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loginPassword.setError(null);
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);

                        //Check if the remember me is checked
                        if (rememberMe.isChecked()){
                            loginPrefsEditor.putBoolean("saveLogin", true);
                            loginPrefsEditor.putString("emailAddress", email);
                            loginPrefsEditor.putString("password", password);
                            loginPrefsEditor.commit();
                        } else {
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                        }

                        serverStatus.dismiss();
                        LoginActivity.this.finish();
                    } else {
                        loginPassword.setError("Check the password");
                        serverStatus.dismiss();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private AlertDialog registerNewUser() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.register_new_user, null);
        dialogBuilder.setView(dialogView);

        //Registering resources
        Button register = dialogView.findViewById(R.id.registerButton);
        EditText getEmail = dialogView.findViewById(R.id.registerEmail);
        EditText getPassword = dialogView.findViewById(R.id.registerPassword);
        EditText getConfirmed = dialogView.findViewById(R.id.confirmedPassword);
        TextView setInformation = dialogView.findViewById(R.id.registerInformation);

        register.setOnClickListener(view -> {
            if (register.getText().equals("LOGIN")) {
                dialogBuilder.dismiss();
                return;
            }

            ProgressDialog serverStatus = ProgressDialog.show(view.getContext(), "","Register to server", true);
            String email, password, confirmed;

            email = getEmail.getText().toString();
            password = getPassword.getText().toString();
            confirmed = getConfirmed.getText().toString();

            //Validation for input Email and Password
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
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
                            Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            getEmail.setFocusable(false);
                            getPassword.setFocusable(false);
                            getConfirmed.setFocusable(false);
                            register.setText("LOGIN");
                            setInformation.setText("Registration successful, please login to continue");
                        } else {
                            Toast.makeText(LoginActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                            setInformation.setText("Registration failed, please check the input information");
                        }
                        serverStatus.dismiss();
                    });
        });

        return dialogBuilder;
    }

    private AlertDialog recovery(){
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.recovery_password, null);
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.setView(dialogView);

        Button recovery = dialogView.findViewById(R.id.recoveryButton);
        Button cancel = dialogView.findViewById(R.id.recoveryCancelButton);
        EditText email = dialogView.findViewById(R.id.recoveryEmail);
        TextView information = dialogView.findViewById(R.id.recoveryInformation);

        recovery.setOnClickListener(view -> {
            if (information.getText().toString().equals("Email successfully send")) {
                dialogBuilder.dismiss();
                return;
            }

            String recoveryEmail = email.getText().toString();
            beginRecovery(view.getContext(), recoveryEmail, information);
        });

        cancel.setOnClickListener(view -> dialogBuilder.dismiss());


        return dialogBuilder;
    }

    private void beginRecovery(Context mContext,String email, TextView title){

        if (email.isEmpty())
            return;

        ProgressDialog serverStatus = ProgressDialog.show(mContext, "","Sending email.....", true);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            serverStatus.dismiss();
            if (task.isSuccessful()){
                title.setText("Email successfully send");
            } else{
                title.setText("Can't reset with this email");
                title.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            }
        }).addOnFailureListener(e -> {
            serverStatus.dismiss();
            title.setText("Can't reset with this email");
            title.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        });
    }
}
