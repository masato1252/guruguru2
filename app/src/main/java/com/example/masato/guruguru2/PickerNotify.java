package com.example.masato.guruguru2;

/**
 * Created by masato on 2017/08/16.
 */

public class PickerNotify {

    private PickerListener listener;

    void sendTags(String tags){

        listener.sendTags(tags);
    }

    void nofityUrl(String url){

        listener.notifyUrl(url);
    }
}
