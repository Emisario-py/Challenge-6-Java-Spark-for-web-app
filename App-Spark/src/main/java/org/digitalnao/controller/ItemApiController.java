package org.digitalnao.controller;

import org.digitalnao.dao.ItemDao;
import org.digitalnao.dao.UserDao;
import org.digitalnao.dao.OfferDao;
import org.digitalnao.model.Item;
import org.digitalnao.model.User;
import org.digitalnao.model.Offer;
import org.digitalnao.model.error.ErrorResponse;
import org.digitalnao.model.error.SuccessResponse;
import org.digitalnao.model.error.UserItemsResponse;
import com.google.gson.Gson;
import org.digitalnao.util.ItemValidator;

import java.util.List;
import static spark.Spark.*;

public class ItemApiController {
    private static final Gson gson = new Gson();

    public static void initRoutes(ItemDao itemDao, UserDao userDao, OfferDao offerDao) {

        before("/*", (req, res) -> res.type("application/json"));
        path("/api", () -> {
            //  Get all items (with offers)
            get("/items", (req, res) -> {
                List<Item> items = itemDao.getAll();

                // For each item, include its offers
                for (Item item : items) {
                    List<Offer> offers = offerDao.findByItemId(item.getId());
                    item.setOffers(offers);
                }

                return gson.toJson(items);
            });

            //  Create new item
            post("/items", (req, res) -> {
                try {
                    Item item = gson.fromJson(req.body(), Item.class);

                    var validationError = ItemValidator.validate(item);
                    if (validationError != null) {
                        res.status(400);
                        return gson.toJson(validationError);
                    }

                    int generatedId = itemDao.insert(item);
                    item.setId(generatedId);

                    res.status(201);
                    return gson.toJson(new SuccessResponse("Item created successfully"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error while processing the request: " + e.getMessage()));
                }
            });

            //  Get item by id (with offers)
            get("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item item = itemDao.findById(id);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    List<Offer> offers = offerDao.findByItemId(id);
                    item.setOffers(offers);

                    return gson.toJson(item);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                }
            });

            // Get items by user (with offers)
            get("/users/:userId/items", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    List<Item> items = itemDao.findByUserId(userId);

                    for (Item item : items) {
                        List<Offer> offers = offerDao.findByItemId(item.getId());
                        item.setOffers(offers);
                    }

                    UserItemsResponse response = new UserItemsResponse();
                    response.setUser(user);
                    response.setItems(items);
                    response.setTotalItems(items.size());

                    return gson.toJson(response);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error while processing the request"));
                }
            });

            // Update item
            put("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item existingItem = itemDao.findById(id);

                    if (existingItem == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    Item item = gson.fromJson(req.body(), Item.class);
                    item.setId(id);

                    User user = userDao.findById(item.getUserId());
                    if (user == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Not a valid user id"));
                    }

                    ErrorResponse validationError = ItemValidator.validate(item);
                    if (validationError != null) {
                        res.status(400);
                        return gson.toJson(validationError);
                    }

                    itemDao.update(item);
                    return gson.toJson(item);

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error while processing the request"));
                }
            });

            // Assign user to item
            put("/items/:id/user/:userId", (req, res) -> {
                try {
                    int itemId = Integer.parseInt(req.params(":id"));
                    int userId = Integer.parseInt(req.params(":userId"));

                    Item item = itemDao.findById(itemId);
                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    User user = userDao.findById(userId);
                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    item.setUserId(userId);
                    itemDao.update(item);

                    return gson.toJson(new SuccessResponse("User assigned successfully to the item"));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error while assigning user to the item"));
                }
            });

            // Delete item by id
            delete("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item item = itemDao.findById(id);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    itemDao.delete(id);
                    return gson.toJson(new SuccessResponse("Item deleted successfully"));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                }
            });

            // Delete all items by user
            delete("/users/:userId/items", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    itemDao.deleteByUserId(userId);
                    return gson.toJson(new SuccessResponse("All items of the user deleted successfully"));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                }
            });
        });
    }
}
