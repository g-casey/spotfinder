package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private void initialFetch(AuthResponseModel authentication, Model model, String genre){
        List<TrackModel> trackData;

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = requestService.getUsersTopArtists(authentication);
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel);
            model.addAttribute("userName", requestService.getUserProfile(authentication).getDisplay_name());
            model.addAttribute("topArtists", topArtistsModel);
        }else{
            trackData = discoverService.fetchRecommendationsClient(authentication, genre);
        }

        model.addAttribute("genres", requestService.getAllGenres(authentication).getGenres());
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("trackList", trackData);
    }

    private void returningFetch(AuthResponseModel authentication, Model model, String genre){
        List<TrackModel> trackData;

        List<TrackModel> oldTrackData = (List<TrackModel>) model.getAttribute("trackList");

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = (TopArtistsModel) model.getAttribute("topArtists");
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel);
        }else{
            trackData = discoverService.fetchRecommendationsClient(authentication, genre, oldTrackData);
        }

        model.addAttribute("trackList", trackData);
    }

    @GetMapping
    public String getDiscover(
            Model model, @RequestParam Optional<String> genre,
            @RequestParam Optional<Boolean> ref,
            @RequestParam Optional<Boolean> ret) {

        AuthResponseModel authentication = (AuthResponseModel) model.getAttribute("authentication");

        String selectedGenre = "pop";
        if(genre.isPresent()){
            selectedGenre = genre.get();
        }

        if (authentication == null) {
            //redirect to get client auth token
            return "redirect:/login/client";
        }

        if(ret.isEmpty() || !ret.get()) {
            initialFetch(authentication, model, selectedGenre);

            if(ref.isPresent() && ref.get()){
                return "carousel";
            }

            return "discover";
        }
        returningFetch(authentication, model, selectedGenre);
        return "carousel";
    }
}
