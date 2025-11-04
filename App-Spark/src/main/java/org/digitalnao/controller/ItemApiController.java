package org.digitalnao.controller;

import org.digitalnao.dao.ItemDao;
import org.digitalnao.dao.UserDao;
import org.digitalnao.model.Item;
import org.digitalnao.model.User;
import org.digitalnao.model.error.ErrorResponse;
import org.digitalnao.model.error.SuccessResponse;
import org.digitalnao.model.error.UserItemsResponse;
import com.google.gson.Gson;
import org.digitalnao.util.ItemValidator;

import java.util.List;
import static spark.Spark.*;

public class ItemApiController {
    private static final Gson gson = new Gson();

    public static void initRoutes(ItemDao itemDao, UserDao userDao) {


            before("/*", (req, res) -> res.type("application/json"));

            // Obtener todos los items
            get("/items", (req, res) -> {
                List<Item> items = itemDao.getAll();
                return gson.toJson(items);
            });

        post("/items", (req, res) -> {
            try {
                Item item = gson.fromJson(req.body(), Item.class);

                // Validación básica
                var validationError = ItemValidator.validate(item);
                if (validationError != null) {
                    res.status(400);
                    return gson.toJson(validationError);
                }

                // userId puede ser null
                int generatedId = itemDao.insert(item);
                item.setId(generatedId);

                res.status(201);
                return gson.toJson(new SuccessResponse("Item created successfully"));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error in while processing the solicitude: " + e.getMessage()));
            }
        });

            // Obtener item por ID
            get("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item item = itemDao.findById(id);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item not found"));
                    }

                    return gson.toJson(item);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Not a valid item id"));
                }
            });

            // Obtener todos los items de un usuario específico
            get("/users/:userId/items", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));

                    // Verificar que el usuario existe
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Usuario not found"));
                    }

                    List<Item> items = itemDao.findByUserId(userId);

                    // Crear respuesta con información del usuario e items
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
                    return gson.toJson(new ErrorResponse("Error while processing the solicitude"));
                }
            });


            // Actualizar item
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

                    // Validar que el usuario existe
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
                    return gson.toJson(new ErrorResponse("Error while processing the solicitude"));
                }
            });

        // Asignar o cambiar usuario a un item existente
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
                return gson.toJson(new ErrorResponse("Error in the assignation of the user to the item"));
            }
        });

            // Eliminar item
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

            // Eliminar todos los items de un usuario
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
    }


}