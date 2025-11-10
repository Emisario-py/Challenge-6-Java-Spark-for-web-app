package org.digitalnao.controller;

import org.digitalnao.dao.OfferDao;
import org.digitalnao.dao.UserDao;
import org.digitalnao.dao.ItemDao;
import org.digitalnao.model.Offer;
import org.digitalnao.model.User;
import org.digitalnao.model.Item;
import org.digitalnao.model.error.ErrorResponse;
import org.digitalnao.model.error.SuccessResponse;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class OfferApiController {
    private static final Gson gson = new Gson();

    public static void initRoutes(OfferDao offerDao, UserDao userDao, ItemDao itemDao) {

        before("/*", (req, res) -> res.type("application/json"));
        path("/api", () -> {
            // Get all offers
            get("/offers", (req, res) -> {
                List<Offer> offers = offerDao.getAll();
                return gson.toJson(offers);
            });

            // Create new offer and assign it to the item
            post("/offers", (req, res) -> {
                try {
                    Offer offer = gson.fromJson(req.body(), Offer.class);

                    User user = userDao.findById(offer.getUserId());
                    if (user == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    Item item = itemDao.findById(offer.getItemId());
                    if (item == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }


                    int generatedId = offerDao.insert(offer);
                    offer.setId(generatedId);

                    itemDao.update(item);

                    res.status(201);
                    return gson.toJson(new SuccessResponse("Offer created successfully and assigned to item"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error while processing the request: " + e.getMessage()));
                }
            });

            // Get offer by id
            get("/offers/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Offer offer = offerDao.findById(id);

                    if (offer == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Offer not found"));
                    }

                    return gson.toJson(offer);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid offer id"));
                }
            });

            // Get offers by user
            get("/users/:userId/offers", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    List<Offer> offers = offerDao.findByUserId(userId);
                    return gson.toJson(offers);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error while processing the request"));
                }
            });

            // Get offers by item
            get("/items/:itemId/offers", (req, res) -> {
                try {
                    int itemId = Integer.parseInt(req.params(":itemId"));
                    Item item = itemDao.findById(itemId);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    List<Offer> offers = offerDao.findByItemId(itemId);
                    return gson.toJson(offers);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error while processing the request"));
                }
            });

            // Update an offer
            put("/offers/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Offer existingOffer = offerDao.findById(id);

                    if (existingOffer == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Offer not found"));
                    }

                    Offer offer = gson.fromJson(req.body(), Offer.class);
                    offer.setId(id);

                    User user = userDao.findById(offer.getUserId());
                    if (user == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    Item item = itemDao.findById(offer.getItemId());
                    if (item == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    offerDao.update(offer);

                    itemDao.update(item);

                    return gson.toJson(new SuccessResponse("Offer updated successfully and linked to item"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid offer id"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error while processing the request"));
                }
            });

            // Delete offer by id
            delete("/offers/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Offer offer = offerDao.findById(id);

                    if (offer == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Offer not found"));
                    }

                    offerDao.delete(id);


                    return gson.toJson(new SuccessResponse("Offer deleted successfully and item unlinked"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid offer id"));
                }
            });

            // Delete all offers by user
            delete("/users/:userId/offers", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    offerDao.deleteByUserId(userId);
                    return gson.toJson(new SuccessResponse("All offers of the user deleted successfully"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                }
            });

            // Delete all offers by item
            delete("/items/:itemId/offers", (req, res) -> {
                try {
                    int itemId = Integer.parseInt(req.params(":itemId"));
                    Item item = itemDao.findById(itemId);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    offerDao.deleteByItemId(itemId);

                    itemDao.update(item);

                    return gson.toJson(new SuccessResponse("All offers for the item deleted successfully"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                }
            });
        });
    }
}
