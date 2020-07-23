package com.example.finalproject;

public class DeezerSongModel {

    private String title;
    private String album_name;
    private String duration;
    protected long id;

    public  DeezerSongModel (String title, String duration, String album_name){
        this.title = title;
        this.duration= duration;
        this.album_name = album_name;
        this.id = id;
    }

    public DeezerSongModel() {
    }


    public String getTitle() {
        return title;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getDuration() {
        return duration.toString();
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setId(long id) {
        this.id = id;
    }
}
