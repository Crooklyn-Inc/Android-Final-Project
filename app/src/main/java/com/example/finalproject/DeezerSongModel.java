package com.example.finalproject;

public class DeezerSongModel {
    private String titile;
    private String album_name;
    private int duration;
    protected long id;

    public  DeezerSongModel (String title, int duration, String album_name, long id){
        this.titile = title;
        this.duration= duration;
        this.album_name = album_name;
        this.id = id;

    }

    public String getTitile() {
        return titile;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public int getDuration() {
        return duration;
    }

    public long getId() {
        return id;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setId(long id) {
        this.id = id;
    }
}
