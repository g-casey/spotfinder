package com.gcasey2.spotfinder.pages.like;

import com.gcasey2.spotfinder.data.likes.UserLikeRepository;
import com.gcasey2.spotfinder.data.song.SongRepository;
import com.gcasey2.spotfinder.data.user.security.ModifiedUserDetails;
import com.gcasey2.spotfinder.data.song.Song;
import com.gcasey2.spotfinder.data.user.User;
import com.gcasey2.spotfinder.data.likes.UserLikeId;
import com.gcasey2.spotfinder.data.likes.UserLikes;
import com.gcasey2.spotfinder.misc.SpotifyRequestService;
import com.gcasey2.spotfinder.models.AuthResponseModel;
import com.gcasey2.spotfinder.models.TrackModel;
import com.gcasey2.spotfinder.pages.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/likes")
public class LikeController {

    private final UserLikeRepository userLikeRepository;
    private final SongRepository songRepository;
    private final SpotifyRequestService requestService;
    private final LoginService loginService;

    @Autowired
    public LikeController(UserLikeRepository userLikeRepository, SongRepository songRepository, SpotifyRequestService requestService, LoginService loginService) {
        this.userLikeRepository = userLikeRepository;
        this.songRepository = songRepository;
        this.requestService = requestService;
        this.loginService = loginService;
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.OK)
    public void likeSong(@RequestBody String songId, Authentication authentication){

        ModifiedUserDetails userDetails = (ModifiedUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        Optional<Song> song = songRepository.findById(songId);

        if(song.isPresent()){
            UserLikes userLikes = new UserLikes(user, song.get());
            if(userLikeRepository.existsById(new UserLikeId(user.getId(), songId))){
                userLikeRepository.delete(userLikes);
            }else {
                userLikeRepository.save(userLikes);
            }
        }
    }

    @GetMapping
    public String getLikesPage(Authentication authentication, Model model, CsrfToken token){
        ModifiedUserDetails userDetails = (ModifiedUserDetails) authentication.getPrincipal();
        List<Song> likedSongs = userLikeRepository.findSongsByUserId(userDetails.getUser().getId());

        AuthResponseModel auth = loginService.sendSpotifyClientAuthRequest();
        List<TrackModel> trackList = likedSongs.stream().map(song -> requestService.getTrack(auth, song.getId())).collect(Collectors.toList());
        trackList.forEach(track -> track.setImageUrl(track.getAlbum().getImageUrl()));

        model.addAttribute("trackList", trackList);
        model.addAttribute("likes", true);
        model.addAttribute("loggedIn", true);
        model.addAttribute("token", token);

        return "discover";
    }

}
