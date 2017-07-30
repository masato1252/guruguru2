package com.example.masato.guruguru2;

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

//    public void backToActivity() {
//
//        listener.completeTest();
//    }

    // リスナーをセットする
    public void setListener(PageLogListener listener) {

        this.listener = listener;
    }


}
