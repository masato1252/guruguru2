package com.example.masato.guruguru2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;

import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_picker);

        this.activity = this;

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
        pickerNotify.setListener(this);

        xWalkView = (XWalkView) findViewById(R.id.web_tagPicker);
        xWalkView.setEnabled(false);

        pickerResourceClient = new PickerResourceClient(xWalkView, this, pickerNotify);
        xWalkView.setResourceClient(pickerResourceClient);
        xWalkView.setUIClient(new PickerUIClient(xWalkView));
        xWalkView.loadUrl(firstUrl);
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
            pickerResourceClient.getAllTags();
        }
    }

    @Override
    public void sendTags(String tags, String url, String title) {

        String fileName = "";
        Date date = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd-HHmmss");
        fileName = "pick_" + sdf1.format(date).toString() + ".txt";

        if(saveFile(fileName, tags)) {

            PickerApi pickerApi = new PickerApi(this, 1, fileName, url, title);
            pickerApi.setOnCallBack(new PickerApi.CallBackTask() {

                @Override
                public void callBack(Integer result) {
                    super.callBack(result);

                    if (result == 1) {
                        //送信完了
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setTitle("ピック完了");      //タイトル設定
                        alertDialog.setMessage("サーバへHTMLタグを送信しました。");  //内容(メッセージ)設定
                        // OK(肯定的な)ボタンの設定
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OKボタン押下時の処理
                                //finish();
                            }
                        });
                        alertDialog.show();

                    } else {
                        //送信失敗
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setTitle("エラー");      //タイトル設定
                        alertDialog.setMessage("サーバへHTMLタグを送信できませんでした。");  //内容(メッセージ)設定
                        // OK(肯定的な)ボタンの設定
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OKボタン押下時の処理
                                //finish();
                            }
                        });
                        alertDialog.show();
                    }

                }
            });
            pickerApi.execute();

        }else{
            Log.d("Error", "File Write Error.");
        }

    }

    @Override
    public void notifyUrl(String url) {

        et_url.setText(url);
    }


    public Boolean saveFile(String file, String str) {
        FileOutputStream fileOutputstream = null;

        try {
            fileOutputstream = openFileOutput(file, Context.MODE_PRIVATE);
            fileOutputstream.write(str.getBytes());

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

    }
}


class PickerResourceClient extends XWalkResourceClient {

    private XWalkView xWalkView;
    private PickerNotify pickerNotify;
    private Activity activity;

    private Boolean pickValid = false;
    private String jsCode = "document.documentElement.outerHTML";

    private String pageUrl;
    private String title;

    public PickerResourceClient(XWalkView view, Activity activity, PickerNotify pn) {
        super(view);

        this.xWalkView = view;
        this.activity = activity;
        this.pickerNotify = pn;
    }

    @Override
    public void onLoadStarted(XWalkView view, String url) {
        super.onLoadStarted(view, url);

        pickValid = false;

    }

    @Override
    public void onLoadFinished(XWalkView view, String url) {
        super.onLoadFinished(view, url);

        pickValid = true;
        this.pageUrl = url;
        this.title = xWalkView.getTitle();
        pickerNotify.notifyUrl(url);
    }

    public void getAllTags(){

        if(pickValid) {
            xWalkView.evaluateJavascript(jsCode, new PickerValueCallBack(this, pickerNotify, pageUrl, title));
        }
    }


    class PickerValueCallBack implements ValueCallback {

        PickerResourceClient pickerResourceClient;
        PickerNotify pickerNotify;
        String url;
        String title;

        public PickerValueCallBack(PickerResourceClient prc, PickerNotify pn, String url, String title){
            this.pickerResourceClient = prc;
            this.pickerNotify = pn;
            this.url = url;
            this.title = title;
        }

        @Override
        public void onReceiveValue(Object o) {
            pickerNotify.sendTags(o.toString(), url, title);
            Log.d("tag", o.toString());
        }
    }
}


class PickerUIClient extends XWalkUIClient {


    PickerUIClient(XWalkView view) {
        super(view);

    }



}
