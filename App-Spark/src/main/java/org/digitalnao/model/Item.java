package org.digitalnao.model;

import lombok.Data;

@Data
public class Item {
    private int id;
    private int userId;
    private String name;
    private String description;
}