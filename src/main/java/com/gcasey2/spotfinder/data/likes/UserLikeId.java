package com.gcasey2.spotfinder.data.likes;

import java.io.Serializable;

public class UserLikeId implements Serializable {
    private Long user;
    private String song;


    public UserLikeId(){}

    public UserLikeId(Long user, String song) {
        this.user = user;
        this.song = song;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
