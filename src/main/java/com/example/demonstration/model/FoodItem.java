package com.example.demonstration.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "food_item") // Maps exactly to your MySQL table name
@Data // Automatically generates Getters, Setters, toString, and equals
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private String image;

    private String description;

    @Column(name = "is_veg") // Maps to the column we created in your SQL script
    private boolean isVeg;

    /**
     * Note: Lombok's @Data will generate a method named isVeg() 
     * which Thymeleaf uses when you write ${item.isVeg}
     */
}