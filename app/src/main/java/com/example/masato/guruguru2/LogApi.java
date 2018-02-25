package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2017/07/29.
 *
 * mode
 * 1: Operation Log
 * 2: Operation Heart Beat
 * 3: Error Log
 */

public class LogApi extends AsyncTask<Integer, Integer, Integer> {

    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;
    private Context context;

    private Integer mode = 0;
    private List<String> params;
    private List<Scenario> scenarios;

    public LogApi(Activity act, Integer mode, List<String> params, List<Scenario> scenarios, Context cont) {

        this.activity = act;
        this.context = cont;
        this.mode = mode;
        if(params == null){
            this.params = new ArrayList<String>();
        }else{
            this.params = params;
        }
        this.scenarios = scenarios;
    }

    //------------------------
    // Must Implement Methods
    //------------------------

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Please wait");
        if(mode==1){
            dialog.setMessage("サーバへ稼働情報を送信中...");
        }else if(mode==2){
            dialog.setMessage("サーバへ稼働情報を送信中...");
        }else if(mode==3){
            dialog.setMessage("サーバへエラーログを送信中...");
        }
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100); dialog.setProgress(0);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {

        if(mode==1){
            if( checkPostValid(postData()) ){
                return 1;
            }else{
                return -1;
            }
        }else if(mode==2){
            if( checkPostValid(postData()) ){
                return 1;
            }else{
                return -1;
            }

        }else if(mode==3){
            if( checkPostValid(postData()) ){
                return 1;
            }else{
                return -1;
            }

        }

        return -1;
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

        //scListAdapter.notifyDataSetChanged();
        callbacktask.callBack(result);
    }


    //-----------------------
    // Original Methods
    //-----------------------

    private String postData() {

        HttpURLConnection con = null;
        URL url = null;
        String readSt = "";

        try {

            if(mode==1){
                url = new URL(AppStatics.URL_SEND_OPERATION_LOG);
            }else if(mode==2){
                url = new URL(AppStatics.URL_SEND_OPERATION_BEAT);
            }else if(mode==3){
                url = new URL(AppStatics.URL_SEND_ERROR_LOG);
            }

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);

            //パラメータ生成
            if(mode==1){
                makeOperationLogParams();
            }else if(mode==2){
                makeOperationHeartBeatParams();
            }else if(mode==3){

            }

            String parameter = "";
            for(String param : params){
                if(!parameter.equals("")) parameter += "&";
                parameter += param;
            }

            //OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            out.write(parameter);
            out.flush();
            out.close();

            con.connect();
            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                //コネクト成功
                InputStream in = con.getInputStream();
                readSt = readInputStream(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
        if (readSt.equals("")) return "";

        return readSt;

    }


    private String readInputStream(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer();
        String st = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null) sb.append(st);
        try { in.close(); }  catch(Exception e) { e.printStackTrace(); }

        return sb.toString();
    }

    public void setParameter(String key, String value){
        if(key.equals("")||value.equals("")) return;
        params.add(key + "=" + value);
        return;
    }

    //送信用の稼働情報を生成
    private void makeOperationLogParams() {

        setParameter("operation_id", AppStatics.getInstance().getOperationId());
        setParameter("os_version", "Android " + Build.VERSION.RELEASE);
        setParameter("manufacturer", Build.MANUFACTURER);
        setParameter("model", Build.MODEL);
        setParameter("product", Build.PRODUCT);
        setParameter("phone_id", Build.ID);
        setParameter("network_type", AppStatics.getInstance().getNetworkType().toString());

        Integer c = 1;
        for(Scenario scenario : scenarios){
            setParameter("scenario"+c, scenario.getScenarioIndex().getId());
            c++;
        }
    }

    //ハートビート用のパラメータ生成
    private void makeOperationHeartBeatParams() {

        setParameter("operation_id", AppStatics.getInstance().getOperationId());
    }

    //返却されたjsonよりresultコードをチェック
    private Boolean checkPostValid(String json_str) {

        try {
            JSONObject jo = new JSONObject(json_str);
            Log.d("JSONParse", jo.toString());

            Integer state = jo.getInt("result");

            if(state==1){
                //送信成功
                Log.d("Post", "OK");
                return true;
            }else{
                //送信失敗
                Log.d("Fault Cause", jo.getString("cause"));
                return false;
            }

        } catch (JSONException e) {
            Log.d("jsonConvertError", "jsonExceprion:"+e);
            return false;
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
