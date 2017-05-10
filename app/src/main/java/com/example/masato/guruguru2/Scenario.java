package com.example.masato.guruguru2;

import java.util.List;

/**
 * Created by masato on 2017/05/05.
 */

public class Scenario {

    private Boolean valid;  //取得時の正常性
    private ScenarioIndex scenarioIndex;
    private List<Scene> sceneList;

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public ScenarioIndex getScenarioIndex() {
        return scenarioIndex;
    }

    public void setScenarioIndex(ScenarioIndex scenarioIndex) {
        this.scenarioIndex = scenarioIndex;
    }

    public List<Scene> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<Scene> sceneList) {
        this.sceneList = sceneList;
    }
}
