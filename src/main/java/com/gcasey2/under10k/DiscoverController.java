package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes({"authentication", "trackList", "topArtists"})
@RequestMapping("/discover")
public class DiscoverController {


    private final DiscoverService discoverService;
    private final SpotifyRequestService requestService;

    @Autowired
    public DiscoverController(DiscoverService discoverService, SpotifyRequestService requestService) {
        this.discoverService = discoverService;
        this.requestService = requestService;
    }

    private void initialFetch(AuthResponseModel authentication, Model model, String genre, PopularityLevels popularity){
        List<TrackModel> trackData;

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = requestService.getUsersTopArtists(authentication);
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, popularity);
            model.addAttribute("userName", requestService.getUserProfile(authentication).getDisplay_name());
            model.addAttribute("topArtists", topArtistsModel);
        }else{
            List<TrackModel> tracks = new ArrayList<>();
            while(tracks.size() < 4){
                 tracks = discoverService.getSongRecommendationsFromGenre(authentication, genre, PopularityLevels.ALL);
            }
            trackData = discoverService.fetchRecommendationsClient(authentication, genre, tracks, popularity);
        }

        model.addAttribute("genres", requestService.getAllGenres(authentication).getGenres());
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedPopularity", popularity.toString().toLowerCase());
        model.addAttribute("trackList", trackData);
    }

    private void returningFetch(AuthResponseModel authentication, Model model, String genre, PopularityLevels popularity){
        List<TrackModel> trackData;
        List<TrackModel> oldTrackData = (List<TrackModel>) model.getAttribute("trackList");

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = (TopArtistsModel) model.getAttribute("topArtists");
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, popularity);
        }else{
            trackData = discoverService.fetchRecommendationsClient(authentication, genre, oldTrackData, popularity);
        }

        model.addAttribute("trackList", trackData);
    }

    private PopularityLevels handlePopularity(String popularity){
        PopularityLevels popularityLevels = PopularityLevels.ALL;
        switch(popularity){
            case "high":
                popularityLevels = PopularityLevels.HIGH;
                break;
            case "medium":
                popularityLevels = PopularityLevels.MEDIUM;
                break;
            case "low":
                popularityLevels = PopularityLevels.LOW;
                break;
        }
        return popularityLevels;
    }

    @GetMapping
    public String getDiscover(
            Model model,
            @RequestParam Optional<String> genre,
            @RequestParam Optional<String> popularity,
            @RequestParam Optional<Boolean> ref,
            @RequestParam Optional<Boolean> ret) {

        AuthResponseModel authentication = (AuthResponseModel) model.getAttribute("authentication");


        String selectedGenre = "pop";
        if(genre.isPresent()){
            selectedGenre = genre.get();
        }

        PopularityLevels popularityLevel = PopularityLevels.ALL;
        if(popularity.isPresent()){
            popularityLevel = handlePopularity(popularity.get());
        }


        if (authentication == null) {
            //redirect to get client auth token
            return "redirect:/login/client";
        }

        if(ret.isEmpty() || !ret.get()) {
            initialFetch(authentication, model, selectedGenre, popularityLevel);

            if(ref.isPresent() && ref.get()){
                return "carousel";
            }

            return "discover";
        }
        returningFetch(authentication, model, selectedGenre, popularityLevel);
        return "carousel";
    }
}
