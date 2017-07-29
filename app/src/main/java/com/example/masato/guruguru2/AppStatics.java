package com.example.masato.guruguru2;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by masato on 2017/05/04.
 */

public class AppStatics extends Application {

    //------------------------
    // アプリ内で共有の定数を定義
    //------------------------

    //GET 参照系
    public static final String URL_SCENARIO_LIST = "http://tk2-220-19891.vs.sakura.ne.jp/guruguru/json_scenario.php";
    public static final String URL_SCENARIO_CTR = "http://tk2-220-19891.vs.sakura.ne.jp/guruguru/json_scenario_ctr2.php";

    //POST 更新系
    public static final String URL_SEND_OPERATION_LOG = "http://tk2-220-19891.vs.sakura.ne.jp/guruguru/regist_operation_log.php";
    public static final String URL_SEND_ERROR_LOG = "http://tk2-220-19891.vs.sakura.ne.jp/guruguru/regist_error_log.php";

    //シナリオ選択用
    public List<Boolean> selectScenarios = new ArrayList<Boolean>();
    public List<ScenarioIndex> selectScenarioIndexes = new ArrayList<ScenarioIndex>();

    //オペレーションID (監視起動毎に生成・更新)
    private String operationId = "";

    //ネットワーク接続種別(1:Mobile, 2:WiFi)
    private Integer networkType = 0;

    //------------------------
    // シングルトン
    //------------------------

    private static AppStatics instance = new AppStatics();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static AppStatics getInstance() {
        return instance;
    }


    //------------------------
    // Original Method
    //------------------------

    public void resetSelectScenarios(){
        selectScenarios = new ArrayList<Boolean>();
    }

    public void resetSelectScenarioIndexes() {
        selectScenarioIndexes = new ArrayList<ScenarioIndex>();
    }

    public Integer outputSleepTime(Integer num) {
        if(num==0){
            return 0;
        }else if(num==1){
            return 2000;
        }else if(num==2){
            return 5000;
        }else if(num==3){
            return 10000;
        }

        return 0;
    }

    //オペレーションID生成（監視起動毎に呼び出し）
    public void makeOperationId() {

        String tmp = "";
        Random rand = new Random();
        Integer num = 0;

        for(int i=0; i<4; i++){
            num = rand.nextInt(89999) + 10000;
            tmp += num.toString();
        }

        this.operationId = tmp;
    }

    public String getOperationId() {
        return this.operationId;
    }


    //ネットワーク接続種別の取得
    public void modifyNetworkType() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //NetworkInfo.State networkState = networkInfo.getState();
            this.networkType = 2;
        }else{
            this.networkType = 1;
        }
    }

    public Integer getNetworkType() {
        return this.networkType;
    }
}
