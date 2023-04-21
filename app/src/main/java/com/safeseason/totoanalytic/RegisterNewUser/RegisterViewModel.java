package com.safeseason.totoanalytic.RegisterNewUser;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class RegisterViewModel  extends ViewModel {

    public MutableLiveData<Boolean> USER_AGREEMENT_FLAG = new MutableLiveData<>();
    public MutableLiveData<String> onClick = new MutableLiveData<>();
    public MutableLiveData<String> loginTask = new MutableLiveData<>();

    public LiveData<Boolean> userAgreementRet(){
        return USER_AGREEMENT_FLAG;
    }
    public void userAgreementChk(boolean flag){
        USER_AGREEMENT_FLAG.postValue(flag);
    }

    public void signUpClick(){
        onClick.postValue("Welcome to sign Up");
    }

    public LiveData<String> setSignUp(){
        return onClick;
    }

    public void setLoginTask(String str){
        loginTask.postValue(str);
    }

    public LiveData<String> checkLogin(){
        return loginTask;
    }
}
