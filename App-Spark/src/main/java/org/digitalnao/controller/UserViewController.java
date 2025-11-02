package org.digitalnao.controller;

import org.digitalnao.dao.UserDao;
import org.digitalnao.model.User;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static spark.Spark.*;

public class UserViewController {

    // 🔹 Motor de plantillas Mustache
    private static final MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

    public static void initRoutes(UserDao dao) {

        // 🔹 Manejo global de excepciones
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Ha ocurrido un error inesperado");
            res.status(500);
            res.body(templateEngine.render(new ModelAndView(model, "error.mustache")));
        });

        // 🔹 Página principal con lista de usuarios
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<User> users = dao.getAll();
            model.put("users", users);
            model.put("hasUsers", users != null && !users.isEmpty());
            return templateEngine.render(new ModelAndView(model, "users.mustache"));
        });

        // 🔹 Formulario para crear usuario (GET)
        get("/users/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return templateEngine.render(new ModelAndView(model, "create-user.mustache"));
        });

        // 🔹 Crear usuario (POST)
        post("/users/create", (req, res) -> {
            try {
                String name = req.queryParams("name");
                String email = req.queryParams("email");

                if (name == null || name.trim().isEmpty() ||
                        email == null || email.trim().isEmpty()) {
                    req.session().attribute("message", "Todos los campos son requeridos");
                    res.redirect("/users/new");
                    return null;
                }

                User user = new User();
                user.setName(name.trim());
                user.setEmail(email.trim());

                int generatedId = dao.insert(user);
                user.setId(generatedId);

                req.session().attribute("message", "Usuario creado exitosamente");
                res.redirect("/");
                return null;
            } catch (Exception e) {
                req.session().attribute("message", "Error al crear el usuario");
                res.redirect("/users/new");
                return null;
            }
        });

        // 🔹 Formulario para editar usuario (GET)
        get("/users/:id/edit", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                User user = dao.findById(id);

                Map<String, Object> model = new HashMap<>();
                if (user != null) {
                    model.put("user", user);
                    return templateEngine.render(new ModelAndView(model, "edit-user.mustache"));
                } else {
                    req.session().attribute("message", "Usuario no encontrado");
                    res.redirect("/");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                req.session().attribute("message", "Error al cargar el usuario");
                res.redirect("/");
                return null;
            }
        });

        // 🔹 Actualizar usuario (POST)
        post("/users/:id/update", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                String name = req.queryParams("name");
                String email = req.queryParams("email");

                if (name == null || name.trim().isEmpty() ||
                        email == null || email.trim().isEmpty()) {
                    req.session().attribute("message", "Todos los campos son requeridos");
                    res.redirect("/users/" + id + "/edit");
                    return null;
                }

                User user = new User();
                user.setId(id);
                user.setName(name.trim());
                user.setEmail(email.trim());

                dao.update(user);

                req.session().attribute("message", "Usuario actualizado exitosamente");
                res.redirect("/");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                req.session().attribute("message", "Error al actualizar el usuario");
                res.redirect("/");
                return null;
            }
        });

        // 🔹 Eliminar usuario (POST)
        post("/users/:id/delete", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                User user = dao.findById(id);

                if (user != null) {
                    dao.delete(id);
                    req.session().attribute("message", "Usuario eliminado exitosamente");
                } else {
                    req.session().attribute("message", "Usuario no encontrado");
                }

                res.redirect("/");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                req.session().attribute("message", "Error al eliminar el usuario");
                res.redirect("/");
                return null;
            }
        });

        // 🔹 Ver detalle de un usuario
        get("/users/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                User user = dao.findById(id);

                Map<String, Object> model = new HashMap<>();
                if (user != null) {
                    model.put("user", user);
                    return templateEngine.render(new ModelAndView(model, "user-detail.mustache"));
                } else {
                    res.status(404);
                    model.put("message", "Usuario no encontrado");
                    return templateEngine.render(new ModelAndView(model, "error.mustache"));
                }
            } catch (Exception e) {
                res.status(500);
                Map<String, Object> model = new HashMap<>();
                model.put("message", "Error al cargar el usuario");
                return templateEngine.render(new ModelAndView(model, "error.mustache"));
            }
        });
    }
}
