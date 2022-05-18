package com.gcasey2.under10k.models;

import java.util.List;
import java.util.Objects;

public class TrackModel {
    private String id;
    private String name;
    private List<ArtistModel> artists;
    private String href;
    private AlbumModel album;
    private List<String> available_markets;
    private boolean is_playable;
    private int listIndex;

    private String preview_url;

    private String imageUrl;

    public TrackModel(){}

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

    public List<ArtistModel> getArtists() {
        return artists;
    }

    public String getArtist(){
        return artists.get(0).getName();
    }

    public void setArtists(List<ArtistModel> artists) {
        this.artists = artists;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public AlbumModel getAlbum() {
        return album;
    }

    public void setAlbum(AlbumModel album) {
        this.album = album;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public boolean isIs_playable() {
        return is_playable;
    }

    public void setIs_playable(boolean is_playable) {
        this.is_playable = is_playable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    @Override
    public String toString() {
        return "TrackModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackModel that = (TrackModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
