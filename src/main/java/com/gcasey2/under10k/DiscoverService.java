package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public List<TrackModel> getSongRecommendationsFromGenre(AuthResponseModel authentication, String genre){
        List<String> genres = List.of(genre);
        URI uri = URI.create(buildRecommendationUrl(20,"US",new ArrayList<String>(), genres, new ArrayList<String>(), 0 ,100));

        return requestService.getRecommendations(authentication, uri)
                .getTracks().stream()
                .filter(track -> track.getPreview_url() != null)
                .collect(Collectors.toList());
    }

    public RecommendationModel getRecommendationsFromArtist(ArtistModel artist, AuthResponseModel authentication, boolean randomRange) {
        int genreCount = artist.getGenres().size() < 2 ? 1 : 2;

        List<String> artists = getRecommendationArtists(artist);
        List<String> genres = getRecommendationGenres(artist, genreCount);
        List<String> tracks = getRecommendationTracks(artist, 4 - genreCount, authentication);

        int min = randomRange ? random.nextInt(40, 90) : 0;
        int max = randomRange ? min + 10  : 100;

        URI uri = URI.create(buildRecommendationUrl(10, "US", artists, genres, tracks, min, max));
        return requestService.getRecommendations(authentication, uri);
    }

    public List<TrackModel> getSongRecommendations(AuthResponseModel authentication, TopArtistsModel topArtistsModel) {
        int min = random.nextInt(0, 20);

        List<ArtistModel> artists = topArtistsModel.getItems().subList(min, min + 3);

        List<TrackModel> recommendations = artists.stream()
                .filter(artist -> artist.getGenres().size() > 0) // remove artists with no genres
                .flatMap(artist -> getRecommendationsFromArtist(artist, authentication, true).getTracks().stream())
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

    public List<TrackModel> fetchRecommendationsOAUTH(AuthResponseModel authentication, TopArtistsModel topArtistsModel){
        List<TrackModel> trackData = getSongRecommendations(authentication, topArtistsModel);
        return fetchRecommendations(authentication, trackData);
    }

    public List<TrackModel> fetchRecommendationsClient(AuthResponseModel authentication, String genre){
        List<TrackModel> trackData = getSongRecommendationsFromGenre(authentication, genre);
        return fetchRecommendations(authentication, trackData);
    }

    public List<TrackModel> fetchRecommendationsClient(AuthResponseModel authentication, String genre, List<TrackModel> oldTrackData){
        ArtistModel artist = oldTrackData.get(0).getArtists().get(0);
        artist.setGenres(List.of(genre));

        List<TrackModel> trackData = getRecommendationsFromArtist(artist, authentication, false).getTracks().stream()
                .filter(track -> track.getPreview_url() != null)
                .collect(Collectors.toList());
        return fetchRecommendations(authentication, trackData);
    }


}
