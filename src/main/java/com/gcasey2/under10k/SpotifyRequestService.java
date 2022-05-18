package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import java.net.URI;

@Service
public class SpotifyRequestService {

    private final WebClient webClient;

    @Autowired
    public SpotifyRequestService(WebClient webClient ) {
        this.webClient = webClient;
    }

    private <T> T sendRequest(RequestHeadersSpec requestHeadersSpec, AuthResponseModel authentication, Class<T> clazz){
         return requestHeadersSpec.header("Authorization", authentication.getAuthHeader())
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }

    private <T> T makeRequest(String url, AuthResponseModel authentication, Class<T> clazz){
        return sendRequest(webClient.get().uri(url), authentication, clazz);
    }


    private <T> T makeRequest(URI uri, AuthResponseModel authentication, Class<T> clazz){
        return sendRequest(webClient.get().uri(uri), authentication, clazz);
    }

    public TopArtistsModel getUsersTopArtists(AuthResponseModel authentication) {
        return makeRequest("/me/top/artists?limit=50", authentication, TopArtistsModel.class);
    }

    public RecommendationGenresModel getAllGenres(AuthResponseModel authentication){
        return makeRequest("/recommendations/available-genre-seeds", authentication, RecommendationGenresModel.class);
    }

    public RecommendationModel getRecommendations(AuthResponseModel authentication, URI recommendationURI){
        return makeRequest(recommendationURI, authentication, RecommendationModel.class);
    }

    public ArtistsTopTracksModel getArtistsTopTracks(AuthResponseModel authentication, String id) {
        return makeRequest("/artists/" + id + "/top-tracks?market=US", authentication, ArtistsTopTracksModel.class);
    }

    public TrackModel getTrack(AuthResponseModel authentication, String trackId){
       return makeRequest("/tracks/" + trackId, authentication, TrackModel.class);
    }

    public ProfileModel getUserProfile(AuthResponseModel authentication){
        return makeRequest("/me", authentication, ProfileModel.class);
    }



}
