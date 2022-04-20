package com.gcasey2.under10k;

import com.gcasey2.under10k.models.AuthResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/discover")
public class DiscoverController {


    private DiscoverService discoverService;

    @Autowired
    public DiscoverController(DiscoverService discoverService) {
        this.discoverService = discoverService;
    }

    @GetMapping
    public String getDiscover(@ModelAttribute("authentication") AuthResponseModel authentication, Model model) {

        if (authentication.getAccess_token() == null) {
            return "error";
        }

        String data = discoverService.getSongRecommendations(authentication).toString();
        model.addAttribute("data", data);

        return "discover";
    }
}
