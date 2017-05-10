package com.example.masato.guruguru2;

import java.io.Serializable;

/**
 * Created by masato on 2017/05/04.
 */

public class ScenarioIndex implements Serializable {

    private String id;
    private String name;
    private String memo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
