package com.example.masato.guruguru2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by masato on 2017/04/23.
 */

public class LogListAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater_;

    public LogListAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        String log = (String)getItem(position);
        ViewHolder holder;

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.layout_listcell_log, null);

            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(R.id.listcell_log_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textView.setText(log);


        return convertView;
    }

}
