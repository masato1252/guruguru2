package com.example.masato.guruguru2;

import java.util.List;

/**
 * Created by masato on 2017/05/04.
 */

public class Action {

    private Integer num;
    private String action_id;
    private String memo;
    private Integer type;
    private Integer sleep;
    private List<Object> childList; //type=0

    private List<String> execJS; //実行スクリプト
    private List<String> preJS; //タグ存在チェック用スクリプト


    public List<String> getExecJS() {
        return execJS;
    }

    public void setExecJS(List<String> execJS) {
        this.execJS = execJS;
    }

    public List<String> getPreJS() {
        return preJS;
    }

    public void setPreJS(List<String> preJS) {
        this.preJS = preJS;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSleep() {
        return sleep;
    }

    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }

    public List<Object> getChildList() {
        return childList;
    }

    public void setChildList(List<Object> childList) {
        this.childList = childList;
    }
}
