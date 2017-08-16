package com.example.masato.guruguru2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;

import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

/**
 * Created by masato on 2017/08/16.
 */

public class HtmlTagPickerActivity extends AppCompatActivity implements PickerListener, View.OnClickListener {

    private Button btn_go, btn_prev, btn_next, btn_reload, btn_pick;
    private EditText et_url;

    private XWalkView xWalkView;
    private PickerResourceClient pickerResourceClient;
    private PickerNotify pickerNotify;
    private String firstUrl = "http://google.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_picker);

        btn_go = (Button)this.findViewById(R.id.btn_pick_go);
        btn_go.setOnClickListener(this);
        btn_prev = (Button)this.findViewById(R.id.btn_pick_prev);
        btn_prev.setOnClickListener(this);
        btn_next = (Button)this.findViewById(R.id.btn_pick_next);
        btn_next.setOnClickListener(this);
        btn_reload = (Button)this.findViewById(R.id.btn_pick_reload);
        btn_reload.setOnClickListener(this);
        btn_pick = (Button)this.findViewById(R.id.btn_pick_pick);
        btn_pick.setOnClickListener(this);

        et_url = (EditText) this.findViewById(R.id.et_pick_url);
        et_url.setText(firstUrl);

        pickerNotify = new PickerNotify();


        xWalkView = (XWalkView) findViewById(R.id.web_tagPicker);
        xWalkView.setEnabled(false);

        xWalkView.setUIClient(new PickerUIClient(xWalkView));
    }


    @Override
    public void onClick(View view) {

        if(view==btn_go){
            String url = et_url.getText().toString();

        }else if(view==btn_prev){
            if(xWalkView.getNavigationHistory().canGoBack()){
                xWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
            }

        }else if(view==btn_next){
            if(xWalkView.getNavigationHistory().canGoForward()){
                xWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.FORWARD, 1);
            }

        }else if(view==btn_reload){
            xWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.FORWARD, 0);

        }else if(view==btn_pick){

        }
    }

    @Override
    public void sendTags(String tags) {

    }

    @Override
    public void notifyUrl(String url) {

    }
}


class PickerResourceClient extends XWalkResourceClient {

    private XWalkView xWalkView;
    private PickerNotify pickerNotify;

    private String jsCode = "document.querySelector(\"body\").innerText";

    public PickerResourceClient(XWalkView view, PickerNotify pn) {
        super(view);

        this.xWalkView = view;
        this.pickerNotify = pn;
    }

    @Override
    public void onLoadFinished(XWalkView view, String url) {
        super.onLoadFinished(view, url);

    }

    public void getAllTags(){
        //xWalkView.evaluateJavascript(jsCode, new PickerValueCallBack());
    }


    class PickerValueCallBack implements ValueCallback {

        PickerResourceClient pickerResourceClient;
        PickerNotify pickerNotify;

        public PickerValueCallBack(PickerResourceClient prc, PickerNotify pn){
            this.pickerResourceClient = prc;
            this.pickerNotify = pn;
        }

        @Override
        public void onReceiveValue(Object o) {
            pickerNotify.sendTags(o.toString());
        }
    }
}


class PickerUIClient extends XWalkUIClient {


    PickerUIClient(XWalkView view) {
        super(view);

    }



}
