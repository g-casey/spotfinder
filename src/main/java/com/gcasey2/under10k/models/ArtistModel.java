package com.gcasey2.under10k.models;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class ArtistModel {
    private String id;
    private String name;

    private String href;

    private int popularity;
    private List<String> genres;

    public ArtistModel(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ArtistModel getArtistModel(AuthResponseModel authentication) {
        return WebClient.create()
                .get()
                .uri(href)
                .header("Authorization", authentication.getAuthHeader())
                .retrieve()
                .bodyToMono(ArtistModel.class)
                .block();
    }

    @Override
    public String toString() {
        return "ArtistModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                ", genres=" + genres +
                '}';
    }
}
