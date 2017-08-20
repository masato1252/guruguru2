package com.example.masato.guruguru2;

/**
 * Created by masato on 2017/08/16.
 */

public class PickerNotify {

    private PickerListener listener;

    void sendTags(String tags, String url, String title){

        listener.sendTags(tags, url, title);
    }

    void notifyUrl(String url){

        listener.notifyUrl(url);
    }

    // リスナーをセットする
    public void setListener(PickerListener listener) {

        this.listener = listener;
    }
}
