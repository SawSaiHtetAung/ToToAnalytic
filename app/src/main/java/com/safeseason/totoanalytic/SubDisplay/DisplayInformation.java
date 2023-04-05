package com.safeseason.totoanalytic.SubDisplay;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.safeseason.totoanalytic.NewsLetters.NewsLetterAdaptor;
import com.safeseason.totoanalytic.NewsLetters.NewsLetterData;
import com.safeseason.totoanalytic.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayInformation extends Fragment {
    //Global variable
    List<NewsLetterData> mNewsLettersData = new ArrayList<>();
    NewsLetterAdaptor mNewsLettersAdaptor;

    //To get data from server
    FirebaseDatabase newsLettersDatabase;
    DatabaseReference databaseReference;

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_information, container, false);

        //News letter code here
        RecyclerView mNewsLetters = view.findViewById(R.id.informationRecycler);
        //Making divider recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //mNewsLetters.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).setDrawable(););
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = ContextCompat.getDrawable(requireContext(), R.drawable.divider);
        assert verticalDivider != null;
        verticalDecoration.setDrawable(verticalDivider);
        mNewsLetters.addItemDecoration(verticalDecoration);
        mNewsLetters.setLayoutManager(linearLayoutManager);

        //Adding news letter data (for example purpose)
        newsLettersDatabase = FirebaseDatabase.getInstance("https://totodataanalysis-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference  = newsLettersDatabase.getReference().child("News Letter");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String mTitle = snapshot.child("Title").getValue(String.class);
                String mBody = snapshot.child("Body").getValue(String.class);
                String mDate = snapshot.child("Date").getValue(String.class);
                String mSource = snapshot.child("Source").getValue(String.class);

                NewsLetterData mData = new NewsLetterData(mTitle, mDate, mBody, mSource);
                mNewsLettersData.add(mData);
                mNewsLettersAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mNewsLettersAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mNewsLettersAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get time stamp from installation
        String newsLettersDate;
        try {
            Long installed = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).firstInstallTime;
            System.out.println(installed);
            newsLettersDate = new SimpleDateFormat("EE, dd MMM yyyy").format(new Date(installed));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Set adaptor list into the list

        NewsLetterData mData = new NewsLetterData(getString(R.string.newsLettersTitle), newsLettersDate, getString(R.string.newsLetterBody),
                getString(R.string.newsLetterSources));
        mNewsLettersData.add(mData);

        mNewsLettersAdaptor = new NewsLetterAdaptor(getContext(), mNewsLettersData);
        mNewsLetters.setAdapter(mNewsLettersAdaptor);

        //Return the view
        return view;
    }
}
