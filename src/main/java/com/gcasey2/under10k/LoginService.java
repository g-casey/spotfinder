package com.gcasey2.under10k;

import com.gcasey2.under10k.models.AuthResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
public class LoginService {
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String REDIRECT_URL;
    private String scopes;
    private String authState;

    @Autowired
    public LoginService(@Value("${client.id}") String CLIENT_ID,
                        @Value("${client.secret}") String CLIENT_SECRET,
                        @Value("${redirect.url}") String REDIRECT_URL,
                        @Value("${scopes}") String scopes) {
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.REDIRECT_URL = REDIRECT_URL;
        this.scopes = scopes;
    }


    private String generateRandomString(int length) {
        final String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return new SecureRandom()
                .ints(length, 0, charset.length())
                .mapToObj(charset::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public AuthResponseModel sendSpotifyOAuthRequest(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("redirect_uri", REDIRECT_URL);
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);

        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(AuthResponseModel.class)
                .block();
    }

    public AuthResponseModel sendSpotifyClientAuthRequest(){
        WebClient webClient = WebClient.create();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);

        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(AuthResponseModel.class)
                .block();
    }

    public String getAuthRedirectUrl() {
        authState = generateRandomString(16);

        final String url = "https://accounts.spotify.com/authorize" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + UriUtils.encode(REDIRECT_URL, "UTF-8") +
                "&show_dialog=true" +
                "&scope=" + scopes;

        return url;
    }


}
