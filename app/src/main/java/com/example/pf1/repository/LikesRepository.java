package com.example.pf1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pf1.entity.Likes;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByLikedUser_UserIdAndLikerSessionId(Long likedUserId, String likerSessionId);
}