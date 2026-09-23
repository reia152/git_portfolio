package com.example.pf1.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.pf1.constants.InquiriesValues;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INQUIRIES")
@Getter
@Setter
public class Inquiries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Categories category;

    @Column(name = "CONTENT", nullable = false, length = 1500)
    private String content;

    @Column(name = "STATUS", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = InquiriesValues.STATUS_UNHANDLED;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}