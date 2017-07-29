package com.example.masato.guruguru2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkHitTestResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;
import org.xwalk.core.internal.XWalkClient;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.chromium.base.ContextUtils.getApplicationContext;


/**
 * Created by masato on 2017/04/22.
 */

public class WebViewActivity extends AppCompatActivity implements PageLogListener, View.OnClickListener {

    private Integer mode = 0;
    //private TextView textLog1;
    private Button btn_back;
    private PageLogNotify pageLogNotify;
    private XWalkView xWalkView;
    private CustomResourceClient customResourceClient;
    private Activity activity;

    //ログの表示
    private ListView listView_log;
    private List<String> logList;
    private LogListAdapter logListAdapter;

    //実行jsリスト(old)
    private List<JSData> jsList;

    //シナリオリスト(new)
    private List<Scenario> scenarioList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);

        activity = this;

        pageLogNotify = new PageLogNotify();
        pageLogNotify.setListener(this);

        btn_back = (Button)this.findViewById(R.id.wv_btn_back);
        btn_back.setOnClickListener(this);

        logList = new ArrayList<String>();
        logListAdapter = new LogListAdapter(this, 0, logList);
        listView_log = (ListView) this.findViewById(R.id.wv_listview_log);
        listView_log.setAdapter(logListAdapter);

        xWalkView = (XWalkView) findViewById(R.id.web_webview);
        xWalkView.setEnabled(false);


        if(mode==100){

            scenarioList = new ArrayList<Scenario>();
            ScenarioApi scenarioApi = new ScenarioApi(this, AppStatics.getInstance().selectScenarioIndexes, scenarioList);
            scenarioApi.setOnCallBack(new ScenarioApi.CallBackTask() {

                @Override
                public void callBack(Integer result) {
                    super.callBack(result);

                    if(checkScenarioValid()) {
                        //全シナリオが正常であれば、Webview実行開始
                        xWalkView.setUIClient(new CustomUIClient(xWalkView));
                        customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify, activity);
                        customResourceClient.setScenarioList(scenarioList);
                        xWalkView.setResourceClient(customResourceClient);
                        Log.d("scene_url", scenarioList.get(0).getSceneList().get(0).getUrl());
                        //xWalkView.load(scenarioList.get(0).getSceneList().get(0).getUrl(), null);
                        xWalkView.loadUrl(scenarioList.get(0).getSceneList().get(0).getUrl());
                    }
                }
            });
            scenarioApi.execute();

        }


    }

    //アクション以下が定義されていないシナリオがあれば実行中止＆アラート表示
    private Boolean checkScenarioValid(){
        String msg = "以下のシナリオは、詳細な命令が登録されていないため、実行を中止します。\n";
        Boolean bool = true;
        for(int i=0; i<scenarioList.size(); i++){
            if(!scenarioList.get(i).getValid()){
                msg += "・" + scenarioList.get(i).getScenarioIndex().getName() + "\n";
                bool = false;
            }
        }

        if(!bool){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("エラー");      //タイトル設定
            alertDialog.setMessage(msg);  //内容(メッセージ)設定
            // OK(肯定的な)ボタンの設定
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OKボタン押下時の処理
                    finish();
                }
            });
            alertDialog.show();
        }
        return bool;
    }

    @Override
    public void dispLog(String str) {
        //textLog1.setText(str);
        logList.add(str);
        logListAdapter.notifyDataSetChanged();
        listView_log.setSelection(listView_log.getCount());
    }

//    @Override
//    public void completeTest() {
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                xWalkView.setUIClient(new CustomUIClient(xWalkView));
//                customResourceClient = null;
//                customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify, activity);
//                xWalkView.setResourceClient(customResourceClient);
//                customResourceClient.setPerameter(jsList);
//
//                if(mode==3 || mode==4){
//                    xWalkView.load(jsList.get(0).getExeScriptList().get(0), null);
//                }else {
//                    xWalkView.load("http://smt.docomo.ne.jp/", null);
//                }
//            }
//        }, 10000);
//
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //AppStatics.getInstance().resetSelectScenarioIndexes();
        customResourceClient.stopAllTask();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        if(view==btn_back){
            customResourceClient.stopAllTask();
            finish();
        }
    }

//    //テストデータ生成
//    private void makeTestControlList(){
//
//        controlList = new ArrayList<ControlData>();
//        ControlData cd = new ControlData();
//        cd.setMode(0);
//        cd.setNum(0);
//        cd.setFirstUrl("http://smt.docomo.ne.jp/");
//        controlList.add(cd);
//
//
//        cd = new ControlData();
//        cd.setMode(1);
//        cd.setNum(1);
//        cd.setSleepTime(1000);
//            List<SearchTargetData> stdList = new ArrayList<SearchTargetData>();
//            SearchTargetData std = new SearchTargetData();
//            std.setNum(0);
//            std.setType(0);
//            std.setTagName("a");
//            std.setAttName("swid");
//            std.setAttValue("dm_toplink");
//            stdList.add(std);
//        cd.setTargetArray(stdList);
//        cd.setClickTagName("a");
//        cd.setClickAttName("");
//        cd.setClickAttValue("");
//        controlList.add(cd);
//
//        cd = new ControlData();
//        cd.setMode(1);
//        cd.setNum(2);
//        cd.setSleepTime(1000);
//            stdList = new ArrayList<SearchTargetData>();
//            std = new SearchTargetData();
//            std.setNum(0);
//            std.setType(0);
//            std.setTagName("dl");
//            std.setAttName("id");
//            std.setAttValue("docomoid_block_daccountlogin");
//            stdList.add(std);
//        cd.setTargetArray(stdList);
//        cd.setClickTagName("a");
//        cd.setClickAttName("");
//        cd.setClickAttValue("");
//        controlList.add(cd);
//
//        cd = new ControlData();
//        cd.setMode(2);
//        cd.setNum(3);
//        cd.setSleepTime(1000);
//            List<InputControlData> icdList = new ArrayList<InputControlData>();
//            InputControlData icd = new InputControlData();
//            icd.setAttName("name");
//            icd.setAttValue("authid");
//            icd.setInputValue(inputId);
//            icdList.add(icd);
//            icd = new InputControlData();
//            icd.setAttName("name");
//            icd.setAttValue("authpass");
//            icd.setInputValue(inputPass);
//            icdList.add(icd);
//        cd.setInputArray(icdList);
//        cd.setSendBtnAttName("name");
//        cd.setSendBtnAttValue("subForm");
//        controlList.add(cd);
//
//    }
}


class CustomResourceClient extends XWalkResourceClient {


    private XWalkCookieManager xWalkCookieManager;
    private XWalkSettings xWalkSettings;


    private Integer mode = 0;
    private Integer pageCount = 0;

    private PageLogNotify pageLogNotify = null;

    //new
    private List<Scenario> scenarioList;
    private Integer scenarioCount = 0;
    private Scenario scenario;
    private ScenarioIndex scenarioIndex;
    private Scene scene;
    private Action action;
    private SceneCheck check;

    private Handler checkHandler = new Handler();
    private Runnable checkRunnable;
    private Integer checkNum = 0;
    private Integer checkCount = 0;

    private Handler actionHandler = new Handler();
    private Runnable actionRunnable;
    private Integer actionNum = 0;
    private Integer actionCount = 0;

    private Integer errorCount = 0;
    private Boolean errorFlag = false;

    private CustomResourceClient crc = this;

    private XWalkView xWalkView;

    private Integer STATE = 0;
    private String nowURL;


    CustomResourceClient(XWalkView view, Integer mode, PageLogNotify pln, Activity act) {
        super(view);

        this.xWalkView = view;
        this.mode = mode;
        this.pageLogNotify = pln;
        pageCount = 0;
        STATE = 0;
        //this.activity = act;

        xWalkCookieManager = new XWalkCookieManager();
        xWalkCookieManager.setAcceptCookie(true);
        xWalkCookieManager.setAcceptFileSchemeCookies(true);
        xWalkCookieManager.removeAllCookie();

        xWalkSettings = view.getSettings();
        xWalkSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03S) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
        xWalkSettings.setUseWideViewPort(true);
        xWalkSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //xWalkSettings.setImagesEnabled(true);

        checkRunnable = new Runnable() {
            public void run() {
                STATE = 1;
                check = scene.getCheckList().get(checkCount);

                if (check.getCheck_type() == 0) {

                    String preStr = check.getPreJS();
                    xWalkView.evaluateJavascript(preStr, new CheckValueCallback(crc, xWalkView, check, pageLogNotify));

                }else if(check.getCheck_type() == 1){

                    if (nowURL.startsWith(check.getUrl())) {
                        pageLogNotify.sendLogsToActivity("URL前方一致チェック: OK");
                        pageLogNotify.sendLogsToActivity("URL 現在位置: " + nowURL);
                        pageLogNotify.sendLogsToActivity("URL 比較対象: " + check.getUrl());
                    } else {
                        pageLogNotify.sendLogsToActivity("URL前方一致チェック: NG");
                        pageLogNotify.sendLogsToActivity("URL 現在位置: " + nowURL);
                        pageLogNotify.sendLogsToActivity("URL 比較対象: " + check.getUrl());
                        errorTrigger();
                        //checkHandler.removeCallbacks(checkRunnable);
                    }
                }

                checkCount++;

                if(checkCount >= checkNum){
                    //Actionへ
                    if(actionNum>0){
                        actionHandler.postDelayed(actionRunnable, AppStatics.getInstance().outputSleepTime(scene.getActionList().get(0).getSleep()));

                    }else{
                        //正常のまま最終ページ到達
                        Log.d("2","2");
                        scenarioComplete();
                    }

                }else{
                    //続行
                    checkHandler.postDelayed(this, AppStatics.getInstance().outputSleepTime(3));
                }
            }
        };


        actionRunnable = new Runnable() {
            public void run() {
                STATE = 2;
                action = scene.getActionList().get(actionCount);

                final List<String> execJS = action.getExecJS();
                final List<String> preJS = action.getPreJS();

                for (int k = 0; k < execJS.size(); k++) {

                    final String execStr = execJS.get(k);
                    final String preStr = preJS.get(k);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            xWalkView.evaluateJavascript(preStr, new CustomValueCallback(crc, xWalkView, preStr, execStr, pageLogNotify));
                        }
                    }, AppStatics.getInstance().outputSleepTime(action.getSleep()));
                }

                actionCount++;

                if(actionCount >= actionNum){
                    //最終ページへ正常のまま到達
                    //Log.d("3","3");
                    //scenarioComplete();
                }else{
                    //続行
                    actionHandler.postDelayed(this, AppStatics.getInstance().outputSleepTime(scene.getActionList().get(actionCount).getSleep()));
                }

            }
        };

//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                action = scene.getActionList().get(i);
//
//                handler.postDelayed(this, REPEAT_INTERVAL);
//            }
//        };

    }


    public void stopAllTask() {

        checkHandler.removeCallbacks(checkRunnable);
        actionHandler.removeCallbacks(actionRunnable);
    }


    public void setScenarioList(List<Scenario> list) {

        this.scenarioList = list;
        pageCount = 0;
        scenarioCount = 0;
    }


    @Override
    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {

        Log.d("url", url);
        //XWalkHitTestResult a = view.getHitTestResult();
        //Log.d("hit", a.getExtra().toString());
        return super.shouldOverrideUrlLoading(view, url);
    }




    public void errorTrigger() {


        stopAllTask();
        errorCount++;


        pageLogNotify.sendLogsToActivity("シナリオ「" + scenarioIndex.getName() + "」:NG" + errorCount + "回目");

        if (errorCount < 3) {
            //やり直し
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    xWalkCookieManager.removeAllCookie();
                    pageCount = 0;
                    pageLogNotify.sendLogsToActivity("リトライ中…");
                    xWalkView.loadUrl(scenarioList.get(scenarioCount).getSceneList().get(0).getUrl());

                }
            }, 5000);

        } else if (errorCount == 3) {
            //エラー報告
            pageLogNotify.sendLogsToActivity("サーバへエラーログ出力");

            errorCount = 0;

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    xWalkCookieManager.removeAllCookie();
                    if (scenarioCount == scenarioList.size() - 1) {
                        scenarioCount = 0;
                    } else {
                        scenarioCount++;
                    }
                    pageCount = 0;
                    xWalkView.loadUrl(scenarioList.get(scenarioCount).getSceneList().get(0).getUrl());

                }
            }, 5000);
        }

    }


    public void scenarioComplete(){

        //シナリオ実行完了(正常)
        pageLogNotify.sendLogsToActivity("シナリオ「" + scenarioIndex.getName() + "」:OK");

        errorCount = 0;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                xWalkCookieManager.removeAllCookie();
                if (scenarioCount == scenarioList.size() - 1) {
                    scenarioCount = 0;
                } else {
                    scenarioCount++;
                }
                pageCount = 0;
                xWalkView.loadUrl(scenarioList.get(scenarioCount).getSceneList().get(0).getUrl());
            }
        }, 10000);
    }



    @Override
    public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
        super.onReceivedLoadError(view, errorCode, description, failingUrl);

        //Log.d("HTTP Status: ", Integer.toString(errorCode));
    }

    @Override
    public void onReceivedResponseHeaders(XWalkView view, XWalkWebResourceRequest request, XWalkWebResourceResponse response) {
        super.onReceivedResponseHeaders(view, request, response);

        if(response.getMimeType().equals("text/html")){
            Log.d("PageCount", Integer.toString(pageCount));
            Log.d("HTTP Status", Integer.toString(response.getStatusCode()));
            Log.d("HTTP MimeType", response.getMimeType());
        }

    }

    @Override
    public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
        super.onReceivedSslError(view, callback, error);


    }

    @Override
    public void onLoadFinished(final XWalkView view, String url) {
        super.onLoadFinished(view, url);


        if (mode == 100) {

            pageCount++;

            if (pageCount == 1) {
                scenario = scenarioList.get(scenarioCount);
                scenarioIndex = scenario.getScenarioIndex();
                pageLogNotify.sendLogsToActivity("シナリオ「" + scenarioIndex.getName() + "」実行開始");
            }

            nowURL = url;



            if (pageCount <= scenario.getSceneList().size()) {

                scene = scenario.getSceneList().get(pageCount - 1);

                checkCount = 0;
                checkNum = scene.getCheckList().size();
                actionCount = 0;
                actionNum = scene.getActionList().size();


                if(checkNum == 0 && actionNum == 0){
                    //シナリオ正常終了
                    Log.d("1","1");
                    scenarioComplete();

                }else if(checkNum > 0){
                    //Checkへ
                    checkHandler.postDelayed(checkRunnable, AppStatics.getInstance().outputSleepTime(3));

                }else if(checkNum == 0){
                    //Actionへ
                    actionHandler.postDelayed(actionRunnable, AppStatics.getInstance().outputSleepTime(scene.getActionList().get(0).getSleep()));

                }




//                //Check
//                for (int i = 0; i < scene.getCheckList().size(); i++) {
//
//                    check = scene.getCheckList().get(i);
//
//                    if (check.getCheck_type() == 0) {
//
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                String preStr = check.getPreJS();
//                                //                                              //String preStr = preJS.get(k);
//                                //view.evaluateJavascript("javascript:document.querySelector('.donation-inner').innerText;", new CustomValueCallback(view, "javascript:document.querySelector('.donation-inner').innerText;", "javascript:document.querySelector('.donation-inner').innerText;", pageLogNotify));
//                                view.evaluateJavascript(preStr, new CheckValueCallback(crc, view, check, pageLogNotify));
//                            }
//                        }, AppStatics.getInstance().outputSleepTime(3));
//
//                    } else if (check.getCheck_type() == 1) {
//
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                if (url2.startsWith(check.getUrl())) {
//                                    pageLogNotify.sendLogsToActivity("URL前方一致チェック: OK");
//                                    pageLogNotify.sendLogsToActivity("URL 現在位置: " + url2);
//                                    pageLogNotify.sendLogsToActivity("URL 比較対象: " + check.getUrl());
//                                } else {
//                                    pageLogNotify.sendLogsToActivity("URL前方一致チェック: NG");
//                                    pageLogNotify.sendLogsToActivity("URL 現在位置: " + url2);
//                                    pageLogNotify.sendLogsToActivity("URL 比較対象: " + check.getUrl());
//                                    errorTrigger();
//                                }
//
//                            }
//                        }, AppStatics.getInstance().outputSleepTime(1));
//                    }
//
//
//                }


                //Action
//                for (int i = 0; i < scene.getActionList().size(); i++) {
//
//                    action = scene.getActionList().get(i);
//
//
//                    for (int j = 0; j < action.getExecJS().size(); j++) {
//
//                        final List<String> execJS = action.getExecJS();
//                        final List<String> preJS = action.getPreJS();
//
//                        for (int k = 0; k < execJS.size(); k++) {
//
//                            final String execStr = execJS.get(k);
//                            final String preStr = preJS.get(k);
//
//                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    //view.evaluateJavascript("javascript:document.querySelector('.donation-inner').innerText;", new CustomValueCallback(view, "javascript:document.querySelector('.donation-inner').innerText;", "javascript:document.querySelector('.donation-inner').innerText;", pageLogNotify));
//                                    view.evaluateJavascript(preStr, new CustomValueCallback(crc, view, preStr, execStr, pageLogNotify));
//                                }
//                            }, AppStatics.getInstance().outputSleepTime(action.getSleep()));
//                        }
//
//
//                    }
//
//                }


                if (pageCount == scenario.getSceneList().size()) {


                    //最終ページ
//                    pageLogNotify.sendLogsToActivity("シナリオ「" + scenarioIndex.getName() + "」:OK");
//                    errorCount = 0;
//                    errorFlag = false;
//
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            xWalkCookieManager.removeAllCookie();
//                            if (scenarioCount == scenarioList.size() - 1) {
//                                scenarioCount = 0;
//                            } else {
//                                scenarioCount++;
//                            }
//                            pageCount = 0;
//                            view.loadUrl(scenarioList.get(scenarioCount).getSceneList().get(0).getUrl());
//
//                        }
//                    }, 10000);


                }


            } else {
                //シナリオ実行完了
                pageLogNotify.sendLogsToActivity("シナリオ「" + scenarioIndex.getName() + "」:OK");

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xWalkCookieManager.removeAllCookie();
                        if (scenarioCount == scenarioList.size() - 1) {
                            scenarioCount = 0;
                        } else {
                            scenarioCount++;
                        }
                        pageCount = 0;
                        view.loadUrl(scenarioList.get(scenarioCount).getSceneList().get(0).getUrl());
                    }
                }, 10000);

            }
        }


    }


    class CustomValueCallback implements ValueCallback {

        CustomResourceClient crc;
        XWalkView view;
        PageLogNotify pageLogNotify;
        String exeStr;
        String checkStr;

        public CustomValueCallback(CustomResourceClient crc, XWalkView view, String checkStr, String exeStr, PageLogNotify pln) {
            this.crc = crc;
            this.view = view;
            this.checkStr = checkStr;
            this.exeStr = exeStr;
            this.pageLogNotify = pln;
        }

        @Override
        public void onReceiveValue(Object o) {
            pageLogNotify.sendLogsToActivity("アクションコマンド: " + exeStr);
            if (!o.toString().equals("null")) {
                view.loadUrl(exeStr);
                Log.d("returnValue", o.toString());
                pageLogNotify.sendLogsToActivity("アクション可否: OK");
            } else {
                pageLogNotify.sendLogsToActivity("アクション可否: NG");
                crc.errorTrigger();
            }


        }
    }


    class CheckValueCallback implements ValueCallback {

        CustomResourceClient crc;
        XWalkView view;
        PageLogNotify pageLogNotify;
        SceneCheck check;

        public CheckValueCallback(CustomResourceClient crc, XWalkView view, SceneCheck check, PageLogNotify pln) {
            this.crc = crc;
            this.view = view;
            this.pageLogNotify = pln;
            this.check = check;
        }

        @Override
        public void onReceiveValue(Object o) {

            Log.d("returnValue", o.toString());
            if (check.getStr_type() == 0) {
                //タグ有無のみ
                pageLogNotify.sendLogsToActivity("指定タグ存在チェック: " + check.getPreJS());
                if (!o.toString().equals("null")) {
                    pageLogNotify.sendLogsToActivity("結果: OK");
                } else {
                    pageLogNotify.sendLogsToActivity("結果: NG");
                    crc.errorTrigger();
                }
            } else if (check.getStr_type() == 1) {
                //固定文言
                pageLogNotify.sendLogsToActivity("固定文言チェック: " + check.getPreJS());
                String str = o.toString().replace("\"", "");
                if (str.equals(check.getOrigin_str())) {
                    pageLogNotify.sendLogsToActivity("結果: OK");
                    pageLogNotify.sendLogsToActivity("文言: " + o.toString());
                } else {
                    pageLogNotify.sendLogsToActivity("結果: NG");
                    crc.errorTrigger();
                }

            } else if (check.getStr_type() == 2) {
                //数値 1000
                pageLogNotify.sendLogsToActivity("数値(桁区切りなし)チェック: " + check.getPreJS());
                String str = o.toString().replace("\"", "");
                try {
                    Integer.parseInt(str);
                    pageLogNotify.sendLogsToActivity("結果: OK");
                    pageLogNotify.sendLogsToActivity("数値: " + str);
                } catch (NumberFormatException e) {
                    pageLogNotify.sendLogsToActivity("結果: NG");
                    crc.errorTrigger();
                }

            } else if (check.getStr_type() == 3) {
                //数値 1,000
                pageLogNotify.sendLogsToActivity("数値(桁区切りあり)チェック: " + check.getPreJS());
                String str = o.toString().replace("\"", "");
                try {
                    str = str.replace(",", "");
                    Integer.parseInt(str);
                    pageLogNotify.sendLogsToActivity("結果: OK");
                    pageLogNotify.sendLogsToActivity("数値: " + o.toString());
                } catch (NumberFormatException e) {
                    pageLogNotify.sendLogsToActivity("結果: NG");
                    crc.errorTrigger();
                }
            }

        }
    }

}

    class CustomUIClient extends XWalkUIClient {


        CustomUIClient(XWalkView view) {
            super(view);

        }


    }


