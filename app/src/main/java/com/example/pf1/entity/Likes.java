package com.example.pf1.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "LIKES", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "LIKED_USER_ID", "LIKER_USER_ID" }),
        @UniqueConstraint(columnNames = { "LIKED_USER_ID", "LIKER_SESSION_ID" })
})
@Getter
@Setter
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "LIKED_USER_ID", nullable = false)
    private Accounts likedUser;

    @ManyToOne
    @JoinColumn(name = "LIKER_USER_ID")
    private Accounts likerUser;

    @Column(name = "LIKER_SESSION_ID", length = 64)
    private String likerSessionId;

    @CreationTimestamp
    @Column(name = "LIKED_AT", nullable = false, updatable = false)
    private LocalDateTime likedAt;
}