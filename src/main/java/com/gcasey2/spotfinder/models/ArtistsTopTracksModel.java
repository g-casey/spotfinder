package com.gcasey2.spotfinder.models;

import java.util.List;

public class ArtistsTopTracksModel {
    private List<TrackModel> tracks;

    public ArtistsTopTracksModel(){}

    public List<TrackModel> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackModel> tracks) {
        this.tracks = tracks;
    }

}
