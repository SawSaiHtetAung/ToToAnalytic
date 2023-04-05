package com.safeseason.totoanalytic.NewsLetters;

import android.text.TextUtils;

public class NewsLetterData {
    private final String mTitle;
    private final String mDate;
    private final String mBody;
    private final String mSource;
    private boolean getShort = true;

    public NewsLetterData(String mTitle, String mDate, String mBody, String mSource) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mBody = mBody;
        this.mSource = mSource;
    }

    public String getShortBody(){
        getShort = true;
        if (!TextUtils.isEmpty(mBody)) {
            if (mBody.length() < 60)
                return mBody;
            else
                return mBody.substring(0, 57) + "...";
        } else{
            return "";
        }
    }

    public boolean isShort(){
        return getShort;
    }

    public String getTitle(){
        return  mTitle;
    }

    public String getBody(){
        getShort = false;
        return mBody;
    }

    public String getDate(){
        return mDate;
    }

    public String getSource(){
        if (TextUtils.isEmpty(mSource))
            return "Source: unknown";

        return "Source: " + mSource;
    }
}
