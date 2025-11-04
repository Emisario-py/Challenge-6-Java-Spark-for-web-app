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

            // Obtener item por ID
            get("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item item = itemDao.findById(id);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item no encontrado"));
                    }

                    return gson.toJson(item);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("ID inválido"));
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
                        return gson.toJson(new ErrorResponse("Usuario no encontrado"));
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
                    return gson.toJson(new ErrorResponse("ID de usuario inválido"));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ErrorResponse("Error al procesar la solicitud"));
                }
            });

            // Crear item para un usuario
            post("/users/:userId/items", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));

                    // Verificar que el usuario existe
                    User user = userDao.findById(userId);

                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Usuario no encontrado"));
                    }

                    Item item = gson.fromJson(req.body(), Item.class);
                    item.setUserId(userId);

                    // Validación
                    ErrorResponse validationError = ItemValidator.validate(item);
                    if (validationError != null) {
                        res.status(400);
                        return gson.toJson(validationError);
                    }

                    int generatedId = itemDao.insert(item);
                    item.setId(generatedId);

                    res.status(201);
                    return gson.toJson("Item created Successfully");

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("ID de usuario inválido"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error al procesar la solicitud: " + e.getMessage()));
                }
            });

            // Actualizar item
            put("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item existingItem = itemDao.findById(id);

                    if (existingItem == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item no encontrado"));
                    }

                    Item item = gson.fromJson(req.body(), Item.class);
                    item.setId(id);

                    // Validar que el usuario existe
                    User user = userDao.findById(item.getUserId());
                    if (user == null) {
                        res.status(400);
                        return gson.toJson(new ErrorResponse("Usuario inválido"));
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
                    return gson.toJson(new ErrorResponse("ID inválido"));
                } catch (Exception e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Error al procesar la solicitud"));
                }
            });

            // Eliminar item
            delete("/items/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Item item = itemDao.findById(id);

                    if (item == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Item no encontrado"));
                    }

                    itemDao.delete(id);
                    return gson.toJson(new SuccessResponse("Item eliminado exitosamente"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("ID inválido"));
                }
            });

            // Eliminar todos los items de un usuario
            delete("/users/:userId/items", (req, res) -> {
                try {
                    int userId = Integer.parseInt(req.params(":userId"));

                    User user = userDao.findById(userId);
                    if (user == null) {
                        res.status(404);
                        return gson.toJson(new ErrorResponse("Usuario no encontrado"));
                    }

                    itemDao.deleteByUserId(userId);
                    return gson.toJson(new SuccessResponse("Todos los items del usuario eliminados"));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("ID de usuario inválido"));
                }
            });
    }


}