package com.safeseason.totoanalytic.SubDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safeseason.totoanalytic.R;

import java.util.ArrayList;

public class SearchPageAdaptor extends BaseAdapter {
    LayoutInflater mLayoutInflater;
    ArrayList<String> date;
    ArrayList<String> digit1;
    ArrayList<String> digit2;
    ArrayList<String> digit3;
    ArrayList<String> digit4;
    ArrayList<String> digit5;
    ArrayList<String> digit6;
    ArrayList<String> additional;

    public SearchPageAdaptor(Context context, ArrayList<String> date, ArrayList<String> digit1,
                             ArrayList<String> digit2, ArrayList<String> digit3, ArrayList<String> digit4,
                             ArrayList<String> digit5, ArrayList<String> digit6, ArrayList<String> additional) {
        mLayoutInflater = LayoutInflater.from(context);
        this.date = date;
        this.digit1 = digit1;
        this.digit2 = digit2;
        this.digit3 = digit3;
        this.digit4 = digit4;
        this.digit5 = digit5;
        this.digit6 = digit6;
        this.additional = additional;
    }

    @Override
    public int getCount() {
        return date.size();
    }

    @Override
    public Object getItem(int position) {
        return date.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row;
        SearchPageViewHolder viewHolder;
        if (convertView == null){
            row = mLayoutInflater.inflate(R.layout.search_list_adaptor, null);
            viewHolder = new SearchPageViewHolder();
            viewHolder.date = row.findViewById(R.id.searchDate);
            viewHolder.digit1 = row.findViewById(R.id.searchDigit1);
            viewHolder.digit2 = row.findViewById(R.id.searchDegit2);
            viewHolder.digit3 = row.findViewById(R.id.searchDigit3);
            viewHolder.digit4 = row.findViewById(R.id.searchDigit4);
            viewHolder.digit5 = row.findViewById(R.id.searchDigit5);
            viewHolder.digit6 = row.findViewById(R.id.searchDigit6);
            viewHolder.additional = row.findViewById(R.id.searchDigitAdditional);

            row.setTag(viewHolder);
        } else {
            row = convertView;
            viewHolder = (SearchPageViewHolder) row.getTag();
        }

        //Set Value for database
        viewHolder.date.setText(date.get(position));
        viewHolder.digit1.setText(digit1.get(position));
        viewHolder.digit2.setText(digit2.get(position));
        viewHolder.digit3.setText(digit3.get(position));
        viewHolder.digit4.setText(digit4.get(position));
        viewHolder.digit5.setText(digit5.get(position));
        viewHolder.digit6.setText(digit6.get(position));
        viewHolder.additional.setText(additional.get(position));

        return row;
    }

    static class SearchPageViewHolder{
        TextView date;
        TextView digit1, digit2, digit3, digit4, digit5, digit6;
        TextView additional;
    }
}
