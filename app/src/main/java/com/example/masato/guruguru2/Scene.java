package com.example.masato.guruguru2;

import java.util.List;

/**
 * Created by masato on 2017/05/04.
 */

public class Scene {

    private Integer num;
    private String scene_id;
    private String url;
    private String memo;
    private List<Action> actionList;

    private Integer check_valid;
    private List<SceneCheck> checkList;

    public List<SceneCheck> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<SceneCheck> checkList) {
        this.checkList = checkList;
    }

    public Integer getCheck_valid() {
        return check_valid;
    }

    public void setCheck_valid(Integer check_valid) {
        this.check_valid = check_valid;
    }


    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<Action> getActionList() {
        return actionList;
    }

    public void setActionList(List<Action> actionList) {
        this.actionList = actionList;
    }
}
