package com.example.demonstration.repository;

import com.example.demonstration.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodItem, Long> {
    // This will allow you to implement the Veg/Non-Veg toggle later
    List<FoodItem> findByIsVeg(boolean isVeg);
}