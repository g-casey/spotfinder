package com.gcasey2.spotfinder.models;

import java.util.List;

public class AlbumModel {
    private String name;
    private List<ArtModel> images;

    public AlbumModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArtModel> getImages() {
        return images;
    }

    public String getImageUrl() {
        return images.get(0).getUrl();
    }

    public void setImages(List<ArtModel> images) {
        this.images = images;
    }

}
