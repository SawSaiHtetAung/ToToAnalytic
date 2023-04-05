package com.safeseason.totoanalytic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.safeseason.totoanalytic.Helper.DataProcessing;
import com.safeseason.totoanalytic.Helper.UpdateSetChecked;
import com.safeseason.totoanalytic.Menu.ConfigLoadAdaptor;
import com.safeseason.totoanalytic.SubDisplay.AnalysisPage;
import com.safeseason.totoanalytic.SubDisplay.DisplayInformation;
import com.safeseason.totoanalytic.SubDisplay.MainPage;
import com.safeseason.totoanalytic.SubDisplay.SearchPage;
import com.safeseason.totoanalytic.SubDisplay.SuggestPage;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, UpdateSetChecked {
    // Set global variable
    MaterialCardView mainBt, searchBt, analysisBt, suggestBt;
    MaterialCardView[] button = new MaterialCardView[5];
    ImageView advancedBtn, exitBtn, menuBtn;
    DrawerLayout menuDrawer;
    NavigationView menuNav;
    ProgressDialog firebaseLoading;
    int buttonID = 5, tempButtonID = 5;
    boolean checkDouble = false;

    //To get data from server
    FirebaseDatabase totoResultDatabase;
    DatabaseReference databaseReference;
    Handler handler;
    Runnable runnable;
    ArrayList<Integer> digit1, digit2, digit3, digit4, digit5, digit6, additional;

    private SharedPreferences.Editor advPrefsEditor;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    FirebaseUser firebaseUser;
    ArrayList<String> saveSetting = new ArrayList<>();
    ArrayList<Boolean> delSetting = new ArrayList<>();
    TextView advFilterDisplay, argumentTextDisplay;
    String algorithmBuff = "N/A";
    String currentAdvFilter = "No filter";
    boolean hasAlgorithm;
    ArrayAdapter<CharSequence> argAdaptorList;
    Spinner algorithm;
    Spinner argument;
    Spinner prefix;
    Spinner suffix;
    ArrayList<String> configFileUI;
    private final String PREFS_LOC = "AdvSetting_";

    @Override
    protected void onPostResume() {
        handler = new Handler();
        handler.post(runnable);
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set resource
        advancedBtn  = findViewById(R.id.advBtn);
        exitBtn      = findViewById(R.id.exitBtn);
        menuBtn      = findViewById(R.id.settingBtn);
        menuDrawer   = findViewById(R.id.mainActivityLayout);
        menuNav      = findViewById(R.id.menuNavigation);

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);


        //Set shared Preferences
        //Advance filter resources
        SharedPreferences advPreferences = getSharedPreferences("mainSetting", MODE_PRIVATE);
        advPrefsEditor = advPreferences.edit();

        delSetting.clear();
        saveSetting.clear();
        int index = 0;

        for (int i=0; i<6; i++){
            String getString;

            //Build Preferences location
            getString = advPreferences.getString(PREFS_LOC + index++, "");

            if (!getString.equals("") && !getString.equals("No filter")){
                saveSetting.add(getString);
                delSetting.add(false);
            }
        }

        //Create progress dialog
        firebaseLoading = ProgressDialog.show(this,"","Loading data from server", true );

        //Initialize the ads
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Set zip zap button
        button[0] = mainBt      = findViewById(R.id.mainBtn);
        button[1] = searchBt    = findViewById(R.id.searchBtn);
        button[2] = analysisBt  = findViewById(R.id.analysisBtn);
        button[3] = suggestBt   = findViewById(R.id.suggestBtn);

        //Assign click identifier
        mainBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        analysisBt.setOnClickListener(this);
        suggestBt.setOnClickListener(this);

        //Advanced button click listener
        advancedBtn.setOnClickListener(view -> {
            AlertDialog alertDialog;
            //Todo interface code in here
            alertDialog = advFilterDialog();
            alertDialog.show();
        });

        //If user press the exit button
        exitBtn.setOnClickListener(view -> onBackPressed());

        //If user press the menu button
        menuDrawer.closeDrawer(GravityCompat.END);
        menuBtn.setOnClickListener(view -> menuDrawer.openDrawer(GravityCompat.END));

        //Set Navigation menu on item
        menuNav.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menuAccount:
                    AlertDialog account = accountSelected();
                    account.show();
                    break;
                case R.id.menuLanguage:
                    AlertDialog languageAlert = languageSelected();
                    languageAlert.show();
                    break;
                case R.id.menuLoadFormula:
                    AlertDialog menuFormula = configLoad();
                    menuFormula.show();
                    break;
                case R.id.menuPurchase:
                    break;
                case R.id.menuAboutAs:
                    AlertDialog aboutUs = aboutUs();
                    aboutUs.show();
                    break;
                case R.id.menuLogOut:
                    logOutProcess();
                    break;
                default:
                    return false;
            }
            menuDrawer.closeDrawer(GravityCompat.END);
            return true;
        });

        //Firebase Database data retrieval code
        //Before getting data clear the array list
        DataProcessing.orgDate.clear();
        DataProcessing.orgDigital1.clear();
        DataProcessing.orgDigital2.clear();
        DataProcessing.orgDigital3.clear();
        DataProcessing.orgDigital4.clear();
        DataProcessing.orgDigital5.clear();
        DataProcessing.orgDigital6.clear();
        DataProcessing.orgAdditional.clear();

        totoResultDatabase = FirebaseDatabase.getInstance("https://totodataanalysis-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference  = totoResultDatabase.getReference().child("ToTo Result");


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataProcessing.orgDate.add(snapshot.child("date").getValue(String.class));
                DataProcessing.orgDigital1.add(twoDigit(snapshot.child("1").getValue(String.class)));
                DataProcessing.orgDigital2.add(twoDigit(snapshot.child("2").getValue(String.class)));
                DataProcessing.orgDigital3.add(twoDigit(snapshot.child("3").getValue(String.class)));
                DataProcessing.orgDigital4.add(twoDigit(snapshot.child("4").getValue(String.class)));
                DataProcessing.orgDigital5.add(twoDigit(snapshot.child("5").getValue(String.class)));
                DataProcessing.orgDigital6.add(twoDigit(snapshot.child("6").getValue(String.class)));
                DataProcessing.orgAdditional.add(twoDigit(snapshot.child("additional").getValue(String.class)));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        runnable = () -> {
            //Get string array list to integer array list
            //This data will be used on advance filter
            digit1 = getIntegerArray(DataProcessing.orgDigital1);
            digit2 = getIntegerArray(DataProcessing.orgDigital2);
            digit3 = getIntegerArray(DataProcessing.orgDigital3);
            digit4 = getIntegerArray(DataProcessing.orgDigital4);
            digit5 = getIntegerArray(DataProcessing.orgDigital5);
            digit6 = getIntegerArray(DataProcessing.orgDigital6);
            additional = getIntegerArray(DataProcessing.orgAdditional);

            //Check the size of digital
            if (digit1.size() == 0)
                handler.postDelayed(runnable, 1000);
            else
                firebaseLoading.dismiss();
        };
    }

    private void logOutProcess() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                firebaseAuth.signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                MainActivity.this.finish();
            } else
                Toast.makeText(MainActivity.this, "Can't signOut", Toast.LENGTH_SHORT).show();
        });
    }
    //End of main program


    @Override
    public void onBackPressed() {
        AlertDialog exitAlert = exitConfirmed();
        exitAlert.show();
    }

    private AlertDialog exitConfirmed(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.exit_confirmed, null);
        alertDialog.setView(dialogView);
        alertDialog.setCanceledOnTouchOutside(false);

        //Set the resource value
        MaterialButton confirmed = dialogView.findViewById(R.id.exitConfirmed);
        MaterialButton exit = dialogView.findViewById(R.id.exitCancel);

        confirmed.setOnClickListener(view -> {
            alertDialog.dismiss();
            finish();
        });

        exit.setOnClickListener(view -> alertDialog.dismiss());

        return alertDialog;
    }

    private AlertDialog accountSelected(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.account_setting, null);
        alertDialog.setView(dialogView);

        ImageView accountImage = dialogView.findViewById(R.id.accountImage);
        TextInputLayout emailLayout = dialogView.findViewById(R.id.accountEmail);
        TextInputLayout getOldPassword = dialogView.findViewById(R.id.accountOldPassword);
        TextInputLayout getPassword = dialogView.findViewById(R.id.accountNewPassword);
        TextInputLayout getConfirmPassword = dialogView.findViewById(R.id.accountConfirmPassword);
        Button confirmBtn = dialogView.findViewById(R.id.accountConfirmButton);

        if (firebaseUser != null){
            if (firebaseUser.getPhotoUrl() != null)
                Glide.with(MainActivity.this).load(firebaseUser.getPhotoUrl()).into(accountImage);
            Objects.requireNonNull(emailLayout.getEditText()).setText(firebaseUser.getEmail());
        }

        confirmBtn.setOnClickListener(view -> {
            //Get the current input password
            String email = Objects.requireNonNull(emailLayout.getEditText()).getText().toString();
            String oldPassword = Objects.requireNonNull(getOldPassword.getEditText()).getText().toString();
            String currentPassword = Objects.requireNonNull(getPassword.getEditText()).getText().toString();
            String confirmPassword = Objects.requireNonNull(getConfirmPassword.getEditText()).getText().toString();

            if (TextUtils.isEmpty(currentPassword)){
                getPassword.setError("Password must not be empty");
                return;
            } else
                getPassword.setError(null);

            if (!currentPassword.equals(confirmPassword)){
                getConfirmPassword.setError("Password must be same");
                return;
            } else
                getConfirmPassword.setError(null);

            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    getOldPassword.setError(null);
                    firebaseUser.updatePassword(currentPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            new AlertDialog.Builder(view.getContext())
                                    .setMessage("Password is been changed")
                                    .setPositiveButton("OK", (dialogInterface, i) -> alertDialog.dismiss())
                                    .show();
                        } else {
                            new AlertDialog.Builder(view.getContext())
                                    .setMessage("Password can not be changed")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
                } else {
                    getOldPassword.setError("Check old password");
                }
            });
        });

        return alertDialog;
    }

    private AlertDialog languageSelected(){
        int[] selectedTag = {0};
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.language_layout, null);
        alertDialog.setView(dialogView);
        alertDialog.setCanceledOnTouchOutside(false);

        //Todo language selected code
        MaterialButton confirmed = dialogView.findViewById(R.id.languageConfirmBtn);
        TextView engSelected = dialogView.findViewById(R.id.lanEng);
        TextView myaSelected = dialogView.findViewById(R.id.lanMya);
        TextView cheSelected = dialogView.findViewById(R.id.lanChi);


        confirmed.setOnClickListener(view -> {
            //Todo action code
            new AlertDialog.Builder(view.getContext())
                    .setMessage("Sorry! this function is not available for Beta Version")
                    .setPositiveButton("OK", (dialogInterface, id) -> alertDialog.dismiss()).show();
        });

        engSelected.setOnClickListener(view -> {
            selectedTag[0] = 1;
            engSelected.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.PrimaryVariant));
            myaSelected.setBackgroundColor(0);
            cheSelected.setBackgroundColor(0);
        });

        myaSelected.setOnClickListener(view -> {
            selectedTag[0] = 2;
            myaSelected.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.PrimaryVariant));
            engSelected.setBackgroundColor(0);
            cheSelected.setBackgroundColor(0);
        });

        cheSelected.setOnClickListener(view -> {
            selectedTag[0] = 3;
            cheSelected.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.PrimaryVariant));
            engSelected.setBackgroundColor(0);
            myaSelected.setBackgroundColor(0);
        });

        return alertDialog;
    }

    private AlertDialog configLoad(){
        //Initial load file

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.config_menu_layout, null);
        alertDialog.setView(dialogView);
        alertDialog.setCanceledOnTouchOutside(false);

        ListView configFile = dialogView.findViewById(R.id.configListView);

        //Copy new value setting for update
        configFileUI = new ArrayList<>(saveSetting);

        ConfigLoadAdaptor adaptor = new ConfigLoadAdaptor(getApplicationContext(), configFileUI);
        adaptor.setUpdateSetChecked(this);
        configFile.setAdapter(adaptor);

        MaterialButton okBtn = dialogView.findViewById(R.id.configOkBtn);
        MaterialButton delBtn  = dialogView.findViewById(R.id.configDelBtn);

        okBtn.setOnClickListener(view -> alertDialog.dismiss());

        boolean[] emptyFlag = {false};
        if (saveSetting.isEmpty())
            emptyFlag[0] = true;

        delBtn.setOnClickListener(view -> {
            //Build new dialog for confirmed delete setting
            emptyFlag[0] = true;
            for (boolean status: delSetting){
                if (status) {
                    emptyFlag[0] = false;
                    break;
                }
            }

            if (!emptyFlag[0]){
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Are you sure delete this setting")
                        .setPositiveButton("Yes", (dialogInterface, id) -> {
                            int currentPosition = 0;
                            for (boolean status: delSetting){
                                if (status){
                                    saveSetting.remove(currentPosition);
                                } else {
                                    currentPosition++;
                                }
                            }

                            //Reload to Preference Sharing
                            currentPosition = 0;
                            advPrefsEditor.clear();
                            delSetting.clear();
                            for (String filter: saveSetting){
                                advPrefsEditor.putString(PREFS_LOC + currentPosition, filter);
                                delSetting.add(false);
                                currentPosition++;
                            }
                            advPrefsEditor.commit();
                            alertDialog.dismiss();
                        })
                        .setNegativeButton("No", (dialogInterface, id) -> {
                            //Nothing
                        })
                        .show();
            }

        });
        return alertDialog;
    }

    @SuppressLint("DefaultLocale")
    private AlertDialog advFilterDialog() {

        //Set initial value
        hasAlgorithm = false;

        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.advance_analysis, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCanceledOnTouchOutside(false);

        //Add checkbox function
        MaterialCheckBox loadSearch = dialogView.findViewById(R.id.advSearchCB);
        MaterialCheckBox loadAdditional = dialogView.findViewById(R.id.advAdditionalCB);

        loadSearch.setOnCheckedChangeListener((compoundButton, flag) -> {
            String filter;
            if (flag){
                filter = "Ser:true";
            } else {
                filter = "Ser:false";
            }
            advFilterChange(filter, hasAlgorithm);
        });

        loadAdditional.setOnCheckedChangeListener((compoundButton, flag) -> {
            String filter;
            if (flag){
                filter = "Add:true";
            } else {
                filter = "Add:false";
            }
            advFilterChange(filter, hasAlgorithm);
        });

        //Todo code for advance filter
        String[] filterItems = new String[50];
        filterItems[0] = "N/A";
        for (int j=1; j<=49; j++)
            filterItems[j] = String.format("%02d", j);

        NumberPicker picker = dialogView.findViewById(R.id.advNumberPicker);
        picker.setDisplayedValues(filterItems);
        picker.setMinValue(0);
        picker.setMaxValue(filterItems.length - 1);
        picker.setWrapSelectorWheel(true);

        //Adding spinner adaptor lists
        ArrayAdapter<CharSequence> algorithmAdaptorList = ArrayAdapter.createFromResource(this,
                R.array.algorithm, android.R.layout.simple_spinner_dropdown_item);

        argAdaptorList = ArrayAdapter.createFromResource(this,
                R.array.JUMP, android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> prefixAdaptorList = ArrayAdapter.createFromResource(this,
                R.array.prefix, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> suffixAdaptorList = ArrayAdapter.createFromResource(this,
                R.array.suffix, android.R.layout.simple_spinner_dropdown_item);

        algorithm = dialogView.findViewById(R.id.algorithm);
        argument = dialogView.findViewById(R.id.advArg);
        prefix = dialogView.findViewById(R.id.prefix);
        suffix = dialogView.findViewById(R.id.suffix);

        algorithm.setAdapter(algorithmAdaptorList);
        argument.setAdapter(argAdaptorList);
        prefix.setAdapter(prefixAdaptorList);
        suffix.setAdapter(suffixAdaptorList);


        //Set item click listener
        picker.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            String filter;
            if (newValue == 0)
                filter = "Num:" + "N/A";
            else
                filter = "Num:" + newValue;
            advFilterChange(filter, hasAlgorithm);
        });
        advFilterDisplay = dialogView.findViewById(R.id.advFilterDisplay);
        argumentTextDisplay = dialogView.findViewById(R.id.advArgumentText);
        algorithm.setOnItemSelectedListener(advFilter);
        prefix.setOnItemSelectedListener(advFilter);
        suffix.setOnItemSelectedListener(advFilter);
        argument.setOnItemSelectedListener(advFilter);

        prefix.setEnabled(false);
        suffix.setEnabled(false);
        argument.setEnabled(false);

        //Button
        MaterialButton confirmedBtn = dialogView.findViewById(R.id.advConfirmed);
        MaterialButton exitBtn = dialogView.findViewById(R.id.advExit);

        confirmedBtn.setOnClickListener(view -> {
            if (!currentAdvFilter.equals("No filter")){
                //Find duplicate setting, if find break the loop
                for (String match: saveSetting){
                    if (match.equals(currentAdvFilter)) {
                        confirmedBtn.setError("Duplicate setting");
                        return;
                    }
                }

                String prefsLocation;
                if (saveSetting.size() >= 6) {
                    prefsLocation = PREFS_LOC + "5";
                    saveSetting.set(5, currentAdvFilter);
                } else {
                    prefsLocation = PREFS_LOC + saveSetting.size();
                    saveSetting.add(currentAdvFilter); //Add current setting to save setting
                    delSetting.add(false);
                }

                advPrefsEditor.putString(prefsLocation, currentAdvFilter);
                advPrefsEditor.commit();
            }
            dialogBuilder.dismiss();
        });
        exitBtn.setOnClickListener(view -> dialogBuilder.dismiss());

        return dialogBuilder;
    }

    private AlertDialog aboutUs(){
        //Set variable
        boolean[] tncFlag = {true};
        boolean[] cpyRightFlag = {true};
        boolean[] privacyFlag = {true};


        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.about_us, null);
        dialogBuilder.setView(dialogView);

        //Set resource for T&C
        ConstraintLayout tncBtn = dialogView.findViewById(R.id.termsAbout);
        ExpandableTextView tncTxt = dialogView.findViewById(R.id.aboutUsTnC);
        ImageView tncImg = dialogView.findViewById(R.id.tncExpandImg);

        //Set resource for copy right
        ConstraintLayout cpyRightBtn = dialogView.findViewById(R.id.copyAbout);
        ExpandableTextView cpyRightTxt = dialogView.findViewById(R.id.aboutUsCpyRight);
        ImageView cpyRightImg = dialogView.findViewById(R.id.cpyRightExpand);

        //Set resources for privacy
        ConstraintLayout privacyBtn = dialogView.findViewById(R.id.privacyAbout);
        ExpandableTextView privacyTxt = dialogView.findViewById(R.id.aboutUsPrivacy);
        ImageView pravicyImg = dialogView.findViewById(R.id.privacyExpand);

        tncBtn.setOnClickListener(view -> {
            if (tncFlag[0]) {
                tncTxt.setText(getString(R.string.termsNcondition));
                tncImg.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            } else {
                tncTxt.setText(null);
                tncImg.setImageResource(R.drawable.baseline_arrow_right_24);
            }
            tncFlag[0] = !tncFlag[0];
        });

        cpyRightBtn.setOnClickListener(view -> {
            if (cpyRightFlag[0]) {
                cpyRightTxt.setText(getString(R.string.copyRight));
                cpyRightImg.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            } else {
                cpyRightTxt.setText(null);
                cpyRightImg.setImageResource(R.drawable.baseline_arrow_right_24);
            }
            cpyRightFlag[0] = !cpyRightFlag[0];
        });

        privacyBtn.setOnClickListener(view -> {
            if (privacyFlag[0]) {
                privacyTxt.setText(getString(R.string.privacy));
                pravicyImg.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            } else {
                privacyTxt.setText(null);
                pravicyImg.setImageResource(R.drawable.baseline_arrow_right_24);
            }
            privacyFlag[0] = !privacyFlag[0];
        });

        return dialogBuilder;
    }

    AdapterView.OnItemSelectedListener advFilter = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            String advFilter = "";
            switch (adapterView.getId()){
                case R.id.algorithm:
                    advFilter = "Alg:" + adapterView.getSelectedItem().toString();
                    if (!adapterView.getSelectedItem().toString().equals("N/A")) {
                        argumentTextDisplay.setText(adapterView.getSelectedItem().toString());
                        hasAlgorithm = true;

                        //Enable spinner to selected
                        prefix.setEnabled(true);
                        suffix.setEnabled(true);
                        argument.setEnabled(true);

                    } else {

                        //Disable spinner to selected
                        argumentTextDisplay.setText(getResources().getString(R.string.arg));
                        hasAlgorithm = false;

                        prefix.setEnabled(false);
                        suffix.setEnabled(false);
                        argument.setEnabled(false);

                        prefix.setSelection(0);
                        suffix.setSelection(0);
                        argument.setSelection(0);
                    }
                    break;
                case R.id.prefix:
                    advFilter = "Pre:" + adapterView.getSelectedItem().toString();
                    break;
                case R.id.suffix:
                    advFilter = "Suf:" + adapterView.getSelectedItem().toString();
                    break;
                case R.id.advArg:
                    advFilter = "Arg:" + adapterView.getSelectedItem().toString();
                    break;
            }
            advFilterChange(advFilter, hasAlgorithm);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    /**
     *
     * @param advFilter
     * @param hasAlgorithm
     */
    private void advFilterChange(String advFilter, boolean hasAlgorithm) {
        String filterPre = advFilterDisplay.getText().toString();
        StringBuilder filterDisplayString = new StringBuilder(filterPre);

        //Check if display in adv filter
        if (filterDisplayString.toString().equals("No filter"))
            filterDisplayString = new StringBuilder();

        if (!hasAlgorithm)
            advFilterDisplay.setText(getString(R.string.no_filter));

        //Query the data
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Num", "N/A");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Alg", "N/A");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Pre", "N/A");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Suf", "N/A");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Arg", "N/A");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Ser", "false");
        filterDisplayString = dataQuery(filterDisplayString.toString(), advFilter, "Add", "false");
        System.out.println(filterDisplayString);

        //This function must be place below
        //If there is no filter, display "No filter" in display query
        if (!filterDisplayString.toString().contains("Num") && !filterDisplayString.toString().contains("Alg") && !filterDisplayString.toString().contains("Pre")
                && !filterDisplayString.toString().contains("Suf") && !filterDisplayString.toString().contains("Arg") && !filterDisplayString.toString().contains("Ser")
                && !filterDisplayString.toString().contains("Add")){
            filterDisplayString = new StringBuilder("No filter");
            algorithmBuff = "N/A";
        } else {
            String algorithmValue = filterDisplayBuilder(filterDisplayString, "Num");
            filterDisplayBuilder(filterDisplayString, "Alg");
            filterDisplayBuilder(filterDisplayString, "Pre");
            filterDisplayBuilder(filterDisplayString, "Suf");
            filterDisplayBuilder(filterDisplayString, "Arg");
            filterDisplayBuilder(filterDisplayString, "Ser");
            filterDisplayBuilder(filterDisplayString, "Add");

            //Set the
            algorithmBuff = algorithmValue;
        }

        //Set the filter display on text view
        advFilterDisplay.setText(filterDisplayString);
        currentAdvFilter = filterDisplayString.toString(); //Store to current advFilter
    }

    private String filterDisplayBuilder(StringBuilder filterDisplayString, String filterString) {
        if (filterDisplayString.toString().contains(filterString)){
            String preDate = filterDisplayString.substring(filterDisplayString.indexOf(filterString));
            String preEnd = preDate.substring(preDate.indexOf(";"));

            int preDateIndex = filterDisplayString.indexOf(preDate);
            int preEndIndex = preDate.indexOf(preEnd);

            if (preDateIndex != -1) {
                return filterDisplayString.substring(preDateIndex + filterString.length() + 1, preDateIndex + preEndIndex);
            }
        }
        return "";
    }

    private StringBuilder dataQuery(String inputString, String queryString, String filter, @Nullable String defFilter){
        StringBuilder filterString = new StringBuilder(inputString);
        if (queryString.contains(filter)){
            if (inputString.contains(filter)){
                String deleteYearSetting = inputString.substring(inputString.indexOf(filter));
                String spaceDelete = deleteYearSetting.substring(deleteYearSetting.indexOf(";"));
                int yearDeleteIndex = inputString.indexOf(deleteYearSetting);
                int spaceIndex = deleteYearSetting.indexOf(spaceDelete);

                if (yearDeleteIndex != -1)
                    filterString.delete(yearDeleteIndex, yearDeleteIndex + spaceIndex + 1);
            }
            //If selected year, append to string builder
            if(!queryString.equals(filter + ":" + filter) && !queryString.equals(filter + ":" + defFilter)){
                filterString.append(queryString).append(";");
            }
        }

        return filterString;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mainBtn:
                buttonID = 1;
                break;
            case R.id.searchBtn:
                buttonID = 2;
                break;
            case R.id.analysisBtn:
                buttonID = 3;
                break;
            case R.id.suggestBtn:
                buttonID = 4;
                break;
            default:
                buttonID = 9;
                break;
        }

        //Test which button need to transit
        if (tempButtonID == buttonID && !checkDouble){
            checkDouble = true;

            //ToDo button default state code here
            for (int i= 0; i<4; i++) {
                setButtonLayout(button[i],false,view);
            }
            tempButtonID = 5;
            fragmentTransaction(5);
        } else {
            checkDouble = false;

            //ToDo button transition code here
            for (int i=1; i<5; i++){
                setButtonLayout(button[i-1], buttonID == i,view);
            }
            tempButtonID = buttonID;
            fragmentTransaction(buttonID);
        }
    }

    private void setButtonLayout(MaterialCardView cardView, boolean setButton, View getView){
        float pixels = getView.getContext().getResources().getDisplayMetrics().density;
        if (setButton){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (100 * pixels + 0.5f));
            params.gravity = Gravity.END;
            params.topMargin = (int) (10 * pixels + 0.5f);
            cardView.setLayoutParams(params);
            cardView.setBackground(ContextCompat.getDrawable(this, R.color.secondary_background));
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (100 * pixels + 0.5f));
            params.gravity = Gravity.END;
            params.topMargin = (int) (10 * pixels + 0.5f);
            cardView.setLayoutParams(params);
            cardView.setBackground(ContextCompat.getDrawable(this, R.color.white));
        }
    }

    private void fragmentTransaction(int layerIndex){
        if (layerIndex < 1 || layerIndex > 6)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevMain = getSupportFragmentManager().findFragmentByTag("consoleFragmentLayer");
        if (prevMain != null)
            transaction.remove(prevMain);

        switch (layerIndex){
            case 1:
                MainPage mainPage = new MainPage();
                transaction.replace(R.id.fragmentConsole, mainPage, "consoleFragmentLayer");
                break;
            case 2:
                SearchPage searchPage = new SearchPage();
                transaction.replace(R.id.fragmentConsole, searchPage, "consoleFragmentLayer");
                break;
            case 3:
                AnalysisPage analysisPage = new AnalysisPage();
                transaction.replace(R.id.fragmentConsole, analysisPage, "consoleFragmentLayer");
                break;
            case 4:
                SuggestPage suggestPage = new SuggestPage();
                transaction.replace(R.id.fragmentConsole, suggestPage, "consoleFragmentLayer");
                break;
            case 5:
                DisplayInformation displayInformation = new DisplayInformation();
                transaction.replace(R.id.fragmentConsole, displayInformation, "consoleFragmentLayer");
                break;
            default:
                return;
        }
        transaction.setReorderingAllowed(true);
        transaction.commitNow();
    }

    /**
     * Get the array string to array Integer
     * @param stringArray, input array string
     * @return output the array integer
     */
    private ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    private String twoDigit (String intString){
        String result;

        //If text is not found, return 0
        if (TextUtils.isEmpty(intString))
            return "00";

        try {
            result = String.format("%02d", Integer.parseInt(intString));
        } catch (NumberFormatException nfe) {
            Log.w("NumberFormat", "Parsing failed! " + intString + " can not be an integer");
            return "00";
        }
        return result;
    }

    @Override
    public void onCheckedListener(int position, boolean flag) {
        delSetting.set(position, flag);
    }
}