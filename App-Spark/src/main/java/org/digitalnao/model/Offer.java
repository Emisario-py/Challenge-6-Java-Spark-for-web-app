package org.digitalnao.model;

import lombok.Data;

@Data
public class Offer {
    private int id;
    private int userId;
    private int itemId;
    private double amount;
}