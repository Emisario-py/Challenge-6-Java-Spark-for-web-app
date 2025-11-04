package org.digitalnao.model;

import lombok.Data;

@Data
public class Item {
    private int id;
    private Integer userId;
    private String name;
    private String description;
}