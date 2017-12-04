package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

/**
 * Created by masato on 2017/05/04.
 */

public class ScenarioListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView listView_sc;
    private List<ScenarioIndex> list_sc;
    private ScenarioListAdapter scListAdapter;

    private Button btn_1, btn_allsel, btn_allrel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_list);

        list_sc = new ArrayList<ScenarioIndex>();
        scListAdapter = new ScenarioListAdapter(this, 0, list_sc);
        listView_sc = (ListView) this.findViewById(R.id.sc_listview);
        listView_sc.setAdapter(scListAdapter);
        listView_sc.setOnItemClickListener(this);

        btn_1 = (Button) this.findViewById(R.id.sclist_btn1);
        btn_1.setOnClickListener(this);

        btn_allsel = (Button) this.findViewById(R.id.sclist_btn_allsel);
        btn_allsel.setOnClickListener(this);

        btn_allrel = (Button) this.findViewById(R.id.sclist_btn_allrel);
        btn_allrel.setOnClickListener(this);

        AppStatics.getInstance().resetSelectScenarioIndexes();
        ScenarioIndexApi scenarioIndexApi = new ScenarioIndexApi(this, list_sc, scListAdapter);
        scenarioIndexApi.setOnCallBack(new ScenarioIndexApi.CallBackTask(){
            @Override
            public void CallBack(Integer result) {
                for(int i=0; i<list_sc.size(); i++){
                    AppStatics.getInstance().selectScenarios.add(false);
                }
            }
        });

        scenarioIndexApi.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {

            Intent intent = new Intent();
            //intent.putExtra("text", "終了");
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent();
        //intent.putExtra("text", "終了");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(AppStatics.getInstance().selectScenarios.size()>0) {
            if (!AppStatics.getInstance().selectScenarios.get(i)) {
                AppStatics.getInstance().selectScenarios.set(i, true);
                view.setBackgroundColor(Color.GREEN);
            } else {
                AppStatics.getInstance().selectScenarios.set(i, false);
                view.setBackgroundColor(Color.WHITE);
            }
        }


    }

    @Override
    public void onClick(View view) {

        if(view == btn_1){
            int count = 0;
            for(int i=0; i<list_sc.size(); i++){
                if(AppStatics.getInstance().selectScenarios.get(i)){
                    AppStatics.getInstance().selectScenarioIndexes.add(list_sc.get(i));
                    count++;
                }
            }

            if(count>0){
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("mode", 100);
                //startActivity(intent);
                startActivityForResult(intent, 999);

            }else{
                //未選択エラー
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
                alertDialog.setTitle("エラー");      //タイトル設定
                alertDialog.setMessage("シナリオを１つ以上選択してください。");  //内容(メッセージ)設定
                // OK(肯定的な)ボタンの設定
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OKボタン押下時の処理

                    }
                });
                alertDialog.show();
            }

        }else if(view == btn_allsel){
            //全選択

            for(int i=0; i<list_sc.size(); i++){
                AppStatics.getInstance().selectScenarios.set(i, true);
            }
            scListAdapter.notifyDataSetChanged();

        }else if(view == btn_allrel){
            //全解除

            for(int i=0; i<list_sc.size(); i++){
                AppStatics.getInstance().selectScenarios.set(i, false);
            }
            scListAdapter.notifyDataSetChanged();
        }

    }
}
