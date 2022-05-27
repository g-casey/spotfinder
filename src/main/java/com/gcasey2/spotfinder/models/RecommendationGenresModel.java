package com.gcasey2.spotfinder.models;

import java.util.List;

public class RecommendationGenresModel {
    private List<String> genres;

    public RecommendationGenresModel(){}

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
