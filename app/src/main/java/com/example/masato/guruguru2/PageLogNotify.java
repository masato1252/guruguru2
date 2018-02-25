package com.example.masato.guruguru2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2017/04/22.
 */

public class PageLogNotify {

    private PageLogListener listener;

    // 画像が表示された事を通知
    public void sendLogsToActivity(String str) {

        listener.dispLog(str);
    }

    //監視のハートビート
    public void sendLogHeartBeat() {

        listener.sendLogHeartBeat();
    }

    //エラーログ送信エラー時にダイアログ表示
    public void faultSendErrorLog() {

        listener.faultSendErrorLog();
    }

    public void sendErrorLog(List<String> errorParams) {

        listener.sendErrorLog(errorParams);
    }

    //アラート音を再生
    public void playAlertMusic() {

        listener.playAlertMusic();
    }

//    public void backToActivity() {
//
//        listener.completeTest();
//    }

    // リスナーをセットする
    public void setListener(PageLogListener listener) {

        this.listener = listener;
    }



}
