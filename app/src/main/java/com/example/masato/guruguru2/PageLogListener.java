package com.example.masato.guruguru2;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by masato on 2017/04/22.
 */

public interface PageLogListener extends EventListener{

    void dispLog(String str);

    void sendLogHeartBeat();

    void faultSendErrorLog();

    void sendErrorLog(List<String> errorParams);

    void playAlertMusic();
    
//    void completeTest();

}
