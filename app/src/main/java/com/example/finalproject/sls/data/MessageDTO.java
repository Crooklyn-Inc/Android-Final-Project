package com.example.finalproject.sls.data;

public class MessageDTO {
    private Long   id;
    private String band;
    private String song;
    private String lyrics;

    public MessageDTO() { }

    public MessageDTO(String band, String song, String lyrics) {
        this(null, band, song, lyrics);
    }

    public MessageDTO(Long id, String band, String song, String lyrics) {
        this.id     = id;
        this.band   = band;
        this.song   = song;
        this.lyrics = lyrics;
    }

    public Long getId()              { return id; }

    public void setId(Long id)       { this.id = id; }

    public String getBand()          { return band; }

    public void setBand(String band) { this.band = band; }

    public String getSong()          { return song; }

    public void setSong(String song) { this.song = song; }

    public String getLyrics()            { return lyrics; }

    public void setLyrics(String lyrics) { this.lyrics = lyrics; }
}
