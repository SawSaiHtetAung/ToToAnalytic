package com.safeseason.totoanalytic.SubDisplay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.safeseason.totoanalytic.Helper.DataProcessing;
import com.safeseason.totoanalytic.MainActivity;
import com.safeseason.totoanalytic.R;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class AnalysisPage extends Fragment {

    //Data reference
    public static ArrayList<String> originalByDate;
    public static ArrayList<String> originalByDigital1;
    public static ArrayList<String> originalByDigital2;
    public static ArrayList<String> originalByDigital3;
    public static ArrayList<String> originalByDigital4;
    public static ArrayList<String> originalByDigital5;
    public static ArrayList<String> originalByDigital6;
    public static ArrayList<String> originalByAdditional;

    //Google ads
    private InterstitialAd mInterstitialAd;
    private CountDownTimer countDownTimer;
    ProgressDialog adsDialog;
    private long timerMilliseconds;
    private static final long GAME_LENGTH_MILLISECONDS = 10000;
    private static final String AD_UNIT_ID = "ca-app-pub-9420035181070868~1835382690";
    private static final String TAG = "AdvFilterLoad";


    XYSeries topListSerial, buttonListSerial;
    XYPlot plotTop, plotBelow;
    BarFormatter plotTopFormatter;
    ArrayList<Integer> digit1, digit2, digit3, digit4, digit5, digit6, additional;
    Number[] tempSerial = {1,2,3,4,5,5,4,3,2,1};
    boolean additionalFlag;

    //Advance filter
    MaterialTextView filterOutput;
    MaterialCheckBox additionalCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis_page, container, false);
        MaterialButton copyFilter = view.findViewById(R.id.analysisFilterCpy);
        MaterialButton loadFilter = view.findViewById(R.id.analysisLoad);
        MaterialButton clearFilter = view.findViewById(R.id.analysisFilterClr);
        additionalCheck = view.findViewById(R.id.analysisAdditional);
        filterOutput = view.findViewById(R.id.analysisFilterOutput);

        //Initialized the data
        originalByDate = new ArrayList<>(DataProcessing.orgDate);
        originalByDigital1 = new ArrayList<>(DataProcessing.orgDigital1);
        originalByDigital2 = new ArrayList<>(DataProcessing.orgDigital2);
        originalByDigital3 = new ArrayList<>(DataProcessing.orgDigital3);
        originalByDigital4 = new ArrayList<>(DataProcessing.orgDigital4);
        originalByDigital5 = new ArrayList<>(DataProcessing.orgDigital5);
        originalByDigital6 = new ArrayList<>(DataProcessing.orgDigital6);
        originalByAdditional = new ArrayList<>(DataProcessing.orgAdditional);
        additionalFlag = false;

        //Google ads load
        MobileAds.initialize(getContext(), initializationStatus -> {

        });

        //Disable in beta version
        loadFilter.setOnClickListener(view1 -> {
            //Loading google ads
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(getContext(), AD_UNIT_ID, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                        }
                    });
            //End of google ads declared

            if (mInterstitialAd != null){
                adsDialog = ProgressDialog.show(view1.getContext(),"","Loading ads ", true );
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
                mInterstitialAd.show((MainActivity) requireActivity());
                timerMilliseconds = GAME_LENGTH_MILLISECONDS;
                createTimer(GAME_LENGTH_MILLISECONDS);
                countDownTimer.start();
            } else {
                AlertDialog dialog = advFilterDialog();
                dialog.show();
            }
        });

        additionalCheck.setChecked(false);
        additionalCheck.setOnCheckedChangeListener((compoundButton, flag) -> {
            if(flag){
                ArrayList<Integer> sortedNum = computeNumFreq(digit1, null);
                sortedNum = computeNumFreq(digit2, sortedNum);
                sortedNum = computeNumFreq(digit3, sortedNum);
                sortedNum = computeNumFreq(digit4, sortedNum);
                sortedNum = computeNumFreq(digit5, sortedNum);
                sortedNum = computeNumFreq(digit6, sortedNum);
                sortedNum = computeNumFreq(additional, sortedNum);
                setGraphPlot(sortedNum);
            } else {
                ArrayList<Integer> sortedNum = computeNumFreq(digit1, null);
                sortedNum = computeNumFreq(digit2, sortedNum);
                sortedNum = computeNumFreq(digit3, sortedNum);
                sortedNum = computeNumFreq(digit4, sortedNum);
                sortedNum = computeNumFreq(digit5, sortedNum);
                sortedNum = computeNumFreq(digit6, sortedNum);
                setGraphPlot(sortedNum);
            }
        });

        copyFilter.setOnClickListener(viewCpy -> {
            if(DataProcessing.globalFilter != null) {
                //Display on filter text view
                filterOutput.setText(DataProcessing.globalFilter);
                additionalCheck.setEnabled(true);
                additionalCheck.setChecked(false);

                // Copy from search data
                digit1 = getIntegerArray(DataProcessing.searchByDigital1);
                digit2 = getIntegerArray(DataProcessing.searchByDigital2);
                digit3 = getIntegerArray(DataProcessing.searchByDigital3);
                digit4 = getIntegerArray(DataProcessing.searchByDigital4);
                digit5 = getIntegerArray(DataProcessing.searchByDigital5);
                digit6 = getIntegerArray(DataProcessing.searchByDigital6);
                additional = getIntegerArray(DataProcessing.searchByAdditional);

                ArrayList<Integer> sortedNum = computeNumFreq(digit1, null);
                sortedNum = computeNumFreq(digit2, sortedNum);
                sortedNum = computeNumFreq(digit3, sortedNum);
                sortedNum = computeNumFreq(digit4, sortedNum);
                sortedNum = computeNumFreq(digit5, sortedNum);
                sortedNum = computeNumFreq(digit6, sortedNum);

                if(additionalCheck.isChecked())
                    sortedNum = computeNumFreq(additional, sortedNum);

                setGraphPlot(sortedNum);
            }
        });

        clearFilter.setOnClickListener(viewClr -> {
            filterOutput.setText(getString(R.string.no_filter));
            additionalCheck.setEnabled(true);
            additionalCheck.setChecked(false);

            //Get string array list to integer array list
            digit1 = getIntegerArray(originalByDigital1);
            digit2 = getIntegerArray(originalByDigital2);
            digit3 = getIntegerArray(originalByDigital3);
            digit4 = getIntegerArray(originalByDigital4);
            digit5 = getIntegerArray(originalByDigital5);
            digit6 = getIntegerArray(originalByDigital6);
            additional = getIntegerArray(originalByAdditional);

            System.out.println(digit1.size());

            ArrayList<Integer> sortedNum = computeNumFreq(digit1, null);
            sortedNum = computeNumFreq(digit2, sortedNum);
            sortedNum = computeNumFreq(digit3, sortedNum);
            sortedNum = computeNumFreq(digit4, sortedNum);
            sortedNum = computeNumFreq(digit5, sortedNum);
            sortedNum = computeNumFreq(digit6, sortedNum);

            if(additionalCheck.isChecked())
                sortedNum = computeNumFreq(additional, sortedNum);

            setGraphPlot(sortedNum);
        });


        //Test barchart
        //Test variable
        plotTop = view.findViewById(R.id.plotTop);
        plotBelow = view.findViewById(R.id.plotBelow);

        topListSerial = new SimpleXYSeries(Arrays.asList(tempSerial), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Loading...");
        buttonListSerial = new SimpleXYSeries(Arrays.asList(tempSerial), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Loading...");

        // Plot the top 10 number
        plotTopFormatter = new BarFormatter(ContextCompat.getColor(requireContext(), R.color.primary_color), Color.WHITE);
        plotTop.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new DecimalFormat("0"));
        plotTop.addSeries(topListSerial, plotTopFormatter);

        // Plot the low 10 number
        plotBelow.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new DecimalFormat("0"));
        plotBelow.addSeries(buttonListSerial, plotTopFormatter); //Share the plot formatter with top plot

        // Set bar to top plot wider
        BarRenderer topRenderer = plotTop.getRenderer(BarRenderer.class);
        topRenderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
        topRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(10));
        plotTop.redraw();

        // Set bar to button plot wider
        BarRenderer belowRenderer = plotBelow.getRenderer(BarRenderer.class);
        belowRenderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
        belowRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(10));
        plotBelow.redraw();

        //Get string array list to integer array list
        digit1 = getIntegerArray(originalByDigital1);
        digit2 = getIntegerArray(originalByDigital2);
        digit3 = getIntegerArray(originalByDigital3);
        digit4 = getIntegerArray(originalByDigital4);
        digit5 = getIntegerArray(originalByDigital5);
        digit6 = getIntegerArray(originalByDigital6);
        additional = getIntegerArray(originalByAdditional);

        System.out.println(digit1.size());

        ArrayList<Integer> sortedNum = computeNumFreq(digit1, null);
        sortedNum = computeNumFreq(digit2, sortedNum);
        sortedNum = computeNumFreq(digit3, sortedNum);
        sortedNum = computeNumFreq(digit4, sortedNum);
        sortedNum = computeNumFreq(digit5, sortedNum);
        sortedNum = computeNumFreq(digit6, sortedNum);
        setGraphPlot(sortedNum);

        return view;
    }

    //Google ads services
    private void createTimer(final long millisecond) {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(millisecond, 50) {
            @Override
            public void onTick(long millisUitFinished) {
                timerMilliseconds = millisUitFinished;
                adsDialog.setMessage("Second remaining: " + ((millisUitFinished /1000) + 1) + "s");
            }

            @Override
            public void onFinish() {
                //show the dialog
                adsDialog.dismiss();
                AlertDialog dialog = advFilterDialog();
                dialog.show();
            }
        };
    }

    private void setGraphPlot(ArrayList<Integer> computeNumFrequency){
        //Set variable
        ArrayList<Integer> xValueMax = new ArrayList<>();
        ArrayList<Integer> yValueMax = new ArrayList<>();
        ArrayList<Integer> xValueMin = new ArrayList<>();
        ArrayList<Integer> yValueMin = new ArrayList<>();

        //Remove all plot date
        plotTop.removeSeries(topListSerial);
        plotBelow.removeSeries(buttonListSerial);

        ArrayList<Integer> cpyCompute = new ArrayList<>(computeNumFrequency);
        ArrayList<Integer> maxList = getMaxTop10Num(computeNumFrequency);
        ArrayList<Integer> minList = getMinTop10Num(cpyCompute);

        for (int i=0; i<20; i += 2){
            xValueMax.add(maxList.get(i+1));
            xValueMin.add(minList.get(i+1));

            yValueMax.add(maxList.get(i));
            yValueMin.add(minList.get(i));
        }

        //Redraw the top plot graph with firebase data
        topListSerial = new SimpleXYSeries(xValueMax, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Top 10");

        //If the boundary value will set to limit 0
        if (Collections.min(xValueMax) < 3)
            plotTop.setRangeBoundaries(Collections.min(xValueMax), Collections.max(xValueMax), BoundaryMode.FIXED);
        else
            plotTop.setRangeBoundaries(Collections.min(xValueMax) - 3, Collections.max(xValueMax) + 3, BoundaryMode.FIXED);

        plotTop.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plotTop.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int i = yValueMax.get(((Number) object).intValue());
                return stringBuffer.append(i);
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });

        //Set the format to the integer value
        plotTop.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int x = Math.round(((Number) object).floatValue());
                return stringBuffer.append( x );
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });

        plotTop.addSeries(topListSerial, plotTopFormatter);
        plotTop.redraw();

        //Redraw the button plot graph with firebase data
        buttonListSerial = new SimpleXYSeries(xValueMin, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Below 10");

        //If the boundary value will set to limit 0
        if (Collections.min(xValueMin) < 3)
            plotBelow.setRangeBoundaries(Collections.min(xValueMin), Collections.max(xValueMin) + 3, BoundaryMode.FIXED);
        else
            plotBelow.setRangeBoundaries(Collections.min(xValueMin) - 3, Collections.max(xValueMin) + 3, BoundaryMode.FIXED);

        plotBelow.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plotBelow.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int i= yValueMin.get(((Number) object).intValue());
                return stringBuffer.append(i);
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });

        plotBelow.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int x = Math.round(((Number) object).floatValue());
                return stringBuffer.append( x );
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });
        plotBelow.addSeries(buttonListSerial, plotTopFormatter);
        plotBelow.redraw();

    }

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

    private ArrayList<Integer> computeNumFreq(ArrayList<Integer> unsortedList, @Nullable ArrayList<Integer> existingList){
        ArrayList<Integer> nunFrequency = new ArrayList<>();

        while(nunFrequency.size() < 50) nunFrequency.add(0);

        for(int i=0; i< unsortedList.size(); i++){
            if(unsortedList.get(i) > 0 && unsortedList.get(i) <= 49){
                int additional = nunFrequency.get(unsortedList.get(i));
                nunFrequency.set(unsortedList.get(i), additional + 1);
            }
        }
        if(existingList != null){
            for(int j=0; j<49;j++)
                nunFrequency.set(j, nunFrequency.get(j) + existingList.get(j));
        }
        return nunFrequency;
    }

    private ArrayList<Integer> getMaxTop10Num(ArrayList<Integer> numFrequency){
        ArrayList<Integer> setSortedNumber = new ArrayList<>();
        ArrayList<Integer> holdingSorted = new ArrayList<>(numFrequency);
        Integer[] holdingIndex = {99,99,99,99,99,99,99,99,99,99};
        boolean skip = false;

        numFrequency.sort(Comparator.comparingInt(integer -> -integer));

        for(int j=0; j<10; j++){
            for(int i=0; i<holdingSorted.size(); i++){
                for(Integer num: holdingIndex){
                    if (i == num) {
                        skip = true;
                        break;
                    }
                }
                if(Objects.equals(holdingSorted.get(i), numFrequency.get(j)) && !skip){
                    setSortedNumber.add(i);
                    setSortedNumber.add(numFrequency.get(j));
                    holdingIndex[j] = i;
                    break;
                }
                skip = false;
            }
        }
        return setSortedNumber;
    }

    private ArrayList<Integer> getMinTop10Num(ArrayList<Integer> numFrequency){
        ArrayList<Integer> setSortedNumber = new ArrayList<>();
        ArrayList<Integer> holdingSorted = new ArrayList<>(numFrequency);
        Integer[] holdingIndex = {99,99,99,99,99,99,99,99,99,99};
        boolean skip = false;

        numFrequency.remove(0);
        numFrequency.sort(Comparator.comparingInt(integer -> integer));

        for(int j=0; j<10; j++){
            for(int i=0; i<holdingSorted.size(); i++){
                for(Integer num: holdingIndex){
                    if (i == num) {
                        skip = true;
                        break;
                    }
                }
                if(Objects.equals(holdingSorted.get(i), numFrequency.get(j)) && !skip){
                    setSortedNumber.add(i);
                    setSortedNumber.add(numFrequency.get(j));
                    holdingIndex[j] = i;
                    break;
                }
                skip = false;
            }
        }
        return setSortedNumber;
    }

    private AlertDialog advFilterDialog() {
        //Initial load file
        String[] advFilter = {"No filter"};

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.analysis_load_adv, null);
        alertDialog.setView(dialogView);

        //Get data from sharing preference
        final String PREFS_LOC = "AdvSetting_";

        //Load resources
        RadioGroup group = dialogView.findViewById(R.id.analysisLoadRadioGroup);
        MaterialButton loadBtn = dialogView.findViewById(R.id.analysisLoadToAnalysizer);

        //Set shared Preferences
        SharedPreferences advPreferences = requireContext().getSharedPreferences("mainSetting", Context.MODE_PRIVATE);

        ArrayList<String> saveSetting = new ArrayList<>();
        int index = 0;
        for (int i=0; i<6; i++){
            String getString;

            //Build Preferences location
            getString = advPreferences.getString(PREFS_LOC + index++, "");

            if (!getString.equals("") && !getString.equals("No filter")){
                saveSetting.add(getString);
            }
        }

        //Set radio button to radio button group
        int currentPosition = 0;
        for (String filter: saveSetting){
            MaterialRadioButton selectedBtn = new MaterialRadioButton(requireContext());
            selectedBtn.setText(filter);
            selectedBtn.setId(currentPosition++);
            group.addView(selectedBtn);
        }

        group.setOnCheckedChangeListener((radioGroup, id) -> {
            int selectedId = group.getCheckedRadioButtonId();
            MaterialRadioButton button = dialogView.findViewById(selectedId);
            advFilter[0] = button.getText().toString();
            Toast.makeText(getContext(), button.getText(), Toast.LENGTH_SHORT).show();
        });

        loadBtn.setOnClickListener(view -> {
            advFilterLoad(advFilter[0]);
            alertDialog.dismiss();
        });

        return alertDialog;
    }

    private void advFilterLoad(String advFilter) {
        //Set resources to config setting
        filterOutput.setText(advFilter);
        additionalCheck.setEnabled(false);

        ArrayList<String> tempDate = new ArrayList<>();
        ArrayList<Integer> tempDigital1 = new ArrayList<>();
        ArrayList<Integer> tempDigital2 = new ArrayList<>();
        ArrayList<Integer> tempDigital3 = new ArrayList<>();
        ArrayList<Integer> tempDigital4 = new ArrayList<>();
        ArrayList<Integer> tempDigital5 = new ArrayList<>();
        ArrayList<Integer> tempDigital6 = new ArrayList<>();
        ArrayList<Integer> tempAdditional = new ArrayList<>();


        if (advFilter.contains("Ser:")){
            tempDate.addAll(DataProcessing.searchByDate);
            tempDigital1.addAll(getIntegerArray(DataProcessing.searchByDigital1));
            tempDigital2.addAll(getIntegerArray(DataProcessing.searchByDigital2));
            tempDigital3.addAll(getIntegerArray(DataProcessing.searchByDigital3));
            tempDigital4.addAll(getIntegerArray(DataProcessing.searchByDigital4));
            tempDigital5.addAll(getIntegerArray(DataProcessing.searchByDigital5));
            tempDigital6.addAll(getIntegerArray(DataProcessing.searchByDigital6));
            tempAdditional.addAll(getIntegerArray(DataProcessing.searchByAdditional));
        } else {
            tempDate.addAll(DataProcessing.orgDate);
            tempDigital1.addAll(getIntegerArray(DataProcessing.orgDigital1));
            tempDigital2.addAll(getIntegerArray(DataProcessing.orgDigital2));
            tempDigital3.addAll(getIntegerArray(DataProcessing.orgDigital3));
            tempDigital4.addAll(getIntegerArray(DataProcessing.orgDigital4));
            tempDigital5.addAll(getIntegerArray(DataProcessing.orgDigital5));
            tempDigital6.addAll(getIntegerArray(DataProcessing.orgDigital6));
            tempAdditional.addAll(getIntegerArray(DataProcessing.orgAdditional));
        }

        //Todo processing code
        String numDisplay = trimValue(advFilter, "Num");
        String algDisplay = trimValue(advFilter, "Alg");
        String argDisplay = trimValue(advFilter, "Arg");
        String prefixDisplay = trimValue(advFilter, "Pre");
        String suffixDisplay = trimValue(advFilter, "Suf");

        //Check if additional number is checked
        additionalCheck.setChecked(advFilter.contains("Add:"));

        System.out.println(numDisplay);
        System.out.println(algDisplay);
        System.out.println(argDisplay);
        System.out.println(prefixDisplay);
        System.out.println(suffixDisplay);


        ArrayList<Integer> unSortedNum = new ArrayList<>();
        switch (algDisplay){
            case "JUMP":
                System.out.println("Calculation jump algorithm >>>>>");
                if (!numDisplay.isEmpty()){
                    int index = stringToInteger(numDisplay);
                    System.out.println("Index is:" + index);

                    int jump = 1;
                    if (!argDisplay.isEmpty())
                        jump = stringToInteger(argDisplay);
                    System.out.println("Jump is:" + jump);

                    int currentPos = 0;
                    for (int ignored : tempDigital1){
                        if ((tempDigital1.get(currentPos) == index) || (tempDigital2.get(currentPos) == index) || tempDigital3.get(currentPos) == index ||
                                tempDigital4.get(currentPos) == index || tempDigital5.get(currentPos) == index || tempDigital6.get(currentPos) == index){
                            if (currentPos >= jump) {
                                unSortedNum.add(tempDigital1.get(currentPos - jump));
                                unSortedNum.add(tempDigital2.get(currentPos - jump));
                                unSortedNum.add(tempDigital3.get(currentPos - jump));
                                unSortedNum.add(tempDigital4.get(currentPos - jump));
                                unSortedNum.add(tempDigital5.get(currentPos - jump));
                                unSortedNum.add(tempDigital6.get(currentPos - jump));

                                if (advFilter.contains("Add"))
                                    unSortedNum.add(tempAdditional.get(currentPos - jump));
                                System.out.println(currentPos);
                            }
                        }
                        currentPos++;
                    }
                } else {
                    int jump = 1;
                    if (!argDisplay.isEmpty())
                        jump = stringToInteger(argDisplay);
                    ArrayList<Integer> index = new ArrayList<>();
                    index.add(tempDigital1.get(0));
                    index.add(tempDigital2.get(0));
                    index.add(tempDigital3.get(0));
                    index.add(tempDigital4.get(0));
                    index.add(tempDigital5.get(0));
                    index.add(tempDigital6.get(0));

                    for (int value: index){
                        int currentPos = 0;
                        for (int ignored : tempDigital1){
                            if ((tempDigital1.get(currentPos) == value) || (tempDigital2.get(currentPos) == value) || (tempDigital3.get(currentPos) == value) ||
                                    (tempDigital4.get(currentPos) == value) || (tempDigital5.get(currentPos) == value) || (tempDigital6.get(currentPos) == value)){
                                if (currentPos >= jump) {
                                    unSortedNum.add(tempDigital1.get(currentPos - jump));
                                    unSortedNum.add(tempDigital2.get(currentPos - jump));
                                    unSortedNum.add(tempDigital3.get(currentPos - jump));
                                    unSortedNum.add(tempDigital4.get(currentPos - jump));
                                    unSortedNum.add(tempDigital5.get(currentPos - jump));
                                    unSortedNum.add(tempDigital6.get(currentPos - jump));

                                    if (advFilter.contains("Add"))
                                        unSortedNum.add(tempAdditional.get(currentPos - jump));
                                    System.out.println(currentPos);
                                }
                            }
                            currentPos++;
                        }
                    }

                }
                break;
        }


        //Display processing
        ArrayList<Integer> sorted = computeNumFreq(unSortedNum, null);
        setGraphPlot(sorted);

    }

    private String trimValue(String filterString, String queryString){
        if (filterString.contains(queryString)){
            String delSetting = filterString.substring(filterString.indexOf(queryString));
            System.out.println(delSetting);
            String argDel = delSetting.substring(delSetting.indexOf(":"));
            String endDel = delSetting.substring(delSetting.indexOf(";"));
            int delIndex = filterString.indexOf(delSetting);
            int argIndex = delSetting.indexOf(argDel);
            int endIndex = delSetting.indexOf(endDel);

            if (delIndex != -1) {
                return delSetting.substring(argIndex + 1,endIndex);
            }
        }

        return "";
    }

    private int stringToInteger(String strNum){
        int result;
        result = Integer.parseInt(strNum);
        return result;
    }
}
