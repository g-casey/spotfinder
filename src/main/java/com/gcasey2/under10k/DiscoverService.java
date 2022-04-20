package com.gcasey2.under10k;

import com.gcasey2.under10k.models.AuthResponseModel;
import com.gcasey2.under10k.models.TopArtistsModel;
import com.gcasey2.under10k.models.TopTracksModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscoverService {
    WebClient webClient;

    @Autowired
    public DiscoverService(WebClient webClient) {
        this.webClient = webClient;
    }

    private <T> T getUsersTop(AuthResponseModel authentication, Class<T> modelClass) {
        String type = modelClass == TopArtistsModel.class ? "artists" : "tracks";
        T response = webClient.get()
                .uri("/me/top/" + type + "?limit=50")
                .header("Authorization", authentication.getAuthHeader())
                .retrieve()
                .bodyToMono(modelClass)
                .block();
        return response;
    }

    public TopTracksModel getUsersTopTracks(AuthResponseModel authentication) {
        return getUsersTop(authentication, TopTracksModel.class);
    }

    public TopArtistsModel getUsersTopArtists(AuthResponseModel authentication) {
        return getUsersTop(authentication, TopArtistsModel.class);
    }

    private String buildRecommendationUrl(int limit, String market, List<String> seed_artists, List<String> seed_genres,
                                          List<String> seed_tracks, int min_popularity, int max_popularity) {
        return "/recommendations" +
                "?limit=" + limit +
                "&market=" + market +
                "&seed_artists=" + seed_artists.stream().collect(Collectors.joining(",")) +
                "&seed_genres=" + seed_genres.stream().collect(Collectors.joining(",")) +
                "&seed_tracks=" + seed_tracks.stream().collect(Collectors.joining(",")) +
                "&min_popularity=" + min_popularity +
                "&max_popularity=" + max_popularity;
    }

    public String getSongRecommendations(AuthResponseModel authentication) {

        TopArtistsModel topArtistsModel = getUsersTopArtists(authentication);
        TopTracksModel topTracksModel = getUsersTopTracks(authentication);



        /*

        ArrayList<String> tracks = new ArrayList<>();
        List<TrackModel> topTracks = topTracksModel.getItems().subList(0,3);
        tracks.addAll(topTracks.stream().map(TrackModel::getId).collect(Collectors.toList()));

        ArrayList<String> artists = new ArrayList<>();
        artists.add(topTracks.get(0));


         */

        List<String> artists = UserRecommendations.getTopArtistIds(topArtistsModel).subList(0, 2);
        List<String> genres = UserRecommendations.getTopGenres(topArtistsModel).subList(0, 1);
        List<String> tracks = UserRecommendations.getTopTrackIds(topTracksModel).subList(0, 2);


        final String url = buildRecommendationUrl(10, "US", artists, genres, tracks, 1,40);

        return webClient.get()
                .uri(url)
                .header("Authorization", authentication.getAuthHeader())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
