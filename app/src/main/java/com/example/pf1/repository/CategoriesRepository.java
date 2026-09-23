package com.example.pf1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pf1.entity.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
}