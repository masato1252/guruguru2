package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2017/08/19.
 */

public class PickerApi extends AsyncTask<Integer, Integer, Integer> {

    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;

    private Integer mode = 0;
    private String tags, url, title;

    public PickerApi(Activity act, Integer mode, String tags, String url, String title) {

        this.activity = act;
        this.mode = mode;
        this.tags = tags;
        this.url = url;
        this.title = title;
    }

    //------------------------
    // Must Implement Methods
    //------------------------

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Please wait");

        dialog.setMessage("タグを送信中...");

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100); dialog.setProgress(0);
        dialog.show();

        super.onPreExecute();
    }


    @Override
    protected Integer doInBackground(Integer... params) {
        if( checkPostValid(postData()) ){
            return 1;
        }else{
            return -1;
        }
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

    private String postData() {

        HttpURLConnection con = null;
        URL url = null;
        String readSt = "";

        try {

            url = new URL(AppStatics.URL_SEND_HTMLTAG);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);

            String parameter = "title" + title + "&url" + url + "&tags=" + tags;

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
