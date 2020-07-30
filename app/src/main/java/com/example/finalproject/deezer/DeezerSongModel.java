package com.example.finalproject.deezer;

public class DeezerSongModel {

    private String title;
    private String album_name;
    private String duration;
    private String album_image;
    protected long id;

    public  DeezerSongModel(){}

    public  DeezerSongModel (String title, String duration, String album_name, String album_image){
        this.title = title;
        this.duration= duration;
        this.album_name = album_name;
        this.id = id;
        this.album_image = album_image;
    }

    public  DeezerSongModel (String title, String duration, String album_name, long id, String album_image) {
        this.title = title;
        this.duration = duration;
        this.album_name = album_name;
        this.id = id;
        this.album_image = album_image;
    }

    public void update(String t, String d, String a)
    {
        title = t;
       duration = d;
       album_name =a;
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

    public String getAlbum_Image() {
        return album_image;
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

    public void setAlbum_Image(String album_image) {
        this.album_image = album_image;
    }
}
