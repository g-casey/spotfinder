package com.gcasey2.spotfinder.pages.discover;

import com.gcasey2.spotfinder.data.song.SongRepository;
import com.gcasey2.spotfinder.data.user.security.ModifiedUserDetails;
import com.gcasey2.spotfinder.data.song.Song;
import com.gcasey2.spotfinder.misc.AuthType;
import com.gcasey2.spotfinder.misc.PopularityLevels;
import com.gcasey2.spotfinder.misc.SpotifyRequestService;
import com.gcasey2.spotfinder.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"authentication", "trackList", "topArtists", "viewedTracks"})
@RequestMapping("/discover")
public class DiscoverController {


    private final DiscoverService discoverService;
    private final SpotifyRequestService requestService;
    private final SongRepository songRepository;

    @Autowired
    public DiscoverController(DiscoverService discoverService, SpotifyRequestService requestService, SongRepository songRepository) {
        this.discoverService = discoverService;
        this.requestService = requestService;
        this.songRepository = songRepository;
    }

    private void saveSongs(List<TrackModel> trackList){
        for(TrackModel track : trackList){
            Song song = new Song();
            song.setName(track.getName());
            song.setArtist(track.getArtist());
            song.setId(track.getId());

            try {
                songRepository.save(song);
            }catch (DataIntegrityViolationException e){

            }
        }
    }


    private void initialFetch(AuthResponseModel authentication, Model model, String genre, PopularityLevels popularity){
        List<TrackModel> trackData = new ArrayList<>();

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = requestService.getUsersTopArtists(authentication);
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, popularity);
            model.addAttribute("topArtists", topArtistsModel);
        }else{
            List<TrackModel> tracks = new ArrayList<>();
            int tryCount = 0;
            while((tracks.size() < 4 || trackData.size() < 4) && tryCount < 5){
                 tracks = discoverService.getSongRecommendationsFromGenre(authentication, genre, PopularityLevels.ALL);
                 trackData = discoverService.fetchRecommendationsClient(authentication, genre, tracks, popularity);
                 tryCount ++;
            }
        }

        saveSongs(trackData);
        model.addAttribute("genres", requestService.getAllGenres(authentication).getGenres());
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedPopularity", popularity.toString().toLowerCase());
        model.addAttribute("trackList", trackData);
        model.addAttribute("viewedTracks", new HashSet<TrackModel>(trackData));

        if(trackData.size() == 0){
            model.addAttribute("noSongsError", true);
            System.out.println("true");
        }

    }

    private void returningFetch(AuthResponseModel authentication, Model model, String genre, PopularityLevels popularity){
        List<TrackModel> trackData;
        List<TrackModel> oldTrackData = (List<TrackModel>) model.getAttribute("trackList");
        HashSet<TrackModel> viewedTracks = (HashSet<TrackModel>) model.getAttribute("viewedTracks");

        if(authentication.getAuthType() == AuthType.OAUTH){
            TopArtistsModel topArtistsModel = (TopArtistsModel) model.getAttribute("topArtists");
            trackData = discoverService.fetchRecommendationsOAUTH(authentication, topArtistsModel, popularity);
        }else{
            List<TrackModel> newTrackData = new ArrayList<>();
            int tryCount = 0;
            while(newTrackData.size() < 4 && tryCount < 5){
                if(new Random().nextInt(1, 10) > 5){
                    oldTrackData = discoverService.getSongRecommendationsFromGenre(authentication, genre, PopularityLevels.ALL);
                }
                newTrackData = discoverService.fetchRecommendationsClient(authentication, genre, oldTrackData, popularity);
                tryCount ++;
            }
            trackData = newTrackData;
        }

        trackData = trackData.stream().filter(track -> !viewedTracks.contains(track)).collect(Collectors.toList());
        viewedTracks.addAll(trackData);

        saveSongs(trackData);
        model.addAttribute("trackList", trackData);
        model.addAttribute("viewedTracks", viewedTracks);
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
            @RequestParam Optional<Boolean> ret,
            CsrfToken token) {

        AuthResponseModel authentication = (AuthResponseModel) model.getAttribute("authentication");

        Authentication springAuth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = springAuth.isAuthenticated() && !springAuth.getPrincipal().equals("anonymousUser");

        if(authenticated){
            UserDetails userDetails = (ModifiedUserDetails) springAuth.getPrincipal();
            model.addAttribute("user", userDetails);
        }

        model.addAttribute("loggedIn", authenticated);
        model.addAttribute("token", token);

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
            return "redirect:/login/spotify/client";
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
