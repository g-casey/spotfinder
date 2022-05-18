package com.gcasey2.under10k;

import com.gcasey2.under10k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Track;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"index","trackList", "topArtists"})
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
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, 0);
            model.addAttribute("userName", requestService.getUserProfile(authentication).getDisplay_name());
            model.addAttribute("topArtists", topArtistsModel);
        }else{
            trackData = discoverService.fetchRecommendationsClient(authentication, genre);
        }

        model.addAttribute("genres", requestService.getAllGenres(authentication).getGenres());
        model.addAttribute("index", trackData.size() - 1);
        model.addAttribute("trackList", trackData);
    }

    private void returningFetch(AuthResponseModel authentication, Model model, String genre){
        List<TrackModel> trackData;

        int index = (int) model.getAttribute("index");
        List<TrackModel> oldTrackData = (List<TrackModel>) model.getAttribute("trackList");

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = (TopArtistsModel) model.getAttribute("topArtists");
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, index);
        }else{
            trackData = discoverService.fetchRecommendationsClient(authentication, genre, oldTrackData, index);
        }
        index += trackData.size();

        model.addAttribute("index", index);
        model.addAttribute("trackList", trackData);
    }

    @GetMapping
    public String getDiscover(
            @ModelAttribute("authentication") AuthResponseModel authentication,
            Model model, @RequestParam Optional<String> genre) {


        String selectedGenre = "pop";

        if(genre.isPresent()){
            selectedGenre = genre.get();
        }

        if (authentication.getAccess_token() == null) {
            //redirect to get client auth token
            return "redirect:/login/client";
        }

        if (!model.containsAttribute("trackList") || ((List<TrackModel>) model.getAttribute("trackList")).size() == 0) {
            initialFetch(authentication, model, selectedGenre);
            return "discover";
        }
        returningFetch(authentication, model, selectedGenre);
        return "carousel";
    }
}
