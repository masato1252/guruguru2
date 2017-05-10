package com.example.masato.guruguru2;

import android.app.Application;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2017/05/04.
 */

public class AppStatics extends Application {

    //------------------------
    // アプリ内で共有の定数を定義
    //------------------------

    public static final String URL_SCENARIO_LIST = "http://concierge-apps.lovepop.jp/guruguru/json_scenario.php";
    public static final String URL_SCENARIO_CTR = "http://concierge-apps.lovepop.jp/guruguru/json_scenario_ctr.php";

    //シナリオ選択用
    public List<Boolean> selectScenarios = new ArrayList<Boolean>();
    public List<ScenarioIndex> selectScenarioIndexes = new ArrayList<ScenarioIndex>();

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


    public void resetSelectScenarios(){
        selectScenarios = new ArrayList<Boolean>();
    }

    public void resetSelectScenarioIndexes(){
        selectScenarioIndexes = new ArrayList<ScenarioIndex>();
    }

}
