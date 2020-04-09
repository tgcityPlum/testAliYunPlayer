package com.tgcity.testaliyunplayer.bean;


/**
 * @author TGCity
 * @description 视频列表的显示核心数据
 */
public class PlayData {

    /**
     * 视频url
     */
    private String url;

    /**
     * 视频id
     */
    private String id;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 是否选中
     */
    private boolean isChoose;

    public PlayData() {
    }

    public PlayData(String url, String id, String title) {
        this.url = url;
        this.id = id;
        this.title = title;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
