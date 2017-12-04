package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by masato on 2017/05/05.
 */

public class ScenarioApi extends AsyncTask<Integer, Integer, Integer> {

    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;

    //Input
    private List<ScenarioIndex> scenarioIndexList;

    //Output
    private List<Scenario> scenarioList;

    //Constractor
    public ScenarioApi(Activity act, List<ScenarioIndex> sil, List<Scenario> scl) {

        this.activity = act;
        this.scenarioIndexList = sil;
        this.scenarioList = scl;
    }

    //------------------------
    // Must Implement Methods
    //------------------------

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("命令情報を取得中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100); dialog.setProgress(0);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... values) {

        //テストデータ生成
        convertObject(getData());
        return 0;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);

        dialog.setProgress(values[0].intValue());
    }


    @Override
    protected void onPostExecute(Integer result) {

        super.onPostExecute(result);

        dialog.setProgress(100);
        dialog.dismiss();
        callbacktask.callBack(result);
    }



    //-----------------------
    // Original Methods
    //-----------------------

    //サーバよりjson取得
    private List<String> getData(){

        List<String> jsons = new ArrayList<String>();
        HttpURLConnection con = null;
        StringBuffer result;

        try {

            for(int i=0; i<scenarioIndexList.size(); i++) {

                result = new StringBuffer();
                ScenarioIndex index = scenarioIndexList.get(i);

                URL url = new URL(AppStatics.URL_SCENARIO_CTR + "?id=" + index.getId());

                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("GET");
                con.connect();

                // HTTPレスポンスコード
                final int status = con.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    // 通信に成功した
                    // テキストを取得する
                    final InputStream in = con.getInputStream();
                    String encoding = con.getContentEncoding();
                    if (null == encoding) {
                        encoding = "UTF-8";
                    }
                    final InputStreamReader inReader = new InputStreamReader(in, encoding);
                    final BufferedReader bufReader = new BufferedReader(inReader);
                    String line = null;
                    // 1行ずつテキストを読み込む
                    while ((line = bufReader.readLine()) != null) {
                        result.append(line);
                    }
                    bufReader.close();
                    inReader.close();
                    in.close();
                } else {
                    System.out.println(status);
                }

                jsons.add(result.toString());
                Log.d("getData", result.toString());
            }

        }catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }

        return jsons;

    }

    //json => オブジェクト(scenarioList)へ変換
    private void convertObject(List<String> jsons){

        JSONObject jo = null;
        JSONObject jo_scene = null;
        JSONObject jo_action = null;
        JSONObject jo_child = null;
        JSONObject jo_check = null;

        JSONArray ja_scene = null;
        JSONArray ja_action = null;
        JSONArray ja_child = null;
        JSONArray ja_check = null;

        try {
            for(int i=0; i<jsons.size(); i++){

                Scenario scenario = new Scenario();
                List<Scene> sceneList = new ArrayList<Scene>();

                jo = new JSONObject(jsons.get(i));
                if(jo.getString("result").equals("-1") || (jo.getString("result").equals("1") && jo.getString("sceneNum").equals("0"))){
                    scenario.setValid(false);
                    scenario.setScenarioIndex(scenarioIndexList.get(i));
                    scenarioList.add(scenario);
                    continue;
                }

                scenario.setValid(true);
                scenario.setScenarioIndex(scenarioIndexList.get(i));

                ja_scene = jo.getJSONArray("scene");
                for(int j=0; j<ja_scene.length(); j++){

                    Scene scene = new Scene();
                    jo_scene = ja_scene.getJSONObject(j);
                    List<Action> actionList = new ArrayList<Action>();
                    List<SceneCheck> checkList = new ArrayList<SceneCheck>();

                    scene.setScene_id(jo_scene.getString("scene_id"));
                    scene.setNum(jo_scene.getInt("scene_num"));
                    scene.setUrl(jo_scene.getString("scene_url"));
                    scene.setMemo(jo_scene.getString("scene_memo"));

                    if(j!=(ja_scene.length()-1)) {
                        ja_action = jo_scene.getJSONArray("action");
                        for (int k = 0; k < ja_action.length(); k++) {

                            Action action = new Action();
                            jo_action = ja_action.getJSONObject(k);
                            List<Object> childList = new ArrayList<Object>();

                            action.setAction_id(jo_action.getString("action_id"));
                            action.setNum(jo_action.getInt("action_num"));
                            action.setMemo(jo_action.getString("action_memo"));
                            action.setSleep(jo_action.getInt("sleep"));
                            action.setType(jo_action.getInt("action_type"));

                            ja_child = jo_action.getJSONArray("child");
                            for (int l = 0; l < ja_child.length(); l++) {

                                if (action.getType() == 0) {

                                    ActionClick ac = new ActionClick();
                                    jo_child = ja_child.getJSONObject(l);

                                    ac.setNum(jo_child.getInt("target_num"));
                                    ac.setType(jo_child.getInt("click"));
                                    ac.setTagName(jo_child.getString("tagName"));
                                    ac.setAttName(jo_child.getString("attName"));
                                    ac.setAttValue(jo_child.getString("attValue"));
                                    ac.setDeep(jo_child.getInt("deep"));

                                    childList.add(ac);

                                } else if (action.getType() == 1) {

                                    ActionInput ai = new ActionInput();
                                    jo_child = ja_child.getJSONObject(l);

                                    ai.setNum(jo_child.getInt("input_num"));
                                    ai.setAttValue(jo_child.getString("attValue"));
                                    ai.setInputValue(jo_child.getString("inputValue"));

                                    childList.add(ai);

                                } else if (action.getType() == 2) {

                                    ActionPulldown pd = new ActionPulldown();
                                    jo_child = ja_child.getJSONObject(l);

                                    pd.setNum(jo_child.getInt("pulldown_num"));
                                    pd.setAttName(jo_child.getString("attName"));
                                    pd.setAttValue(jo_child.getString("attValue"));
                                    pd.setIndex(jo_child.getInt("selectIndex"));

                                    childList.add(pd);
                                }

                            }

                            action.setChildList(childList);
                            makeVariableScript(action);
                            actionList.add(action);
                        }
                    }

                    scene.setCheck_valid(jo_scene.getInt("check_valid"));
                    if(jo_scene.getInt("check_valid")==1){

                        ja_check = jo_scene.getJSONArray("check");
                        for(int k = 0; k < ja_check.length(); k++) {
                            jo_check = ja_check.getJSONObject(k);

                            SceneCheck check = new SceneCheck();
                            check.setCheck_id(jo_check.getString("check_id"));
                            check.setMemo(jo_check.getString("check_memo"));
                            check.setCheck_type(jo_check.getInt("check_type"));
                            if (jo_check.getInt("check_type") == 0) {

                                check.setTagName(jo_check.getString("tagName"));
                                check.setAttName(jo_check.getString("attName"));
                                check.setAttValue(jo_check.getString("attValue"));
                                check.setStr_type(jo_check.getInt("str_type"));
                                check.setDeep(jo_check.getInt("deep"));
                                check.setOrigin_str(jo_check.getString("origin_str"));
                            } else if (jo_check.getInt("check_type") == 1) {

                                check.setUrl(jo_check.getString("check_url"));
                            }

                            makeVariableScriptForCheck(check);
                            checkList.add(check);
                        }



                    }

                    scene.setCheckList(checkList);
                    scene.setActionList(actionList);
                    sceneList.add(scene);
                }

                scenario.setSceneList(sceneList);
                scenarioList.add(scenario);
            }

        } catch (JSONException e) {
            Log.d("jsonConvertError", e.toString());
            //return -1;
        }


    }

    //jsの動的生成
    private void makeVariableScript(Action action){

        List<Object> childList = action.getChildList();
        List<String> execJS = new ArrayList<String>();
        List<String> preJS = new ArrayList<String>();

        final String header = "javascript:";
        final String header2 = "document";

        if(action.getType()==0){
            //クリック
             String strE = header + header2;
             String strP = header2;
             String lastTagName = "";
             for(int i=0; i<childList.size(); i++){

                 ActionClick ac = (ActionClick) childList.get(i);
                 String str = "";

                 if(ac.getType()==0){
                     //通常
                     if(ac.getDeep()<=1){
                         //一意に絞り込み
                         if (ac.getAttName() == null || ac.getAttName().isEmpty()) {
                             //属性情報なし
                             str += ".querySelector('" + ac.getTagName() + "')";
                         } else {
                             //属性情報あり
                             str += ".querySelector('" + ac.getTagName() + "[" + ac.getAttName() + "=\"" + ac.getAttValue() + "\"]')";
                         }
                     }else if(ac.getDeep()>1){
                         //複数あり
                         if (ac.getAttName() == null || ac.getAttName().isEmpty()) {
                             //属性情報なし
                             str += ".querySelectorAll('" + ac.getTagName() + "')[" + (ac.getDeep()-1) + "]";
                         } else {
                             //属性情報あり
                             str += ".querySelectorAll('" + ac.getTagName() + "[" + ac.getAttName() + "=\"" + ac.getAttValue() + "\"]')[" + (ac.getDeep()-1) + "]";
                         }
                     }
                     lastTagName = ac.getTagName();
                     strE += str;
                     strP += str;

                 }else{
                     //クリック対象
                     if(ac.getTagName().equals(lastTagName)){
                         //指定されたクリック対象タグと、最後に絞り込んだタグが一致していれば絞り込まない
                        strE += ".click();";
                        strP += ";";
                     }else{
                         //指定タグでさらに絞り込んでクリック
                         if(ac.getDeep()<=1) {
                             //一意に絞り込み
                             strE += ".querySelector('" + ac.getTagName() + "').click();";
                             strP += ".querySelector('" + ac.getTagName() + "');";
                         }else if(ac.getDeep()>1) {
                             //複数あり
                             strE += ".querySelectorAll('" + ac.getTagName() + "')[" + (ac.getDeep()-1) + "].click();";
                             strP += ".querySelectorAll('" + ac.getTagName() + "')[" + (ac.getDeep()-1) + "];";
                         }
                     }
                 }
             }

             //実行スクリプトに登録
             execJS.add(strE);
             preJS.add(strP);


        }else if(action.getType()==1){
            //フォーム入力

            for(int i=0; i<childList.size(); i++){
                ActionInput ai = (ActionInput)childList.get(i);
                String strE = header + header2;
                String strP = header2;

                //フォーム絞り込み
                strE += ".querySelector('input[name=\"" + ai.getAttValue() + "\"]')";
                strP += ".querySelector('input[name=\"" + ai.getAttValue() + "\"]');";

                //入力値指定
                strE += ".value='" + ai.getInputValue() + "'; void 0;";

                //実行スクリプトに登録
                execJS.add(strE);
                preJS.add(strP);
            }

        }else if(action.getType()==2){
            //プルダウン選択
            for(int i=0; i<childList.size(); i++) {
                ActionPulldown pd = (ActionPulldown)childList.get(i);
                String strE = header + header2;
                String strP = header2;

                //プルダウン絞り込み
                strE += ".querySelector('select[" + pd.getAttName() + "=\"" + pd.getAttValue() + "\"]')";
                strP += ".querySelector('select[" + pd.getAttName() + "=\"" + pd.getAttValue() + "\"]');";

                //選択
                strE += ".selectedIndex=" + (pd.getIndex()-1) + "; void 0;";

                //実行スクリプトに登録
                execJS.add(strE);
                preJS.add(strP);
            }

        }

        //Actionオブジェクトに実行スクリプトリストを登録
        action.setExecJS(execJS);
        action.setPreJS(preJS);
    }


    private void makeVariableScriptForCheck(SceneCheck check){

        final String header = "javascript:";
        final String header2 = "document";

        final String footer = ".innerText";

        String strE = header + header2;
        //String strP = header2;
        //String lastTagName = "";

        if(check.getCheck_type()==0){

            String str = "";

            //通常
            if(check.getDeep()<=1){
                //一意に絞り込み
                if (check.getAttName() == null || check.getAttName().isEmpty()) {
                    //属性情報なし
                    str += ".querySelector('" + check.getTagName() + "')";
                } else {
                    //属性情報あり
                    str += ".querySelector('" + check.getTagName() + "[" + check.getAttName() + "=" + check.getAttValue() + "]')";
                }
            }else if(check.getDeep()>1){
                //複数あり
                if (check.getAttName() == null || check.getAttName().isEmpty()) {
                    //属性情報なし
                    str += ".querySelectorAll('" + check.getTagName() + "')[" + (check.getDeep()-1) + "]";
                } else {
                    //属性情報あり
                    str += ".querySelectorAll('" + check.getTagName() + "[" + check.getAttName() + "=" + check.getAttValue() + "]')[" + (check.getDeep()-1) + "]";
                }
            }
            strE += str;
            //strP += str;

            strE += footer;

            check.setPreJS(strE);
        }

    }


    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }


    /**
     * コールバック用のstaticなclass
     */
    public static class CallBackTask {
        public void callBack(Integer result) {
        }
    }
}
