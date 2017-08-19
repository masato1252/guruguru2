package com.example.masato.guruguru2;

/**
 * Created by masato on 2017/08/16.
 */

public interface PickerListener {

    void sendTags(String tags, String url, String title);

    void notifyUrl(String url);

}
