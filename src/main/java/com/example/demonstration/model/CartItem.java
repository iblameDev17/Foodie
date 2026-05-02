package com.example.demonstration.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private Long foodId;
    private String name;
    private Double price;
    private int quantity;

    public Double getTotalPrice() {
        return this.price * this.quantity;
    }
}