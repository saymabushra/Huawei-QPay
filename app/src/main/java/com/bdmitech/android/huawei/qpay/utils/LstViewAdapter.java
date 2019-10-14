package com.bdmitech.android.huawei.qpay.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.R;

import java.util.ArrayList;

/**
 * @author Mr. Nazmuzzaman, Umme Sayma Bushra, Muhammad Sadat Al-Jony
 * @version 1.0
 * @company Bangladesh Microtechnology Limited
 * @since 2015-02-01
 */

public class LstViewAdapter extends ArrayAdapter<String> {
    int groupid;
    ArrayList<String> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewAdapter(Context context, int vg, int id, ArrayList<String> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textview;
        public Button button;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the list_item.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview = (TextView) rowView.findViewById(R.id.txt);
            viewHolder.button = (Button) rowView.findViewById(R.id.bt);
            viewHolder.button = (Button) rowView.findViewById(R.id.btdelete);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));

//        holder.button.setText("Add");
        return rowView;
    }



}
