package org.digitalnao.controller;

import org.digitalnao.dao.UserDao;
import org.digitalnao.dao.OfferDao;
import org.digitalnao.model.User;
import org.digitalnao.model.Offer;
import org.digitalnao.model.error.ErrorResponse;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class UserApiController {

    public static void initRoutes(UserDao userDao, OfferDao offerDao) {
        Gson gson = new Gson();

        before("/*", (req, res) -> res.type("application/json"));
        path("/api", () -> {
            // Get all users (with their offers)
            get("/users", (req, res) -> {
                List<User> users = userDao.getAll();

                // Add offers for each user
                for (User user : users) {
                    List<Offer> offers = offerDao.findByUserId(user.getId());
                    user.setOffers(offers);
                }

                return gson.toJson(users);
            });

            // Get user by id (with offers)
            get("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    User user = userDao.findById(id);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    List<Offer> offers = offerDao.findByUserId(id);
                    user.setOffers(offers);

                    return gson.toJson(user);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                }
            });

            // Create new user
            post("/users", (req, res) -> {
                try {
                    User user = gson.fromJson(req.body(), User.class);

                    if (user.getName() == null || user.getName().trim().isEmpty()) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("The user name is empty"));
                    }

                    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("The user email is empty"));
                    }

                    int generatedId = userDao.insert(user);
                    user.setId(generatedId);

                    res.status(201);
                    return gson.toJson(user);
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error while creating the user: " + e.getMessage()));
                }
            });

            // Update existing user
            put("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    User user = gson.fromJson(req.body(), User.class);
                    user.setId(id);

                    userDao.update(user);
                    List<Offer> offers = offerDao.findByUserId(id);
                    user.setOffers(offers);

                    return gson.toJson(user);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                }
            });

            // Delete user by id
            delete("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    User user = userDao.findById(id);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("User not found"));
                    }

                    userDao.delete(id);
                    return gson.toJson("User deleted successfully");
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid user id"));
                }
            });
        });
    }
}
