package com.safeseason.totoanalytic.NewsLetters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.safeseason.totoanalytic.R;

import java.util.List;

public class NewsLetterAdaptor extends RecyclerView.Adapter<NewsLetterAdaptor.NewsLettersHolder>{
    private final List<NewsLetterData> mNewsLetterData;

    public NewsLetterAdaptor(Context mContext, List<NewsLetterData> mNewsLetterData){
        this.mNewsLetterData = mNewsLetterData;
    }

    @NonNull
    @Override
    public NewsLettersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_letters, parent, false);
        return new NewsLettersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsLettersHolder holder, int position) {
        holder.mNewsLettersTitle.setText(mNewsLetterData.get(position).getTitle());
        holder.mNewsLetterBody.setText(mNewsLetterData.get(position).getShortBody());
        holder.mNewsLettersDate.setText(mNewsLetterData.get(position).getDate());
        holder.mNewsLetterSource.setText(mNewsLetterData.get(position).getSource());

        holder.mLayout.setOnClickListener(view -> {
            if (mNewsLetterData.get(position).isShort())
                holder.mNewsLetterBody.setText(mNewsLetterData.get(position).getBody());
            else
                holder.mNewsLetterBody.setText(mNewsLetterData.get(position).getShortBody());
        });
    }


    @Override
    public int getItemCount() {
        return mNewsLetterData.size();
    }



    static class NewsLettersHolder extends RecyclerView.ViewHolder{

        //Set the global variable
        TextView mNewsLetterBody;
        TextView mNewsLettersDate;
        TextView mNewsLetterSource;
        TextView mNewsLettersTitle;
        ConstraintLayout mLayout;

        public NewsLettersHolder(@NonNull View itemView) {
            super(itemView);

            mNewsLettersDate = itemView.findViewById(R.id.newsLetterDate);
            mNewsLettersTitle = itemView.findViewById(R.id.newsLettersTitle);
            mNewsLetterSource = itemView.findViewById(R.id.newsLettersSource);
            mNewsLetterBody = itemView.findViewById(R.id.newsLettersBody);
            mLayout = itemView.findViewById(R.id.newsLettersLayout);
        }
    }
}
