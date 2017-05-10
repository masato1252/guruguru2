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
import java.util.List;

/**
 * Created by masato on 2017/05/04.
 */

public class ScenarioIndexApi extends AsyncTask<Integer, Integer, Integer> {

    private Activity activity;
    private CallBackTask callbacktask;
    private ProgressDialog dialog;
    private List<ScenarioIndex> scList;

    private ScenarioListAdapter scListAdapter;

    public ScenarioIndexApi(Activity act, List<ScenarioIndex> list, ScenarioListAdapter sla) {

        this.activity = act;
        this.scList = list;
        this.scListAdapter = sla;

    }

    //------------------------
    // Must Implement Methods
    //------------------------

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("シナリオ一覧を取得中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100); dialog.setProgress(0);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {

        return addToList(getData());
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

        scListAdapter.notifyDataSetChanged();
        callbacktask.CallBack(0);
    }


    //-----------------------
    // Original Methods
    //-----------------------

    private String getData(){

        HttpURLConnection con = null;
        StringBuffer result = new StringBuffer();

        try {

            URL url = new URL(AppStatics.URL_SCENARIO_LIST);

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
                if(null == encoding){
                    encoding = "UTF-8";
                }
                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            }else{
                System.out.println(status);
            }

        }catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }

        return result.toString();

    }


    private Integer addToList(String str){

        Integer count = null;
        ScenarioIndex sd = null;
        JSONObject tmp = null;

        try {

            JSONObject jo = new JSONObject(str);
            Log.d("JSONParse", jo.toString());

            Integer state = jo.getInt("result");

            if(state == 1){
                //json取得成功
                count = jo.getInt("count");

                if(count==0){
                    //取得0件
                    return count;
                }else{
                    //1件以上正常取得
                    JSONArray jArray = jo.getJSONArray("list");
                    for(int i=0; i<jArray.length(); i++) {
                        sd = new ScenarioIndex();
                        tmp = jArray.getJSONObject(i);
                        sd.setId(tmp.getString("scenario_id"));
                        sd.setName(tmp.getString("name"));
                        sd.setMemo(tmp.getString("memo"));
                        scList.add(sd);
                    }
                }

//                onProgressUpdate(Math.round((float)((float)i/(float)a)));

            }else if(state == -1){
                //json取得エラー
                Log.d("jsonGetError", tmp.getString("cause"));
                return -1;
            }

        } catch (JSONException e) {
            Log.d("jsonConvertError", "jsonExceprion");
            return -1;
        }

        return count;
    }


    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }


    /**
     * コールバック用のstaticなclass
     */
    public static class CallBackTask {
        public void CallBack(Integer result) {
        }
    }


}
