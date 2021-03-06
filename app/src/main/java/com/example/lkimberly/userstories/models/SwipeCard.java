package com.example.lkimberly.userstories.models;

public class SwipeCard {
    private String text1, text2, url;
    private Job job;

    public SwipeCard(String text1, String text2, String url, Job job) {
        this.text1 = text1;
        this.text2 = text2;
        this.url = url;
        this.job = job;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getImageUrl() {
        return url;
    }

    public void setImageUrl(String url) {
        this.url = url;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

}