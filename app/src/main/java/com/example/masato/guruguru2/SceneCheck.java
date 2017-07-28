package com.example.masato.guruguru2;

/**
 * Created by masato on 2017/07/18.
 */

public class SceneCheck {

    private String check_id;
    private String memo;
    private Integer check_type;

    private String tagName;
    private String attName;
    private String attValue;
    private String origin_str;
    private Integer str_type;
    private Integer deep;
    private String preJS;

    private String url;


    public Integer getDeep() {
        return deep;
    }

    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    public String getPreJS() {
        return preJS;
    }

    public void setPreJS(String preJS) {
        this.preJS = preJS;
    }

    public String getCheck_id() {
        return check_id;
    }

    public void setCheck_id(String check_id) {
        this.check_id = check_id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getCheck_type() {
        return check_type;
    }

    public void setCheck_type(Integer check_type) {
        this.check_type = check_type;
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

    public String getOrigin_str() {
        return origin_str;
    }

    public void setOrigin_str(String origin_str) {
        this.origin_str = origin_str;
    }

    public Integer getStr_type() {
        return str_type;
    }

    public void setStr_type(Integer str_type) {
        this.str_type = str_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
