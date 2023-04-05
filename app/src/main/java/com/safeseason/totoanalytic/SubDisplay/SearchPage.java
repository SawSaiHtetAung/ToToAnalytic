package com.safeseason.totoanalytic.SubDisplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.safeseason.totoanalytic.Helper.DataProcessing;
import com.safeseason.totoanalytic.R;

import java.util.ArrayList;

public class SearchPage extends Fragment implements View.OnClickListener {
    ArrayList<String> searchDate = new ArrayList<>();
    ArrayList<String> digit1 = new ArrayList<>();
    ArrayList<String> digit2 = new ArrayList<>();
    ArrayList<String> digit3 = new ArrayList<>();
    ArrayList<String> digit4 = new ArrayList<>();
    ArrayList<String> digit5 = new ArrayList<>();
    ArrayList<String> digit6 = new ArrayList<>();
    ArrayList<String> additional = new ArrayList<>();

    //Set to original state
    ArrayList<String> tempSearchDate = new ArrayList<>();
    ArrayList<String> tempDigit1 = new ArrayList<>();
    ArrayList<String> tempDigit2 = new ArrayList<>();
    ArrayList<String> tempDigit3 = new ArrayList<>();
    ArrayList<String> tempDigit4 = new ArrayList<>();
    ArrayList<String> tempDigit5 = new ArrayList<>();
    ArrayList<String> tempDigit6 = new ArrayList<>();
    ArrayList<String> tempAdditional = new ArrayList<>();

    SearchPageAdaptor searchAdaptor;
    MaterialTextView filterDisplay;

    MaterialButton filterDigit1, filterDigit2, filterDigit3, filterDigit4, filterDigit5, filterDigit6;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_page, container, false);

        //Set resources
        Spinner daySpinner = view.findViewById(R.id.searchDayInput);
        Spinner monthSpinner = view.findViewById(R.id.searchMonthInput);
        Spinner yearSpinner = view.findViewById(R.id.searchYearInput);
        filterDigit1 = view.findViewById(R.id.filterDigit1);
        filterDigit2 = view.findViewById(R.id.filterDigit2);
        filterDigit3 = view.findViewById(R.id.filterDigit3);
        filterDigit4 = view.findViewById(R.id.filterDigit4);
        filterDigit5 = view.findViewById(R.id.filterDigit5);
        filterDigit6 = view.findViewById(R.id.filterDigit6);
        filterDisplay = view.findViewById(R.id.searchOutput);
        ListView searchListView = view.findViewById(R.id.searchListView);

        //Adding spinner adaptor lists
        ArrayAdapter<CharSequence> dayAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.daySelected, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> monthAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.monthSelected, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> yearAdaptorList = ArrayAdapter.createFromResource(getContext(),
                R.array.yearSelected, android.R.layout.simple_spinner_dropdown_item);


        //Set spinner view adaptor
        daySpinner.setAdapter(dayAdaptorList);
        monthSpinner.setAdapter(monthAdaptorList);
        yearSpinner.setAdapter(yearAdaptorList);


        //Set spinner listeners
        daySpinner.setOnItemSelectedListener(filterListener);
        monthSpinner.setOnItemSelectedListener(filterListener);
        yearSpinner.setOnItemSelectedListener(filterListener);

        //Set button click listener
        filterDigit1.setOnClickListener(this);
        filterDigit2.setOnClickListener(this);
        filterDigit3.setOnClickListener(this);
        filterDigit4.setOnClickListener(this);
        filterDigit5.setOnClickListener(this);
        filterDigit6.setOnClickListener(this);

        //Get data from Data Processing
        searchDate = new ArrayList<>(DataProcessing.orgDate);
        digit1 = new ArrayList<>(DataProcessing.orgDigital1);
        digit2 = new ArrayList<>(DataProcessing.orgDigital2);
        digit3 = new ArrayList<>(DataProcessing.orgDigital3);
        digit4 = new ArrayList<>(DataProcessing.orgDigital4);
        digit5 = new ArrayList<>(DataProcessing.orgDigital5);
        digit6 = new ArrayList<>(DataProcessing.orgDigital6);
        additional = new ArrayList<>(DataProcessing.orgAdditional);

        tempSearchDate = new ArrayList<>(DataProcessing.orgDate);
        tempDigit1 = new ArrayList<>(DataProcessing.orgDigital1);
        tempDigit2 = new ArrayList<>(DataProcessing.orgDigital2);
        tempDigit3 = new ArrayList<>(DataProcessing.orgDigital3);
        tempDigit4 = new ArrayList<>(DataProcessing.orgDigital4);
        tempDigit5 = new ArrayList<>(DataProcessing.orgDigital5);
        tempDigit6 = new ArrayList<>(DataProcessing.orgDigital6);
        tempAdditional = new ArrayList<>(DataProcessing.orgAdditional);

        //List View set adaptor
        searchAdaptor = new SearchPageAdaptor(getContext(), searchDate, digit1, digit2, digit3, digit4,
                digit5, digit6, additional);
        searchListView.setAdapter(searchAdaptor);

        return view;
    }

    AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            String filter = "";
            switch (adapterView.getId()){
                case R.id.searchDayInput:
                    filter = "Day:" + adapterView.getSelectedItem().toString();
                    break;
                case R.id.searchMonthInput:
                    filter = "Month:" + adapterView.getSelectedItem().toString();
                    break;
                case R.id.searchYearInput:
                    filter = "Year:" + adapterView.getSelectedItem().toString();
                    break;
            }
            System.out.println(filter);
            filterInputChange(filter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onClick(View view) {

        String[] selectedValue = {"N/A"};

        //Show the pick number dialog
        AlertDialog alertDialog;
        alertDialog = buildDialog(value -> {
            selectedValue[0] = value;

            String filter = "";
            switch (view.getId()){
                case R.id.filterDigit1:
                    filter = "Num1:" + selectedValue[0];
                    filterDigit1.setText(selectedValue[0]);
                    break;
                case R.id.filterDigit2:
                    filter = "Num2:" + selectedValue[0];
                    filterDigit2.setText(selectedValue[0]);
                    break;
                case R.id.filterDigit3:
                    filter = "Num3:" + selectedValue[0];
                    filterDigit3.setText(selectedValue[0]);
                    break;
                case R.id.filterDigit4:
                    filter = "Num4:" + selectedValue[0];
                    filterDigit4.setText(selectedValue[0]);
                    break;
                case R.id.filterDigit5:
                    filter = "Num5:" + selectedValue[0];
                    filterDigit5.setText(selectedValue[0]);
                    break;
                case R.id.filterDigit6:
                    filter = "Num6:" + selectedValue[0];
                    filterDigit6.setText(selectedValue[0]);
                    break;
            }
            filterInputChange(filter);
        });
        alertDialog.show();
    }


    void filterInputChange(String s){
        //Copy previous input query string

        String filterPre = filterDisplay.getText().toString();
        StringBuilder filterDisplayStr = new StringBuilder(filterPre);

        //Set global variable
        String filterDay, filterMonth, filterYear;
        String filterNum1, filterNum2, filterNum3,
                filterNum4, filterNum5, filterNum6;

        //Check no filter display
        if (filterDisplayStr.toString().equals("No filter"))
            filterDisplayStr = new StringBuilder();


        //Check filter is on Date
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Day", null);
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Month", null);
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Year", null);
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num1", "N/A");
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num2", "N/A");
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num3", "N/A");
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num4", "N/A");
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num5", "N/A");
        filterDisplayStr = dataQuery(filterDisplayStr.toString(), s, "Num6", "N/A");


        //This function must be place below
        //If there is no filter, display "No filter" in display query
        if (!filterDisplayStr.toString().contains("Day") && !filterDisplayStr.toString().contains("Month") &&
                !filterDisplayStr.toString().contains("Year") && !filterDisplayStr.toString().contains("Num1")
                && !filterDisplayStr.toString().contains("Num2") && !filterDisplayStr.toString().contains("Num3")
                && !filterDisplayStr.toString().contains("Num4") && !filterDisplayStr.toString().contains("Num5")
                && !filterDisplayStr.toString().contains("Num6")){
            filterDisplayStr = new StringBuilder("No filter");

            //Clear all temporary array list
            clearData();

            for (int j = 0; j < tempSearchDate.size(); j++) {
                //Add line
                searchDate.add(tempSearchDate.get(j));
                DataProcessing.searchByDate.add(tempSearchDate.get(j));
                digit1.add(tempDigit1.get(j));
                DataProcessing.searchByDigital1.add(tempDigit1.get(j));
                digit2.add(tempDigit2.get(j));
                DataProcessing.searchByDigital2.add(tempDigit2.get(j));
                digit3.add(tempDigit3.get(j));
                DataProcessing.searchByDigital3.add(tempDigit3.get(j));
                digit4.add(tempDigit4.get(j));
                DataProcessing.searchByDigital4.add(tempDigit4.get(j));
                digit5.add(tempDigit5.get(j));
                DataProcessing.searchByDigital5.add(tempDigit5.get(j));
                digit6.add(tempDigit6.get(j));
                DataProcessing.searchByDigital6.add(tempDigit6.get(j));
                additional.add(tempAdditional.get(j));
                DataProcessing.searchByAdditional.add(tempAdditional.get(j));
            }

        } else {
            //Todo array adaptor list filter
            filterDay = dataFilterQuery(filterDisplayStr, "Day");
            filterMonth = dataFilterQuery(filterDisplayStr, "Month");
            filterYear = dataFilterQuery(filterDisplayStr, "Year");
            filterNum1 = dataFilterQuery(filterDisplayStr, "Num1");
            filterNum2 = dataFilterQuery(filterDisplayStr, "Num2");
            filterNum3 = dataFilterQuery(filterDisplayStr, "Num3");
            filterNum4 = dataFilterQuery(filterDisplayStr, "Num4");
            filterNum5 = dataFilterQuery(filterDisplayStr, "Num5");
            filterNum6 = dataFilterQuery(filterDisplayStr, "Num6");


            //Clear all temporary array list
            clearData();

            for (int i=0; i< tempSearchDate.size(); i++){
                String filterQuery = " " + tempDigit1.get(i) + " " + tempDigit2.get(i) + " " + tempDigit3.get(i) +
                        " " + tempDigit4.get(i) + " " + tempDigit5.get(i) + " " + tempDigit6.get(i) + " " + tempAdditional.get(i);

                if  (tempSearchDate.get(i).contains(filterDay) && tempSearchDate.get(i).contains(filterMonth) && tempSearchDate.get(i).contains(filterYear)
                        && filterQuery.contains(filterNum1) && filterQuery.contains(filterNum2) && filterQuery.contains(filterNum3) && filterQuery.contains(filterNum4)
                        && filterQuery.contains(filterNum5) && filterQuery.contains(filterNum6)){
                    //Add line
                    searchDate.add(tempSearchDate.get(i));
                    DataProcessing.searchByDate.add(tempSearchDate.get(i));
                    digit1.add(tempDigit1.get(i));
                    DataProcessing.searchByDigital1.add(tempDigit1.get(i));
                    digit2.add(tempDigit2.get(i));
                    DataProcessing.searchByDigital2.add(tempDigit2.get(i));
                    digit3.add(tempDigit3.get(i));
                    DataProcessing.searchByDigital3.add(tempDigit3.get(i));
                    digit4.add(tempDigit4.get(i));
                    DataProcessing.searchByDigital4.add(tempDigit4.get(i));
                    digit5.add(tempDigit5.get(i));
                    DataProcessing.searchByDigital5.add(tempDigit5.get(i));
                    digit6.add(tempDigit6.get(i));
                    DataProcessing.searchByDigital6.add(tempDigit6.get(i));
                    additional.add(tempAdditional.get(i));
                    DataProcessing.searchByAdditional.add(tempAdditional.get(i));
                }
            }
        }

        searchAdaptor.notifyDataSetChanged();
        filterDisplay.setText(filterDisplayStr.toString());

        //Sent update data
        DataProcessing.globalFilter = filterDisplayStr.toString();
    }

    private StringBuilder dataQuery(String textString, String queryString, String filter, @Nullable String subFilter){
        StringBuilder filterString = new StringBuilder(textString);
        if (queryString.contains(filter)){
            if (textString.contains(filter)){
                String deleteYearSetting = textString.substring(textString.indexOf(filter));
                String spaceDelete = deleteYearSetting.substring(deleteYearSetting.indexOf(";"));
                int yearDeleteIndex = textString.indexOf(deleteYearSetting);
                int spaceIndex = deleteYearSetting.indexOf(spaceDelete);

                if (yearDeleteIndex != -1)
                    filterString.delete(yearDeleteIndex, yearDeleteIndex + spaceIndex + 1);
            }
            //If selected year, append to string builder
            if(!queryString.equals(filter + ":" + filter) && !queryString.equals(filter + ":" + subFilter)){
                filterString.append(queryString).append(";");
            }
        }

        return filterString;
    }

    private void clearData (){
        searchDate.clear();
        DataProcessing.searchByDate.clear();
        digit1.clear();
        DataProcessing.searchByDigital1.clear();
        digit2.clear();
        DataProcessing.searchByDigital2.clear();
        digit3.clear();
        DataProcessing.searchByDigital3.clear();
        digit4.clear();
        DataProcessing.searchByDigital4.clear();
        digit5.clear();
        DataProcessing.searchByDigital5.clear();
        digit6.clear();
        DataProcessing.searchByDigital6.clear();
        additional.clear();
        DataProcessing.searchByAdditional.clear();
    }

    String dataFilterQuery(StringBuilder sBuilder, String filterString){
        if (sBuilder.toString().contains(filterString)){
            String preDate = sBuilder.substring(sBuilder.indexOf(filterString));
            String preEnd = preDate.substring(preDate.indexOf(";"));

            int preDateIndex = sBuilder.indexOf(preDate);
            int preEndIndex = preDate.indexOf(preEnd);

            if (preDateIndex != -1) {
                sBuilder.append(" ");
                return sBuilder.substring(preDateIndex + filterString.length() + 1, preDateIndex + preEndIndex);
            }
        }
        return "";
    }

    @SuppressLint("DefaultLocale")
    private AlertDialog buildDialog(PickNumber number) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.number_picker, null);

        dialogBuilder.setView(dialogView);

        final String[] value = {"N/A"};
        String[] filterItems = new String[50];
        filterItems[0] = "N/A";
        for (int j=1; j<=49; j++)
            filterItems[j] = String.format("%02d", j);

        NumberPicker picker = dialogView.findViewById(R.id.numberPicker);
        MaterialButton confirmBtn = dialogView.findViewById(R.id.numberConfirmBtn);
        picker.setDisplayedValues(filterItems);
        picker.setMinValue(0);
        picker.setMaxValue(filterItems.length - 1);
        picker.setWrapSelectorWheel(true);

        confirmBtn.setOnClickListener(view -> {
            dialogBuilder.dismiss();
            number.userSelectedValue(value[0]);
        });

        picker.setOnValueChangedListener((numberPicker, oldValue, newValue) -> value[0] = filterItems[newValue]);

        return dialogBuilder;
    }

    private interface PickNumber{
        void userSelectedValue(String value);
    }
}
