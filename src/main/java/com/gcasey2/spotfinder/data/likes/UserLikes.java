package com.gcasey2.spotfinder.data.likes;

import com.gcasey2.spotfinder.data.song.Song;
import com.gcasey2.spotfinder.data.user.User;

import javax.persistence.*;

@Entity
@Table(name = "user_likes_songs")
@IdClass(UserLikeId.class)
public class UserLikes {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "song_id", referencedColumnName = "id")
    private Song song;

    public UserLikes(){}

    public UserLikes(User user, Song song){
        this.user = user;
        this.song = song;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
