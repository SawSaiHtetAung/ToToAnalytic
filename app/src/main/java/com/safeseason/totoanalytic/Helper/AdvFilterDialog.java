package com.safeseason.totoanalytic.Helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.safeseason.totoanalytic.R;
import com.safeseason.totoanalytic.RegisterNewUser.RegisterViewModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdvFilterDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvFilterDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "pre setting";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ArrayList<String> mParam1;
    private String mParam2;

    //Set global variable
    ArrayList<String> saveSetting = new ArrayList<>();
    TextView advFilterDisplay, argumentTextDisplay;
    String algorithmBuff = "N/A";
    String currentAdvFilter = "No filter";
    boolean hasAlgorithm;
    ArrayAdapter<CharSequence> argAdaptorList;
    Spinner algorithm;
    Spinner argument;
    Spinner prefix;
    Spinner suffix;
    private SharedPreferences.Editor advPrefsEditor;
    private final String PREFS_LOC = "AdvSetting_";
    private final String PREFS_PROF = "AdvProfile_";
    AdvFilterViewModel viewModel;

    public AdvFilterDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdvFilterDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AdvFilterDialog newInstance(String param1, String param2) {
        AdvFilterDialog fragment = new AdvFilterDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Set window to fixed on 90%
    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();;
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            saveSetting = getArguments().getStringArrayList(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdvFilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adv_filter_dialog, container, false);

        //Set initial value
        hasAlgorithm = false;

        //Set shared Preferences
        //Advance filter resources
        SharedPreferences advPreferences = requireActivity().getSharedPreferences("mainSetting", MODE_PRIVATE);
        advPrefsEditor = advPreferences.edit();

        //Add checkbox function
        CheckBox loadSearch = view.findViewById(R.id.advSearchCB);
        CheckBox loadAdditional = view.findViewById(R.id.advAdditionalCB);

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
            filterItems[j] = String.format(Locale.US,"%02d", j);

        NumberPicker picker = view.findViewById(R.id.advNumberPicker);
        picker.setDisplayedValues(filterItems);
        picker.setMinValue(0);
        picker.setMaxValue(filterItems.length - 1);
        picker.setWrapSelectorWheel(true);

        //Adding spinner adaptor lists
        ArrayAdapter<CharSequence> algorithmAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.algorithm, android.R.layout.simple_spinner_dropdown_item);

        argAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.JUMP, android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> prefixAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.prefix, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> suffixAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.suffix, android.R.layout.simple_spinner_dropdown_item);

        algorithm = view.findViewById(R.id.algorithm);
        argument = view.findViewById(R.id.advArg);
        prefix = view.findViewById(R.id.prefix);
        suffix = view.findViewById(R.id.suffix);

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
        advFilterDisplay = view.findViewById(R.id.advFilterDisplay);
        argumentTextDisplay = view.findViewById(R.id.advArgumentText);
        algorithm.setOnItemSelectedListener(advFilter);
        prefix.setOnItemSelectedListener(advFilter);
        suffix.setOnItemSelectedListener(advFilter);
        argument.setOnItemSelectedListener(advFilter);

        prefix.setEnabled(false);
        suffix.setEnabled(false);
        argument.setEnabled(false);

        //Button
        MaterialButton confirmedBtn = view.findViewById(R.id.advConfirmed);
        MaterialButton exitBtn = view.findViewById(R.id.advExit);

        confirmedBtn.setOnClickListener(confView -> {
            if (!currentAdvFilter.equals("No filter")){
                //Find duplicate setting, if find break the loop
                for (String match: saveSetting){
                    if (match.equals(currentAdvFilter)) {
                        confirmedBtn.setError("Duplicate setting");
                        return;
                    }
                }

                //Set input layout in edit text
                final EditText profileInput = new EditText(confView.getContext());
                profileInput.setGravity(Gravity.CENTER);

                new AlertDialog.Builder(confView.getContext())
                        .setTitle("Enter the profile name")
                        .setView(profileInput)
                        .setPositiveButton("OK", (dialogInterface, which) -> {
                            String profileString;
                            profileString = profileInput.getText().toString();

                            for (String match: saveSetting){
                                if (match.equals(profileString)){
                                    Toast.makeText(getContext(), "Can't be same name as previous profile", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            if (!TextUtils.isEmpty(profileString)){
                                String prefsLocation;
                                String prefsProfile;

                                if (saveSetting.size() >= 12) {
                                    prefsLocation = PREFS_LOC + "5";
                                    prefsProfile = PREFS_PROF + "5";
                                    saveSetting.set(10, currentAdvFilter);
                                    saveSetting.set(11, profileString);
                                    //Add to view model to activity communication
                                    viewModel.setAdvFilterString(saveSetting);
                                } else {
                                    prefsLocation = PREFS_LOC + (saveSetting.size() / 2);
                                    prefsProfile = PREFS_PROF + ((saveSetting.size() / 2) + 1);
                                    saveSetting.add(currentAdvFilter); //Add current setting to save setting
                                    saveSetting.add(profileString);
                                    //Add to view model to activity communication
                                    viewModel.setAdvFilterString(saveSetting);
                                }

                                advPrefsEditor.putString(prefsLocation, currentAdvFilter);
                                advPrefsEditor.putString(prefsProfile, profileString);
                                advPrefsEditor.commit();
                                dismissAllowingStateLoss();
                            } else {
                                profileInput.setError("Name can't be empty");
                                Toast.makeText(getContext(), "Profile can't be empty!", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.cancel())
                        .show();
            }
        });
        exitBtn.setOnClickListener(exitView -> dismiss());

        return view;
    }

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
}