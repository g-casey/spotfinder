package com.gcasey2.spotfinder.data.song;


import javax.persistence.*;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @Column(unique = true)
    private String id;

    public Song(){

    }

    public Song(String id) {
        this.id = id;
    }

    private String name;

    private String artist;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
