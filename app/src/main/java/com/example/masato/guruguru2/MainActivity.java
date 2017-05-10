package com.example.masato.guruguru2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_menu1;
    private Button btn_menu2;
    private Button btn_menu3;
    private Button btn_menu4;
    private Button btn_menu5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_menu1 = (Button) this.findViewById(R.id.main_btn_1);
        btn_menu1.setOnClickListener(this);

        btn_menu2 = (Button) this.findViewById(R.id.main_btn_2);
        btn_menu2.setOnClickListener(this);

        btn_menu3 = (Button) this.findViewById(R.id.main_btn_3);
        btn_menu3.setOnClickListener(this);

        btn_menu4 = (Button) this.findViewById(R.id.main_btn_4);
        btn_menu4.setOnClickListener(this);

        btn_menu5 = (Button) this.findViewById(R.id.main_btn_5);
        btn_menu5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_menu1){

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
            Log.d("intent", "1");
        }else if(view == btn_menu2){

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("mode", 2);
            startActivity(intent);
            Log.d("intent", "2");
        }else if(view == btn_menu3){

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("mode", 3);
            startActivity(intent);
            Log.d("intent", "3");
        }else if(view == btn_menu4){

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("mode", 4);
            startActivity(intent);
            Log.d("intent", "4");
        }else if(view == btn_menu5){

            Intent intent = new Intent(this, ScenarioListActivity.class);
            //intent.putExtra("mode", 4);
            startActivity(intent);
            Log.d("intent", "5");
        }
    }
}
