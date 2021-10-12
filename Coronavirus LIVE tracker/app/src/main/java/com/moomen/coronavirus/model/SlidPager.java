package com.moomen.coronavirus.model;


public class SlidPager {
    private String imageNews;
    private String titleNews;
    private String publishDate;
    private int iconTime;

    public SlidPager(String imageNews, String titleNews, String publishDate, int iconTime) {
        this.imageNews = imageNews;
        this.titleNews = titleNews;
        this.publishDate = publishDate;
        this.iconTime = iconTime;
    }

    public String getImageNews() {
        return imageNews;
    }

    public void setImageNews(String imageNews) {
        this.imageNews = imageNews;
    }

    public String getTitleNews() {
        return titleNews;
    }

    public void setTitleNews(String titleNews) {
        this.titleNews = titleNews;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getIconTime() {
        return iconTime;
    }

    public void setIconTime(int iconTime) {
        this.iconTime = iconTime;
    }
}
