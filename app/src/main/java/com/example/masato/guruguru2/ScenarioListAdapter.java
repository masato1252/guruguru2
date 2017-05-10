package com.example.masato.guruguru2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by masato on 2017/05/04.
 */

public class ScenarioListAdapter extends ArrayAdapter<ScenarioIndex> {


    private LayoutInflater layoutInflater_;

    public ScenarioListAdapter(Context context, int textViewResourceId, List<ScenarioIndex> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class ViewHolder {
        TextView text_name;
        TextView text_memo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        ScenarioIndex scenarioIndex = (ScenarioIndex) getItem(position);
        ScenarioListAdapter.ViewHolder holder;

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.layout_listcell_sc, null);

            holder = new ScenarioListAdapter.ViewHolder();
            holder.text_name = (TextView)convertView.findViewById(R.id.listcell_sc_name);
            holder.text_memo = (TextView)convertView.findViewById(R.id.listcell_sc_memo);

            convertView.setTag(holder);
        } else {

            holder = (ScenarioListAdapter.ViewHolder)convertView.getTag();
        }

        holder.text_name.setText(scenarioIndex.getName());
        holder.text_memo.setText(scenarioIndex.getMemo());


        if(!AppStatics.getInstance().selectScenarios.get(position)) {
            convertView.setBackgroundColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.GREEN);
        }

        return convertView;
    }



}
