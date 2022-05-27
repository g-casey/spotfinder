package com.gcasey2.spotfinder.pages.login;

import com.gcasey2.spotfinder.misc.AuthType;
import com.gcasey2.spotfinder.misc.SpotifyRequestService;
import com.gcasey2.spotfinder.models.AuthResponseModel;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {


    private LoginService loginService;
    private SpotifyRequestService requestService;

    public LoginController(LoginService loginService, SpotifyRequestService requestService) {
        this.loginService = loginService;
        this.requestService = requestService;
    }

    @GetMapping
    public String getLoginForm(Model model, CsrfToken token){
        model.addAttribute("token", token);
        model.addAttribute("login", true);
        return "login";
    }

    @GetMapping("/spotify/oauth")
    public RedirectView authorizeSpotify() {
        final String url = loginService.getAuthRedirectUrl();
        return new RedirectView(url);
    }

    @GetMapping("/spotify/client")
    public RedirectView getSpotifyClientAuthCode(RedirectAttributes redirectAttributes){
        AuthResponseModel response = loginService.sendSpotifyClientAuthRequest();
        response.setAuthType(AuthType.CLIENT);
        redirectAttributes.addFlashAttribute("authentication", response);
        return new RedirectView("/discover");
    }

    @GetMapping("/authenticated")
    public RedirectView getSpotifyAuthCode(@RequestParam Optional<String> code,
                                           //@RequestParam String state,      For some reason redirect url does not contain state param
                                           @RequestParam Optional<String> error,
                                           RedirectAttributes redirectAttributes) {

        // check if state == authState
        if (error.isPresent()) {
            return new RedirectView("auth-error");
        }

        AuthResponseModel response = loginService.sendSpotifyOAuthRequest(code.get());



        response.setAuthType(AuthType.OAUTH);
        redirectAttributes.addFlashAttribute("authentication", response);
        return new RedirectView("/discover");
    }
}
