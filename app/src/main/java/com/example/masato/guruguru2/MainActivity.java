package com.example.masato.guruguru2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private Button btn_menu1;
//    private Button btn_menu2;
//    private Button btn_menu3;
//    private Button btn_menu4;
    private Button btn_menu5, btn_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_menu5 = (Button) this.findViewById(R.id.main_btn_5);
        btn_menu5.setOnClickListener(this);

        btn_tag = (Button) this.findViewById(R.id.btn_main_tag);
        btn_tag.setOnClickListener(this);

        AppStatics.getInstance().resetSelectScenarios();
        AppStatics.getInstance().resetSelectScenarioIndexes();
    }

    @Override
    public void onClick(View view) {

        if(view == btn_menu5){

            Intent intent = new Intent(this, ScenarioListActivity.class);
            //startActivity(intent);
            startActivityForResult(intent, 999);

        }else if(view == btn_tag){

            Intent intent = new Intent(this, HtmlTagPickerActivity.class);
            //startActivity(intent);
            startActivityForResult(intent, 999);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            AppStatics.getInstance().resetSelectScenarios();
            AppStatics.getInstance().resetSelectScenarioIndexes();
        }

    }
}
