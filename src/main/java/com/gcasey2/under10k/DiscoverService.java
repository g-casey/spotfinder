package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import com.mitchellbosecke.pebble.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DiscoverService {
    private final Random random;
    private final SpotifyRequestService requestService;

    @Autowired
    public DiscoverService(Random random, SpotifyRequestService requestService) {
        this.random = random;
        this.requestService = requestService;
    }

    private String buildRecommendationUrl(int limit, String market, List<String> seed_artists, List<String> seed_genres,
                                          List<String> seed_tracks, int min_popularity, int max_popularity) {
        String url = "https://api.spotify.com/v1/recommendations" +
                "?limit=" + limit +
                //"&market=" + market +
                "&seed_genres=" + URLEncoder.encode(String.join(",", seed_genres), StandardCharsets.UTF_8)+
                "&min_popularity=" + min_popularity +
                "&max_popularity=" + max_popularity;

        if(seed_artists.size() > 0 || seed_tracks.size() > 0) {
            url +=
            "&seed_artists=" + URLEncoder.encode(String.join(",", seed_artists), StandardCharsets.UTF_8) +
                    "&seed_tracks=" + URLEncoder.encode(String.join(",", seed_tracks), StandardCharsets.UTF_8);
        }

        return url;
    }

    private Pair<Integer, Integer> handlePopularityLevel(PopularityLevels popularity){
        Pair<Integer, Integer> popularityLevel = new Pair<>(0, 100);
        switch (popularity){
            case HIGH:
                popularityLevel = new Pair<>(66,100);
                break;
            case MEDIUM:
                popularityLevel = new Pair<>(33,66);
                break;
            case LOW:
                popularityLevel = new Pair<>(0,33);
                break;
        }
        return popularityLevel;
    }

    private List<String> getRecommendationArtists(ArtistModel artist){
       return List.of(artist.getId());
    }

    private List<String> getRecommendationGenres(ArtistModel artist, int genreCount){
        return artist.getGenres().subList(0, genreCount);
    }

    private List<String> getRecommendationTracks(ArtistModel artist, int limit,  AuthResponseModel authentication){
        ArtistsTopTracksModel artistsTopTracksModel = requestService.getArtistsTopTracks(authentication, artist.getId());
            List<String> tracks = artistsTopTracksModel.getTracks().stream()
                    .map(TrackModel::getId)
                    .collect(Collectors.toCollection(ArrayList::new));

            if(tracks.size() >= 3){
                return tracks.subList(0, limit);
            }

            return tracks;
    }

    public List<TrackModel> getSongRecommendationsFromGenre(AuthResponseModel authentication, String genre, PopularityLevels popularityLevels){
        List<String> genres = List.of(genre);

        Pair<Integer, Integer> popularity = handlePopularityLevel(popularityLevels);

        URI uri = URI.create(buildRecommendationUrl(20,"US",new ArrayList<String>(), genres, new ArrayList<String>(), popularity.getLeft() , popularity.getRight()));

        return requestService.getRecommendations(authentication, uri)
                .getTracks().stream()
                .filter(track -> track.getPreview_url() != null)
                .collect(Collectors.toList());
    }

    public RecommendationModel getRecommendationsFromArtist(ArtistModel artist, AuthResponseModel authentication, PopularityLevels popularityLevels) {
        int genreCount = artist.getGenres().size() < 2 ? 1 : 2;

        List<String> artists = getRecommendationArtists(artist);
        List<String> genres = getRecommendationGenres(artist, genreCount);
        List<String> tracks = getRecommendationTracks(artist, 4 - genreCount, authentication);

        Pair<Integer, Integer> popularity = handlePopularityLevel(popularityLevels);

        URI uri = URI.create(buildRecommendationUrl(10, "US", artists, genres, tracks, popularity.getLeft(), popularity.getRight()));
        return requestService.getRecommendations(authentication, uri);
    }

    public List<TrackModel> getSongRecommendations(AuthResponseModel authentication, TopArtistsModel topArtistsModel, PopularityLevels popularity) {
        int artistMin = random.nextInt(0, 20);
        List<ArtistModel> artists = topArtistsModel.getItems().subList(artistMin, artistMin + 3);

        List<TrackModel> recommendations = artists.stream()
                .filter(artist -> artist.getGenres().size() > 0) // remove artists with no genres
                .flatMap(artist -> getRecommendationsFromArtist(artist, authentication, popularity).getTracks().stream())
                .filter(track -> track.getPreview_url() != null)
                .collect(Collectors.toList());

        Collections.shuffle(recommendations);
        return recommendations;
    }


    public List<TrackModel> fetchRecommendations(AuthResponseModel authentication, List<TrackModel> trackData){
        trackData.forEach(track -> track.setImageUrl(
                requestService.getTrack(authentication, track.getId()).
                        getAlbum().getImageUrl()));

        return trackData;
    }

    public List<TrackModel> fetchRecommendationsOAUTH(AuthResponseModel authentication, TopArtistsModel topArtistsModel, PopularityLevels popularity){
        List<TrackModel> trackData = getSongRecommendations(authentication, topArtistsModel, popularity);
        return fetchRecommendations(authentication, trackData);
    }

    public List<TrackModel> fetchRecommendationsClient(AuthResponseModel authentication, String genre, PopularityLevels popularity){
        List<TrackModel> trackData = getSongRecommendationsFromGenre(authentication, genre, popularity);
        return fetchRecommendations(authentication, trackData);
    }

    public List<TrackModel> fetchRecommendationsClient(AuthResponseModel authentication, String genre, List<TrackModel> oldTrackData, PopularityLevels popularity){

        List<String> tracks = oldTrackData.subList(0,4).stream().map(TrackModel::getId).collect(Collectors.toList());

        Pair<Integer, Integer> popularityLevel = handlePopularityLevel(popularity);

        URI recommendationURI = URI.create(buildRecommendationUrl(10,"US", new ArrayList<String>(), List.of(genre), tracks, popularityLevel.getLeft(), popularityLevel.getRight()));
        List<TrackModel> trackData = requestService.getRecommendations(authentication, recommendationURI ).getTracks().stream()
                .filter(track -> track.getPreview_url() != null)
                .collect(Collectors.toList());
        return fetchRecommendations(authentication, trackData);
    }
}
