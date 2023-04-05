package com.safeseason.totoanalytic.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.safeseason.totoanalytic.Helper.UpdateSetChecked;
import com.safeseason.totoanalytic.R;

import java.util.ArrayList;

public class ConfigLoadAdaptor extends BaseAdapter {
    ArrayList<String> loadFile;
    LayoutInflater mInflater;
    boolean[] checkedStatus;
    UpdateSetChecked updateSetChecked;

    public ConfigLoadAdaptor(Context mContext, ArrayList<String> loadFile) {
        mInflater = LayoutInflater.from(mContext);
        this.loadFile = loadFile;
        checkedStatus = new boolean[loadFile.size()];
    }

    //Interface with main program
    public void setUpdateSetChecked(UpdateSetChecked updateSetChecked){
        this.updateSetChecked = updateSetChecked;
    }

    @Override
    public int getCount() {
        return loadFile.size();
    }

    @Override
    public Object getItem(int position) {
        return loadFile.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row;
        ConfigLoadHolder holder;
        if (convertView == null){
            row = mInflater.inflate(R.layout.config_list_adaptor, null);
            holder = new ConfigLoadHolder();
            holder.configFile = row.findViewById(R.id.configFormula);

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ConfigLoadHolder) row.getTag();
        }
        //Load title of config file
        holder.configFile.setText(loadFile.get(position));
        holder.configFile.setOnClickListener(view -> {
            checkedStatus[position] = !checkedStatus[position];
            if (updateSetChecked != null) //Interface with main program
                updateSetChecked.onCheckedListener(position, checkedStatus[position]);
        });
        holder.configFile.setChecked(checkedStatus[position]);

        return row;
    }

    static class ConfigLoadHolder{
        CheckBox configFile;

    }
}
