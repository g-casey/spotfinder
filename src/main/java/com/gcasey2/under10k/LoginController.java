package com.gcasey2.under10k;

import com.gcasey2.under10k.models.AuthResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @Autowired
    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }


    @GetMapping
    public RedirectView authorizeSpotify() {
        final String url = loginService.getAuthRedirectUrl();
        return new RedirectView(url);
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

        AuthResponseModel response = loginService.sendSpotifyAuthRequest(code.get());
        redirectAttributes.addFlashAttribute("authentication", response);
        return new RedirectView("/discover");
    }
}
