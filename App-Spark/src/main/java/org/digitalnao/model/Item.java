package org.digitalnao.model;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private int id;
    private Integer userId;
    private String name;
    private String description;
    private double initialPrice;
    private double highestOffer;
    private List<Offer> offers;
}