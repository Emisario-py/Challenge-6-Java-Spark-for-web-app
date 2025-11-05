package org.digitalnao.model;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private int id;
    private Integer userId;
    private String name;
    private String description;
    private List<Offer> offers;
}