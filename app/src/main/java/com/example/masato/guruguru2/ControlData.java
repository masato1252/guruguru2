package com.example.masato.guruguru2;

import java.util.List;

/**
 * Created by masato on 2017/04/23.
 */

public class ControlData {

    /*
        mode Definition
        0:操作開始(INIT)
        1:リンクのクリック
        2:フォームの入力=>送信
     */


    private Integer mode;
    private Integer num;

    private Integer sleepTime;

    // for 初回(mode==0) 最初に開くリンク
    private String firstUrl;

    // for リンク(mode==1)
    private List<SearchTargetData> targetArray;
    private String clickTagName;
    private String clickAttName;
    private String clickAttValue;


    // for フォーム入力(mode==2)
        //入力情報の配列
    private List<InputControlData> inputArray;
    private String sendBtnAttName;
    private String sendBtnAttValue;



    public Integer getMode() { return mode; }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getFirstUrl() {
        return firstUrl;
    }

    public void setFirstUrl(String firstUrl) {
        this.firstUrl = firstUrl;
    }

    public String getClickTagName() {
        return clickTagName;
    }

    public void setClickTagName(String clickTagName) {
        this.clickTagName = clickTagName;
    }

    public String getClickAttName() {
        return clickAttName;
    }

    public void setClickAttName(String clickAttName) {
        this.clickAttName = clickAttName;
    }

    public String getClickAttValue() {
        return clickAttValue;
    }

    public void setClickAttValue(String clickAttValue) {
        this.clickAttValue = clickAttValue;
    }

    public List<InputControlData> getInputArray() {
        return inputArray;
    }

    public void setInputArray(List<InputControlData> inputArray) {
        this.inputArray = inputArray;
    }

    public String getSendBtnAttName() {
        return sendBtnAttName;
    }

    public void setSendBtnAttName(String sendBtnAttName) {
        this.sendBtnAttName = sendBtnAttName;
    }

    public String getSendBtnAttValue() {
        return sendBtnAttValue;
    }

    public void setSendBtnAttValue(String sendBtnAttValue) {
        this.sendBtnAttValue = sendBtnAttValue;
    }

    public List<SearchTargetData> getTargetArray() {
        return targetArray;
    }

    public void setTargetArray(List<SearchTargetData> targetArray) {
        this.targetArray = targetArray;
    }

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }
}
