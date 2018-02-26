package com.example.masato.guruguru2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by masato on 2018/02/26.
 */

public class SceneCheckGroup {

    private String group_id;
    private List<SceneCheck> checkList;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public List<SceneCheck> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<SceneCheck> checkList) {
        this.checkList = checkList;
    }

}
