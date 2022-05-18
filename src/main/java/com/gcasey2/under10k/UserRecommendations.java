package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRecommendations {

    public static ArrayList<String> getTopArtistIds(TopArtistsModel topArtistsModel) {
        return topArtistsModel.getItems().stream()
                .map(ArtistModel::getId)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public static ArrayList<String> getTopTrackIds(TopTracksModel topTracksModel) {
        return topTracksModel.getItems().stream()
                .map(TrackModel::getId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<String> getTopGenres(TopArtistsModel topArtistsModel) {
        return topArtistsModel.getItems().stream()
                .flatMap(artist -> artist.getGenres().stream())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
