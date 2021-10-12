package com.moomen.coronavirus.model;

public class News {

    private String imageUrl;
    private String title;
    private String description;
    private String timeDiff;
    private String date;
    private String url;
    private String sourceName;

    public News(String imageUrl, String title, String description, String date, String url, String sourceName) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.date = date;
        this.url = url;
        this.sourceName = sourceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceName() { return sourceName; }

    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public boolean isValidNews() {
        return (isValid(this.getImageUrl()) && isValid(this.getTitle()) && isValid(this.getDescription())
                && isValid(this.getUrl()) && isValid(sourceName));
    }

    private boolean isValid(String s) {
        return !s.equals("null") && !s.equals("");
    }
}
