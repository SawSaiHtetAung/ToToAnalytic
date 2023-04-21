package com.safeseason.totoanalytic.Helper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class AdvFilterViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> advFilterString = new MutableLiveData<>();

    public void setAdvFilterString(ArrayList<String> saveSetting){
        advFilterString.postValue(saveSetting);
    }

    public LiveData<ArrayList<String>> getAdvFilterString(){
        return advFilterString;
    }
}
