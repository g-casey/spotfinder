package com.gcasey2.spotfinder.pages.forgot_password;

import com.gcasey2.spotfinder.data.user.User;
import com.gcasey2.spotfinder.data.user.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;


@Controller
@RequestMapping("/forgot_password")
public class ForgotPasswordController {
    private final JavaMailSender mailSender;
    private final UserService userService;

    @Autowired
    public ForgotPasswordController(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    @GetMapping
    public String showForgotPasswordForm(CsrfToken token, Model model) {
        model.addAttribute("token", token);
        return "forgot_password";
    }

    @PostMapping
    public String processForgotPassword(@RequestParam String email, Model model) {
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            final String resetUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                    "/forgot_password/reset?token=" + token;
            sendEmail(email, resetUrl);
        }catch (UsernameNotFoundException | MessagingException | UnsupportedEncodingException e){
            return "redirect:/forgot_password";
        }
        model.addAttribute("mailSent", true);
        return "forgot_password";
    }

    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model, CsrfToken csrfToken) {
        User user = userService.getUserByResetToken(token);
        model.addAttribute("resetToken", token);
        model.addAttribute("token", csrfToken);

        return "reset_password";

    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        User user = userService.getUserByResetToken(token.strip());

        if(user != null){
            userService.updatePassword(user, password);;
        }
        return "reset-success";
    }

    public void sendEmail(String emailAddress, String url ) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("spotfinderservice@gmail.com", "spotfinder service");
        helper.setTo(emailAddress);

        String subject = "Password Reset Link";
        String content = "<p> Click the link below to reset your password: </p>"
                + "<a href='" + url + "'> Reset Password </a>";

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}
