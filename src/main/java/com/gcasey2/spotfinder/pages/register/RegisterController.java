package com.gcasey2.spotfinder.pages.register;

import com.gcasey2.spotfinder.data.user.UserRepository;
import com.gcasey2.spotfinder.data.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public RegisterController(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getRegistrationForm(
            @RequestParam Optional<Boolean> error,
            Model model,
            CsrfToken token){
        if(error.isPresent()){
            model.addAttribute("error", true);
        }
        model.addAttribute("token", token);
        model.addAttribute("login", true);
        return "register";
    }

    @PostMapping
    public String registerUser(String email, String name, String password){
        User user = new User();

        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setSpotifyConnected(false);

        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            return "redirect:/register?error=true";
        }

        return "register-success";
    }
}
