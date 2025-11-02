package org.digitalnao.controller;

import org.digitalnao.dao.UserDao;
import org.digitalnao.model.User;
import com.google.gson.Gson;
import static spark.Spark.*;

public class UserApiController {
    public static void initRoutes(UserDao dao) {
        Gson gson = new Gson();

        get("/users", (req, res) -> {
            res.type("application/json");
            return gson.toJson(dao.getAll());
        });

        get("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User user = dao.findById(id);
            if (user == null) {
                res.status(404);
                return gson.toJson("User not found");
            }
            return gson.toJson(user);
        });

        post("/users", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            dao.insert(user);
            res.status(201);
            return gson.toJson("User created");

        });

        put("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User user = gson.fromJson(req.body(), User.class);
            user.setId(id);
            dao.update(user);
            return gson.toJson(user);
        });

        delete("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User user = dao.findById(id);
            if (user == null) {
                res.status(404);
                return gson.toJson("User not found");
            }
            dao.delete(id);
            return gson.toJson("User deleted");
        });
    }
}
