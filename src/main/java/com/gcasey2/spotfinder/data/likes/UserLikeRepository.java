package com.gcasey2.spotfinder.data.likes;

import com.gcasey2.spotfinder.data.song.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLikeRepository extends JpaRepository<UserLikes, UserLikeId> {

    @Query("SELECT l.song FROM UserLikes l JOIN l.user u WHERE u.id = ?1")
    public List<Song> findSongsByUserId(Long id);

    @Query("SELECT COUNT(l.song) FROM UserLikes l WHERE l.song.id = ?1 AND l.user.id = ?2")
    public int currentSongLiked(String songId, Long userId);

}
