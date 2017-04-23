package com.example.masato.guruguru2;

/**
 * Created by masato on 2017/04/23.
 */

public class SearchTargetData {

    /*
        type Definition
        0:ターゲット配下に要素は1つのみ
        1:ターゲット配下に複数要素あり(childNumで絞り込む必要あり)
     */

    private Integer num;
    private Integer type;
    private Integer childNum;
    private String tagName;
    private String attName;
    private String attValue;


    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getChildNum() {
        return childNum;
    }

    public void setChildNum(Integer childNum) {
        this.childNum = childNum;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    public String getAttValue() {
        return attValue;
    }

    public void setAttValue(String attValue) {
        this.attValue = attValue;
    }
}
