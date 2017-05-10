package com.example.masato.guruguru2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkClient;
import org.xwalk.core.internal.XWalkCookieManager;
import org.xwalk.core.internal.XWalkSettings;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager;

import java.util.ArrayList;
import java.util.List;

import static org.chromium.base.ApplicationStatus.getApplicationContext;

/**
 * Created by masato on 2017/04/22.
 */

public class WebViewActivity extends AppCompatActivity implements PageLogListener, View.OnClickListener {

    private Integer mode = 0;
    private TextView textLog1;
    private Button btn_back;
    private PageLogNotify pageLogNotify;
    private XWalkView xWalkView;
    private CustomResourceClient customResourceClient;

    //ログの表示
    private ListView listView_log;
    private List<String> logList;
    private LogListAdapter logListAdapter;

    //実行jsリスト(old)
    private List<JSData> jsList;

    //シナリオリスト(new)
    private List<Scenario> scenarioList;


    private static final String inputId = "09090841258";
    private static final String inputPass = "MATSUURA8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);


        pageLogNotify = new PageLogNotify();
        pageLogNotify.setListener(this);

        textLog1 = (TextView)this.findViewById(R.id.wv_text_log1);
        btn_back = (Button)this.findViewById(R.id.wv_btn_back);
        btn_back.setOnClickListener(this);

        logList = new ArrayList<String>();
        logListAdapter = new LogListAdapter(this, 0, logList);
        listView_log = (ListView) this.findViewById(R.id.wv_listview_log);
        listView_log.setAdapter(logListAdapter);

        xWalkView = (XWalkView) findViewById(R.id.web_webview);
        xWalkView.setEnabled(false);

        //xWalkView.load("", null);

        if(mode==3 || mode==4) {
            ControlApi ctrApi = new ControlApi(this);
            //コールバック関数
            ctrApi.setOnCallBack(new ControlApi.CallBackTask() {

                @Override
                public void CallBack(List<JSData> result) {
                    super.CallBack(result);

                    jsList = result;
                    xWalkView.setUIClient(new CustomUIClient(xWalkView));
                    customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify);
                    xWalkView.setResourceClient(customResourceClient);
                    customResourceClient.setPerameter(jsList);

                    if (mode == 3 || mode == 4) {
                        xWalkView.load(result.get(0).getExeScriptList().get(0), null);
                    } else {
                        xWalkView.load("http://smt.docomo.ne.jp/", null);
                    }

                }

            });
            ctrApi.execute(mode);
        }

        if(mode==100){

            scenarioList = new ArrayList<Scenario>();
            ScenarioApi scenarioApi = new ScenarioApi(this, AppStatics.getInstance().selectScenarioIndexes, scenarioList);
            scenarioApi.setOnCallBack(new ScenarioApi.CallBackTask() {

                @Override
                public void callBack(Integer result) {
                    super.callBack(result);

                    xWalkView.setUIClient(new CustomUIClient(xWalkView));
                    customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify);
                    xWalkView.setResourceClient(customResourceClient);
                    Log.d("scene_url", scenarioList.get(0).getSceneList().get(0).getUrl());
                    xWalkView.load(scenarioList.get(0).getSceneList().get(0).getUrl(), null);
                }
            });
            scenarioApi.execute();
        }

//        XWalkCookieManager mCookieManager = new XWalkCookieManager();
//        mCookieManager.setAcceptCookie(true);
//        mCookieManager.setAcceptFileSchemeCookies(true);
//        mCookieManager.removeAllCookie();


//        XWalkSettings mSettings = new XWalkSettings(getApplicationContext(), 0, false);
//        mSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03S) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
//        mSettings.setUseWideViewPort(true);
//        mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        mSettings.setImagesEnabled(true);

        //テスト操作データ生成
        //makeTestControlList();

        // XWalkViewを取得


        // XWalkViewから通知を受け取るためのXWalkResourceClient継承クラスをset
        //xWalkView.setResourceClient(new CustomClient(xWalkView));




//        WebView webView = (WebView) findViewById(R.id.web_webview);
//        MyWVClient mvc = new MyWVClient();
//        mvc.setMode(mode);
//        mvc.setParentContext(this);
//        mvc.setPageLogNotify(pageLogNotify);
//        mvc.preProcess();

//        webView.setWebViewClient(mvc);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDefaultTextEncodingName("UTF-8");
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03S) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.clearCache(true);

        //webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            CookieManager.getInstance().removeAllCookies(null);
//            CookieManager.getInstance().flush();
//        } else {
//            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(getApplicationContext());
//            cookieSyncMngr.startSync();
//            CookieManager cookieManager=CookieManager.getInstance();
//            cookieManager.removeAllCookie();
//            cookieManager.removeSessionCookie();
//            cookieSyncMngr.stopSync();
//            cookieSyncMngr.sync();
//        }
//



        //webView.loadUrl("http://smt.docomo.ne.jp/");


    }


    @Override
    public void dispLog(String str) {
        //textLog1.setText(str);
        logList.add(str);
        logListAdapter.notifyDataSetChanged();
        listView_log.setSelection(listView_log.getCount());
    }

    @Override
    public void completeTest() {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                xWalkView.setUIClient(new CustomUIClient(xWalkView));
                customResourceClient = null;
                customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify);
                xWalkView.setResourceClient(customResourceClient);
                customResourceClient.setPerameter(jsList);

                if(mode==3 || mode==4){
                    xWalkView.load(jsList.get(0).getExeScriptList().get(0), null);
                }else {
                    xWalkView.load("http://smt.docomo.ne.jp/", null);
                }
            }
        }, 10000);
//        xWalkView.setUIClient(new CustomUIClient(xWalkView));
//        customResourceClient = null;
//        customResourceClient = new CustomResourceClient(xWalkView, mode, pageLogNotify);
//        xWalkView.setResourceClient(customResourceClient);
//        customResourceClient.setPerameter(jsList);
//
//        if(mode==3 || mode==4){
//            xWalkView.load(jsList.get(0).getExeScriptList().get(0), null);
//        }else {
//            xWalkView.load("http://smt.docomo.ne.jp/", null);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppStatics.getInstance().resetSelectScenarioIndexes();
    }

    @Override
    public void onClick(View view) {

        if(view==btn_back){
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

    private static final String JS_CLICKANCHOR =
            "javascript:document.querySelector('%s').click();";

    private static final String JS_CODE1 =
            "javascript:document.querySelector('div#boxPoint').querySelector('a').click();";

    private static final String JS_CODE2 =
            "javascript:document.querySelector('ul[class=cmn-footer__loginout]').querySelector('a').click();";

    private static final String JS_CODE3_1 =
            "javascript:document.querySelector('input[name=authid]').value='09090841258';void 0;";

    private static final String JS_CODE3_2 =
            "javascript:document.querySelector('input[name=authpass]').value='MATSUURA8';void 0;";

    private static final String JS_CODE3_3 =
            "javascript:document.querySelector('input[name=subForm]').click();";


    private static final String JS_1 =
            "javascript:document.querySelector('a[swid=dm_toplink]').click();";

    private static final String JS_2 =
            "javascript:document.querySelector('dl[id=docomoid_block_daccountlogin]').querySelector('a').click();";

    private static final String JS_3_1 =
            "javascript:document.querySelector('input[name=authid]').value='09090841258';void 0;";

    private static final String JS_3_2 =
            "javascript:document.querySelector('input[name=authpass]').value='MATSUURA8';void 0;";

    private static final String JS_3_3 =
            "javascript:document.querySelector('input[name=subForm]').click();";


    private XWalkCookieManager xWalkCookieManager;
    private XWalkSettings xWalkSettings;
    /** スレッドUI操作用ハンドラ */
    private Handler mHandler = new Handler();
    /** テキストオブジェクト */
    //private Runnable step1;
//    private Runnable exeRunnable;
//    private Runnable resetRunnable;

    private Integer mode = 0;
    private Integer pageCount = 0;
    private Integer sleepTime = 2000;
    private PageLogNotify pageLogNotify = null;

    private List<JSData> jsList = new ArrayList<JSData>();
    private List<String> execJs = new ArrayList<String>();  //loadに入力する実行スクリプト
    private List<String> checkJs = new ArrayList<String>(); //要素があるかどうかチェックするための絞り込みスクリプト

    CustomResourceClient(XWalkView view, Integer mode, PageLogNotify pln){
        super(view);

        //this.xWalkView = view;
        this.mode = mode;
        this.pageLogNotify = pln;
        pageCount = 0;


        xWalkCookieManager = new XWalkCookieManager();
        xWalkCookieManager.setAcceptCookie(true);
        xWalkCookieManager.setAcceptFileSchemeCookies(true);
        xWalkCookieManager.removeAllCookie();

        xWalkSettings = new XWalkSettings(getApplicationContext(), 0, false);
        xWalkSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03S) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
        xWalkSettings.setUseWideViewPort(true);
        xWalkSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        xWalkSettings.setImagesEnabled(true);

//        resetRunnable = new Runnable() {
//            public void run() {
//                mHandler.removeCallbacks(resetRunnable);
//                pageLogNotify.backToActivity();
//            }
//        };
    }

    public void setPerameter(List<JSData> js){

        this.jsList = js;
        pageCount = 0;
    }

//    @Override
//    public void onLoadStarted(XWalkView view, String url) {
//        super.onLoadStarted(view, url);
//
//        pageCount++;
//        if(pageCount >= controlList.size()){
//            return;
//        }
//        makeVariableScript();
//    }

    @Override
    public void onLoadFinished(final XWalkView view, String url) {
        super.onLoadFinished(view, url);



            if(mode==3 || mode==4) {

                pageCount++;

                if(url.equals("http://smt.docomo.ne.jp/")){
                    pageCount = 1;
                }

                //動的に改良予定
                if (mode == 4 && pageCount == 2) {
                    //mHandler.postDelayed(exeRunnable, 10000);
                    sleepTime = 10000;
                } else {
                    //mHandler.postDelayed(exeRunnable, 2000);
                    sleepTime = 2000;
                }

                //実行
                if(pageCount >= jsList.size()){
                    pageCount=0;
                    pageLogNotify.backToActivity();

                    return;
                }


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {



                        execJs = jsList.get(pageCount).getExeScriptList();
                        checkJs = jsList.get(pageCount).getPreScriptList();

                        Log.d("pageCount", pageCount.toString());
                        for (Integer i = 0; i < execJs.size(); i++) {

                            String exeStr = execJs.get(i);
                            String checkStr = checkJs.get(i);
                            view.evaluateJavascript(checkStr, new CustomValueCallback(view, checkStr, exeStr, pageLogNotify));

                        }
                    }
                }, sleepTime);

//                exeRunnable = new Runnable() {
//                    public void run() {
//
//                        pageCount++;
//
//                        if(pageCount >= jsList.size()){
//                            pageCount=0;
//                            mHandler.postDelayed(resetRunnable, 15000);
//                            //mHandler.removeCallbacks(exeRunnable);
//                            return;
//                        }
//
//                        execJs = jsList.get(pageCount).getExeScriptList();
//                        checkJs = jsList.get(pageCount).getCheckScriptList();
//
//                        Log.d("pageCount", pageCount.toString());
//                        for (Integer i = 0; i < execJs.size(); i++) {
//
//                            String exeStr = execJs.get(i);
//                            String checkStr = checkJs.get(i);
//                            view.evaluateJavascript(checkStr, new CustomValueCallback(view, checkStr, exeStr, pageLogNotify));
//
//                        }
//                        mHandler.removeCallbacks(exeRunnable);
//                        //mHandler.postDelayed(exeRunnable, 1000);
//                    }
//                };



            }
//                for(Integer i=0; i<execJs.size(); i++) {
//
//                    String exeStr = execJs.get(i);
//                    String checkStr = checkJs.get(i);
//
//                    view.evaluateJavascript(checkStr, new CustomValueCallback(view, checkStr, exeStr, pageLogNotify));
//
//                }
//
//
//            }
//
//            if(mode==2) {
//                if (pageCount == 1) {
//                    //Thread.sleep(2000);
//                    view.load(JS_CODE1, "");
//                } else if (pageCount == 2) {
//                    Thread.sleep(4000);
//                    view.load(JS_CODE2, "");
//                } else if (pageCount == 3) {
//                    view.load(JS_CODE3_1, "");
//                    view.load(JS_CODE3_2, "");
//                    view.load(JS_CODE3_3, "");
//
//                }
//
//            }else if(mode==1){
//
//                if (pageCount == 1) {
//                    //view.load(JS_1, "");
//                    view.evaluateJavascript("document.querySelector('a[swid=dm_toplink]');", new ValueCallback() {
//                        @Override
//                        public void onReceiveValue(Object o) {
//                            pageLogNotify.sendLogsToActivity(o.toString());
//                            if(!o.toString().equals("null")) {
//                                view.load(JS_1, "");
//                            }
//                        }
//
//                    });
//
//
//                } else if (pageCount == 2) {
//                    view.load(JS_2, "");
//                } else if (pageCount == 3) {
//                    view.load(JS_3_1, "");
//                    view.load(JS_3_2, "");
//                    view.load(JS_3_3, "");
//                }
//            }
//
    }


//    private void loopExec(){
//
//        pageCount = 0;
//
////        XWalkCookieManager mCookieManager = new XWalkCookieManager();
////        mCookieManager.setAcceptCookie(true);
////        mCookieManager.setAcceptFileSchemeCookies(true);
////        mCookieManager.removeAllCookie();
//
//        resetRunnable = new Runnable() {
//            public void run() {
//                xWalkView.load(jsList.get(0).getExeScriptList().get(0), null);
//            }
//        };
//
//        mHandler.postDelayed(resetRunnable, 30000);
//
//    }

//    private void makeVariableScript(){
//
//        //スクリプトリセット
//        execJs = new ArrayList<String>();
//        checkJs = new ArrayList<String>();
//
//        ControlData cd = controlList.get(pageCount);
//        if(cd.getNum()!=pageCount){
//            pageLogNotify.sendLogsToActivity("操作情報エラー");
//            return;
//        }
//
//
//        //命令JavaScriptの動的生成
//        if(cd.getMode()==1){
//            //リンクのクリック
//
//            List<SearchTargetData> stdList = cd.getTargetArray();
//            String eachExecStr = extraJsStr + "document";
//            String eachCheckStr = "document";
//            String lastTagName = "";
//            for(SearchTargetData row: stdList){
//                //クリック要素の絞り込み
//                String str = "";
//                if(row.getType()==0){
//                    //配下は一意
//                    if(row.getAttName()==null || row.getAttName().isEmpty()){
//                        //属性情報なし
//                        str += ".querySelector('" + row.getTagName() + "')";
//                    }else{
//                        //属性情報あり
//                        str += ".querySelector('" + row.getTagName() + "[" + row.getAttName() + "=" + row.getAttValue() + "]')";
//                    }
//
//                }else if(row.getType()==1){
//                    //配下は複数
//                    if(row.getAttName()==null || row.getAttName().isEmpty()){
//                        //属性情報なし
//                        str += ".querySelectorAll('" + row.getTagName() + "')[" + row.getChildNum().toString() + "]";
//                    }else{
//                        //属性情報あり
//                        str += ".querySelectorAll('" + row.getTagName() + "[" + row.getAttName() + "=" + row.getAttValue() + "]')[" + row.getChildNum().toString() + "]";
//                    }
//                }
//                lastTagName = row.getTagName();
//                eachExecStr += str;
//                eachCheckStr += str;
//            }
//
//            //指定されたクリック対象タグと、最後に絞り込んだタグが一致していれば無視する
//            if(cd.getClickTagName().equals(lastTagName)){
//                //無視してクリック
//                eachExecStr += ".click();";
//                eachCheckStr += ";";
//            }else{
//                //指定タグでさらに絞り込んでクリック
//                eachExecStr += ".querySelector('" + cd.getClickTagName() + "').click();";
//                eachCheckStr += ".querySelector('" + cd.getClickTagName() + "');";
//            }
//
//            //実行スクリプトとして登録
//            execJs.add(eachExecStr);
//            checkJs.add(eachCheckStr);
//
//        }else if(cd.getMode()==2){
//            //フォームの自動入力
//
//            List<InputControlData> icdList = cd.getInputArray();
//            String eachExecStr;
//            String eachCheckStr;
//            for(InputControlData row: icdList){
//                eachExecStr = extraJsStr + "document";
//                eachCheckStr = "document";
//                String str = "";
//
//                //フォーム絞り込み
//                str += ".querySelector('input[" + row.getAttName() + "=" + row.getAttValue() + "]')";
//                eachCheckStr += str + ";";
//                //入力値指定
//                str += ".value='" + row.getInputValue() + "'; void 0;";
//
//                //整形
//                eachExecStr += str;
//
//                //実行スクリプトとして登録
//                execJs.add(eachExecStr);
//                checkJs.add(eachCheckStr);
//            }
//
//            //送信ボタン押下用スクリプト生成
//            eachExecStr = extraJsStr + "document.querySelector('input[" +  cd.getSendBtnAttName() + "=" + cd.getSendBtnAttValue() + "]').click();";
//            eachCheckStr = "document.querySelector('input[" +  cd.getSendBtnAttName() + "=" + cd.getSendBtnAttValue() + "]');";
//
//            //実行スクリプトとして登録
//            execJs.add(eachExecStr);
//            checkJs.add(eachCheckStr);
//        }
//
//    }

}


class CustomValueCallback implements ValueCallback {

    XWalkView view;
    PageLogNotify pageLogNotify;
    String exeStr;
    String checkStr;

    public CustomValueCallback(XWalkView view, String checkStr, String exeStr, PageLogNotify pln){
        this.view = view;
        this.checkStr = checkStr;
        this.exeStr = exeStr;
        this.pageLogNotify = pln;
    }

    @Override
    public void onReceiveValue(Object o) {
        pageLogNotify.sendLogsToActivity("command: " + exeStr);
        if (!o.toString().equals("null")) {
            view.load(exeStr, "");
            pageLogNotify.sendLogsToActivity("結果: OK");
        }else{
            pageLogNotify.sendLogsToActivity("結果: NG");
        }
    }
}



class CustomUIClient extends XWalkUIClient {



    CustomUIClient(XWalkView view) {
        super(view);

    }





}



//class MyWVClient extends WebViewClient {
//
//
//    private static final String JS_CLICKANCHOR =
//            "javascript:document.querySelector('%s').click();";
//
//    private static final String JS_CODE1 =
//            "javascript:document.querySelector('div#boxPoint').querySelector('a').click();";
//
//    private static final String JS_CODE2 =
//            "javascript:document.querySelector('ul[class=cmn-footer__loginout]').querySelector('a').click();";
//
//    private static final String JS_CODE3_1 =
//            "javascript:document.querySelector('input[name=authid]').value='09090841258';void 0;";
//
//    private static final String JS_CODE3_2 =
//            "javascript:document.querySelector('input[name=authpass]').value='MATSUURA8';void 0;";
//
//    private static final String JS_CODE3_3 =
//            "javascript:document.querySelector('input[name=subForm]').click();";
//
//
//    private static final String JS_1 =
//            "javascript:document.querySelector('a[swid=dm_toplink]').click();";
//
//    private static final String JS_2 =
//            "javascript:document.querySelector('dl[id=docomoid_block_daccountlogin]').querySelector('a').click();";
//
//    private static final String JS_3_1 =
//            "javascript:document.querySelector('input[name=authid]').value='09090841258';void 0;";
//
//    private static final String JS_3_2 =
//            "javascript:document.querySelector('input[name=authpass]').value='MATSUURA8';void 0;";
//
//    private static final String JS_3_3 =
//            "javascript:document.querySelector('input[name=subForm]').click();";
//
//    private Integer mode = 0;
//    private Integer pageCount = 0;
//    private Context parentCnt = null;
//    private PageLogNotify pageLogNotify = null;
//
//    public void setMode(Integer mode){
//        this.mode = mode;
//    }
//
//    public void setParentContext(Context cnt){
//        this.parentCnt = cnt;
//    }
//
//    public void setPageLogNotify(PageLogNotify pln){
//        this.pageLogNotify = pln;
//    }
//
//
//    public void preProcess() {
//
//
//
//    }
//
//
//
//
//    String loginCookie = "";
//    @Override
//    public void onLoadResource(WebView wv,
//                               String url) {
//        CookieManager cMgr = CookieManager.getInstance();
//        loginCookie = cMgr.getCookie(url);
//
//    }
//
//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            CookieManager cm = CookieManager.getInstance();
//            cm.setAcceptCookie(true);
//            cm.setCookie(url, loginCookie);
//        }
//    }
//
//    @Override
//    public void onPageFinished(WebView view, String url) {
//        super.onPageFinished(view, url);
//        Log.d("mode", mode.toString());
//
//        CookieManager cMgr = CookieManager.getInstance();
//        cMgr.setCookie(url, loginCookie);
//
//        if(loginCookie==null || loginCookie.isEmpty()){
//
//        }else{
//            pageLogNotify.sendLogsToActivity(loginCookie);
//            Log.d("cookie", loginCookie);
//        }
//
//        pageCount++;
//        try {
//
//            if(mode==2) {
//                if (pageCount == 1) {
//                    //Thread.sleep(2000);
//                    view.loadUrl(JS_CODE1);
//                } else if (pageCount == 2) {
//                    Thread.sleep(2000);
//                    view.loadUrl(JS_CODE2);
//                } else if (pageCount == 3) {
//                    view.loadUrl(JS_CODE3_1);
//                    view.loadUrl(JS_CODE3_2);
//                    view.loadUrl(JS_CODE3_3);
//                }
//
//            }else if(mode==1){
//
//                if (pageCount == 1) {
//                    view.loadUrl(JS_1);
//                } else if (pageCount == 2) {
//                    view.loadUrl(JS_2);
//                } else if (pageCount == 3) {
//                    view.loadUrl(JS_3_1);
//                    view.loadUrl(JS_3_2);
//                    view.loadUrl(JS_3_3);
//                }
//            }
//
//        }catch(InterruptedException e){
//
//        }
//
//
//    }
//
//}
