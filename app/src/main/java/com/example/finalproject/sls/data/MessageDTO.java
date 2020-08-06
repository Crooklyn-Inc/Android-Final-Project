/**
 * This is the copyrighted content for course
 * of mobile programming at Algonquin College
 *
 * @author Olga Zimina
 * @version 1.0.0
 * @created Jul 25, 2020
 */

package com.example.finalproject.sls.data;

/**
 * DTO object describing message structure to transfer within Song Lyrics App
 */
public class MessageDTO {
    private Long   id;
    private String band;
    private String song;
    private String lyrics;

    /**
     * Default constructor
     */
    public MessageDTO() { }

    /**
     * Parameterized constructor
     *
     * @param band   Band Name
     * @param song   Song Name
     * @param lyrics Lyrics as a text
     */
    public MessageDTO(String band, String song, String lyrics) {
        this(null, band, song, lyrics);
    }

    /**
     * Parameterized constructor
     *
     * @param id     ID of the record
     * @param band   Band Name
     * @param song   Song Name
     * @param lyrics Lyrics as a text
     */
    public MessageDTO(Long id, String band, String song, String lyrics) {
        this.id     = id;
        this.band   = band;
        this.song   = song;
        this.lyrics = lyrics;
    }

    /**
     * Getter to obtain ID of the current record
     *
     * @return ID of the current record
     */
    public Long getId() { return id; }

    /**
     * Set ID of the current record
     *
     * @param id ID of the current record
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Getter to obtain Band Name of the current record
     *
     * @return Band Name of the current record
     */
    public String getBand() { return band; }

    /**
     * Set Band Name of the current record
     *
     * @param band Band Name of the current record
     */
    public void setBand(String band) { this.band = band; }

    /**
     * Getter to obtain Song Name of the current record
     *
     * @return Song Name of the current record
     */
    public String getSong() { return song; }

    /**
     * Set Band Song of the current record
     *
     * @param song Song Name  of the current record
     */
    public void setSong(String song) { this.song = song; }

    /**
     * Getter to obtain Lyrics for the current record
     *
     * @return Lyrics for the current record
     */
    public String getLyrics() { return lyrics; }

    /**
     * Set Lyrics for current record
     *
     * @param lyrics Lyrics for current record
     */
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }
}
