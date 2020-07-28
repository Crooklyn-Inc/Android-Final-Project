package com.example.finalproject.soccerMatch;

import java.net.URL;

public class Match {
    private String title;
    private long id;
    private String thumbnail;
    private String date;
    private String competitionName;
    private String videoUrl;
    private String team1;
    private String team2;


    public Match() {

    }

    public Match( long id, String title, String thumbnail, String date, String competitionName, String videoUrl, String team1, String team2) {
        this.title = title;
        this.id = id;
        this.thumbnail = thumbnail;
        this.date = date;
        this.competitionName = competitionName;
        this.videoUrl = videoUrl;
        this.team1 = team1;
        this.team2 = team2;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
