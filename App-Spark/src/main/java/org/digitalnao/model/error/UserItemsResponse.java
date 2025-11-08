package org.digitalnao.model.error;

import lombok.Data;
import org.digitalnao.model.User;
import org.digitalnao.model.Item;
import java.util.List;

@Data
public class UserItemsResponse {
    private User user;
    private List<Item> items;
    private int totalItems;
}