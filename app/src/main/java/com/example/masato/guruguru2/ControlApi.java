package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2017/04/23.
 */

public class ControlApi extends AsyncTask<Integer, Integer, List<JSData>> {


    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;

    private List<ControlData> controlList;

    private static final String inputId = "09090841258";
    private static final String inputPass = "MATSUURA8";
    private static final String extraJsStr = "javascript:";

    public ControlApi(Activity act) {

        this.activity = act;

    }


    //テストデータ生成
    private void makeTestControlList(Integer mode){

        if(mode==3) {
            controlList = new ArrayList<ControlData>();
            ControlData cd = new ControlData();
            cd.setMode(0);
            cd.setNum(0);
            cd.setFirstUrl("http://smt.docomo.ne.jp/");
            controlList.add(cd);


            cd = new ControlData();
            cd.setMode(1);
            cd.setNum(1);
            cd.setSleepTime(1000);
            List<SearchTargetData> stdList = new ArrayList<SearchTargetData>();
            SearchTargetData std = new SearchTargetData();
            std.setNum(0);
            std.setType(0);
            std.setTagName("a");
            std.setAttName("swid");
            std.setAttValue("dm_toplink");
            stdList.add(std);
            cd.setTargetArray(stdList);
            cd.setClickTagName("a");
            cd.setClickAttName("");
            cd.setClickAttValue("");
            controlList.add(cd);

            cd = new ControlData();
            cd.setMode(1);
            cd.setNum(2);
            cd.setSleepTime(1000);
            stdList = new ArrayList<SearchTargetData>();
            std = new SearchTargetData();
            std.setNum(0);
            std.setType(0);
            std.setTagName("dl");
            std.setAttName("id");
            std.setAttValue("docomoid_block_daccountlogin");
            stdList.add(std);
            cd.setTargetArray(stdList);
            cd.setClickTagName("a");
            cd.setClickAttName("");
            cd.setClickAttValue("");
            controlList.add(cd);

            cd = new ControlData();
            cd.setMode(2);
            cd.setNum(3);
            cd.setSleepTime(1000);
            List<InputControlData> icdList = new ArrayList<InputControlData>();
            InputControlData icd = new InputControlData();
            icd.setAttName("name");
            icd.setAttValue("authid");
            icd.setInputValue(inputId);
            icdList.add(icd);
            icd = new InputControlData();
            icd.setAttName("name");
            icd.setAttValue("authpass");
            icd.setInputValue(inputPass);
            icdList.add(icd);
            cd.setInputArray(icdList);
            cd.setSendBtnAttName("name");
            cd.setSendBtnAttValue("subForm");
            controlList.add(cd);

        }else if(mode==4) {

            controlList = new ArrayList<ControlData>();
            ControlData cd = new ControlData();
            cd.setMode(0);
            cd.setNum(0);
            cd.setFirstUrl("http://smt.docomo.ne.jp/");
            controlList.add(cd);


            cd = new ControlData();
            cd.setMode(1);
            cd.setNum(1);
            cd.setSleepTime(1000);
                List<SearchTargetData> stdList = new ArrayList<SearchTargetData>();
                SearchTargetData std = new SearchTargetData();
                std.setNum(0);
                std.setType(0);
                std.setTagName("div");
                std.setAttName("id");
                std.setAttValue("boxPoint");
                stdList.add(std);
                cd.setTargetArray(stdList);
            cd.setClickTagName("a");
            cd.setClickAttName("");
            cd.setClickAttValue("");
            controlList.add(cd);

            cd = new ControlData();
            cd.setMode(1);
            cd.setNum(2);
            cd.setSleepTime(1000);
                stdList = new ArrayList<SearchTargetData>();
                std = new SearchTargetData();
                std.setNum(0);
                std.setType(0);
                std.setTagName("ul");
                std.setAttName("class");
                std.setAttValue("cmn-footer__loginout");
                stdList.add(std);
                cd.setTargetArray(stdList);
            cd.setClickTagName("a");
            cd.setClickAttName("");
            cd.setClickAttValue("");
            controlList.add(cd);

            cd = new ControlData();
            cd.setMode(2);
            cd.setNum(3);
            cd.setSleepTime(1000);
                List<InputControlData> icdList = new ArrayList<InputControlData>();
                InputControlData icd = new InputControlData();
                icd.setAttName("name");
                icd.setAttValue("authid");
                icd.setInputValue(inputId);
                icdList.add(icd);
                icd = new InputControlData();
                icd.setAttName("name");
                icd.setAttValue("authpass");
                icd.setInputValue(inputPass);
                icdList.add(icd);
            cd.setInputArray(icdList);
            cd.setSendBtnAttName("name");
            cd.setSendBtnAttValue("subForm");
            controlList.add(cd);
        }
    }

    private List<JSData> makeVariableScript(){

        List<JSData> jsList = new ArrayList<JSData>();
        List<String> execJs;
        List<String> checkJs;

        for(ControlData cd: controlList) {

            execJs = new ArrayList<String>();
            checkJs = new ArrayList<String>();

            //命令JavaScriptの動的生成
            if (cd.getMode() == 0) {
                //初回アクセスURL

                execJs.add(cd.getFirstUrl());
                checkJs.add(cd.getFirstUrl());
            }else if (cd.getMode() == 1) {
                //リンクのクリック

                List<SearchTargetData> stdList = cd.getTargetArray();
                String eachExecStr = extraJsStr + "document";
                String eachCheckStr = "document";
                String lastTagName = "";
                for (SearchTargetData row : stdList) {
                    //クリック要素の絞り込み
                    String str = "";
                    if (row.getType() == 0) {
                        //配下は一意
                        if (row.getAttName() == null || row.getAttName().isEmpty()) {
                            //属性情報なし
                            str += ".querySelector('" + row.getTagName() + "')";
                        } else {
                            //属性情報あり
                            str += ".querySelector('" + row.getTagName() + "[" + row.getAttName() + "=" + row.getAttValue() + "]')";
                        }

                    } else if (row.getType() == 1) {
                        //配下は複数
                        if (row.getAttName() == null || row.getAttName().isEmpty()) {
                            //属性情報なし
                            str += ".querySelectorAll('" + row.getTagName() + "')[" + row.getChildNum().toString() + "]";
                        } else {
                            //属性情報あり
                            str += ".querySelectorAll('" + row.getTagName() + "[" + row.getAttName() + "=" + row.getAttValue() + "]')[" + row.getChildNum().toString() + "]";
                        }
                    }
                    lastTagName = row.getTagName();
                    eachExecStr += str;
                    eachCheckStr += str;
                }

                //指定されたクリック対象タグと、最後に絞り込んだタグが一致していれば無視する
                if (cd.getClickTagName().equals(lastTagName)) {
                    //無視してクリック
                    eachExecStr += ".click();";
                    eachCheckStr += ";";
                } else {
                    //指定タグでさらに絞り込んでクリック
                    eachExecStr += ".querySelector('" + cd.getClickTagName() + "').click();";
                    eachCheckStr += ".querySelector('" + cd.getClickTagName() + "');";
                }

                //実行スクリプトとして登録
                execJs.add(eachExecStr);
                checkJs.add(eachCheckStr);

            } else if (cd.getMode() == 2) {
                //フォームの自動入力

                List<InputControlData> icdList = cd.getInputArray();
                String eachExecStr;
                String eachCheckStr;
                for (InputControlData row : icdList) {
                    eachExecStr = extraJsStr + "document";
                    eachCheckStr = "document";
                    String str = "";

                    //フォーム絞り込み
                    str += ".querySelector('input[" + row.getAttName() + "=" + row.getAttValue() + "]')";
                    eachCheckStr += str + ";";
                    //入力値指定
                    str += ".value='" + row.getInputValue() + "'; void 0;";

                    //整形
                    eachExecStr += str;

                    //実行スクリプトとして登録
                    execJs.add(eachExecStr);
                    checkJs.add(eachCheckStr);
                }

                //送信ボタン押下用スクリプト生成
                eachExecStr = extraJsStr + "document.querySelector('input[" + cd.getSendBtnAttName() + "=" + cd.getSendBtnAttValue() + "]').click();";
                eachCheckStr = "document.querySelector('input[" + cd.getSendBtnAttName() + "=" + cd.getSendBtnAttValue() + "]');";

                //実行スクリプトとして登録
                execJs.add(eachExecStr);
                checkJs.add(eachCheckStr);
            }

            JSData js = new JSData();
            js.setExeScriptList(execJs);
            js.setCheckScriptList(checkJs);
            jsList.add(js);

        }

        return jsList;
    }


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("スクリプトを生成中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100); dialog.setProgress(0);
        dialog.show();



        super.onPreExecute();
    }

    @Override
    protected List<JSData> doInBackground(Integer... values) {

        //テストデータ生成
        makeTestControlList(values[0]);

        return makeVariableScript();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);

        dialog.setProgress(values[0].intValue());
    }


    @Override
    protected void onPostExecute(List<JSData> result) {

        super.onPostExecute(result);

        dialog.setProgress(100);
        dialog.dismiss();
        callbacktask.CallBack(result);
    }


    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }


    /**
     * コールバック用のstaticなclass
     */
    public static class CallBackTask {
        public void CallBack(List<JSData> result) {
        }
    }
}
