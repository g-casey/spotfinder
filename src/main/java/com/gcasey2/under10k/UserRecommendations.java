package com.gcasey2.under10k;

import com.gcasey2.under10k.models.ArtistModel;
import com.gcasey2.under10k.models.TopArtistsModel;
import com.gcasey2.under10k.models.TopTracksModel;
import com.gcasey2.under10k.models.TrackModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
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
