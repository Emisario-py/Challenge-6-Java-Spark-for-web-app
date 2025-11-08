package org.digitalnao.model;

import lombok.Data;

import java.util.Date;

@Data
public class Offer {
    private int id;
    private int userId;
    private int itemId;
    private String itemName;
    private String userName;
    private Date createAt;
    private String formattedDate;
    private String status;
    private double amount;
}