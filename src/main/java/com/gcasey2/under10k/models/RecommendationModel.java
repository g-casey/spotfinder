package com.gcasey2.under10k.models;

import java.util.List;

public class RecommendationModel {
    private List<TrackModel> tracks;

    public RecommendationModel(){};

    public List<TrackModel> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackModel> tracks) {
        this.tracks = tracks;
    }
}
