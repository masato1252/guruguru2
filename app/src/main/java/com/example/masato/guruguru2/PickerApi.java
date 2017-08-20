package com.example.masato.guruguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by masato on 2017/08/19.
 */

public class PickerApi extends AsyncTask<Integer, Integer, Integer> {

    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;

    private Integer mode = 0;
    private String fileName;

    private Map<String, String> params;

    public PickerApi(Activity act, Integer mode, String fileName, String url, String title) {

        this.activity = act;
        this.mode = mode;
        this.fileName = fileName;
        this.params = new HashMap<String, String>();
        params.put("url", url);
        params.put("title", title);
        params.put("fileName", fileName);
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
        if( checkPostValid(sendMultiPart()) ){
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

//    private String postData() {
//
//        HttpURLConnection con = null;
//        URL url = null;
//        String readSt = "";
//
//        try {
//
//            url = new URL(AppStatics.URL_SEND_HTMLTAG);
//
//            con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("POST");
//            con.setInstanceFollowRedirects(false);
//            con.setDoOutput(true);
//
//            String parameter = "title=" + title + "&url=" + pageUrl + "&tags=" + tags;
//
//            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
//            out.write(parameter);
//            out.flush();
//            out.close();
//
//            con.connect();
//            int status = con.getResponseCode();
//            if (status == HttpURLConnection.HTTP_OK) {
//                //コネクト成功
//                InputStream in = con.getInputStream();
//                readSt = readInputStream(in);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (con != null) con.disconnect();
//        }
//        if (readSt.equals("")) return "";
//
//        return readSt;
//
//    }


    private String sendMultiPart(){

        final String twoHyphens = "--";
        final String boundary =  "*****"+ UUID.randomUUID().toString()+"*****";
        final String lineEnd = "\r\n";
        final int maxBufferSize = 1024*1024*3;

        HttpURLConnection con = null;
        URL url = null;
        String readSt = "";
        DataOutputStream out;
        FileInputStream fileInputStream;

        try {

            url = new URL(AppStatics.URL_SEND_HTMLTAG);

            con = (HttpURLConnection) url.openConnection();


            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(twoHyphens + boundary + lineEnd);
            out.writeBytes("Content-Disposition: form-data; name=\"" + "tagFile" + "\"; filename=\"" + fileName +"\"" + lineEnd);
            out.writeBytes("Content-Type: application/octet-stream" + lineEnd);
            out.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            out.writeBytes(lineEnd);

            //FileInputStream fileInputStream = new FileInputStream(filepath);
            fileInputStream = activity.openFileInput(fileName);
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while(bytesRead > 0) {
                out.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            out.writeBytes(lineEnd);

            for (Map.Entry<String, String> entry : params.entrySet()) {
                out.writeBytes(twoHyphens + boundary + lineEnd);
                out.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                out.writeBytes("Content-Type: text/plain"+lineEnd);
                out.writeBytes(lineEnd);
                out.writeBytes(entry.getValue());
                out.writeBytes(lineEnd);
            }

            out.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

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


//    public void setParameter(String key, String value){
//        if(key.equals("")||value.equals("")) return;
//        params.add(key + "=" + value);
//        return;
//    }


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
