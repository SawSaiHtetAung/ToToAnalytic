package com.safeseason.totoanalytic.SubDisplay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.safeseason.totoanalytic.R;

public class MainPage extends Fragment {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        //Todo code in here
        WebView totoResultView = view.findViewById(R.id.mainPageWebView);
        totoResultView.getSettings().setJavaScriptEnabled(true);
        totoResultView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(getContext(), error.getDescription().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        //Set gable variable
        String url = "https://www.singaporepools.com.sg/en/product/pages/toto_results.aspx";
        totoResultView.loadUrl(url);

        return view;
    }
}
