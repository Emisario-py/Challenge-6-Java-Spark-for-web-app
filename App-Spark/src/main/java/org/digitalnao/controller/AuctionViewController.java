package org.digitalnao.controller;

import org.digitalnao.dao.ItemDao;
import org.digitalnao.dao.OfferDao;
import org.digitalnao.dao.UserDao;
import org.digitalnao.model.Item;
import org.digitalnao.model.Offer;
import org.digitalnao.model.User;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.*;
import static spark.Spark.*;

public class AuctionViewController {

    private static final MustacheTemplateEngine mte = new MustacheTemplateEngine();

    private static String renderHtml(ModelAndView modelAndView, spark.Response res) {
        res.type("text/html; charset=utf-8");
        return mte.render(modelAndView);
    }

    public static void initRoutes(UserDao userDao, ItemDao itemDao, OfferDao offerDao) {
        staticFiles.location("/");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<User> users = userDao.getAll();
            model.put("users", users);
            model.put("error", req.queryParams("error"));
            model.put("success", req.queryParams("success"));
            return renderHtml(new ModelAndView(model, "index.mustache"), res);
        });

        post("/profiles", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");

            if (name == null || name.isBlank() || email == null || email.isBlank()) {
                res.redirect("/?error=Name+and+email+are+required");
                return null;
            }

            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());

            int id = userDao.insert(user);
            res.redirect("/users/" + id + "?success=Profile+created");
            return null;
        });

        get("/users/:id", (req, res) -> {
            try {
                int userId = Integer.parseInt(req.params(":id"));
                User user = userDao.findById(userId);
                if (user == null) {
                    res.redirect("/?error=User+not+found");
                    return null;
                }
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                List<Offer> myOffers = offerDao.findByUserId(userId);

                for (Offer offer : myOffers) {
                    Item item = itemDao.findById(offer.getItemId());
                    if (item != null) {
                        offer.setItemName(item.getName());
                        if (offer.getCreateAt() != null) {
                            offer.setFormattedDate(fmt.format(offer.getCreateAt()));
                        }

                        List<Offer> itemOffers = offerDao.findByItemId(item.getId());
                        double highest = itemOffers.stream()
                                .mapToDouble(Offer::getAmount)
                                .max()
                                .orElse(item.getInitialPrice());

                        if (offer.getAmount() >= highest) {
                            offer.setStatus("Tu oferta es la más alta 🟢");
                        } else {
                            offer.setStatus("Tu oferta fue superada 🔴");
                        }
                    }
                }

                Map<String, Object> model = new HashMap<>();
                model.put("user", user);

                List<Item> items = itemDao.getAll();

                for (Item item : items) {
                    List<Offer> offers = offerDao.findByItemId(item.getId());
                    double highest = offers.stream()
                            .mapToDouble(Offer::getAmount)
                            .max()
                            .orElse(item.getInitialPrice());
                    item.setHighestOffer(highest);
                }

                model.put("items", items);
                model.put("myOffers", myOffers);
                model.put("success", req.queryParams("success"));
                model.put("error", req.queryParams("error"));

                return renderHtml(new ModelAndView(model, "user_dashboard.mustache"), res);
            } catch (NumberFormatException e) {
                res.redirect("/?error=Invalid+user+id");
                return null;
            }
        });

        get("/items/:itemId", (req, res) -> {
            try {
                int itemId = Integer.parseInt(req.params(":itemId"));
                Item item = itemDao.findById(itemId);
                if (item == null) {
                    res.redirect("/?error=Item+not+found");
                    return null;
                }

                Integer userId = null;
                try {
                    userId = Integer.parseInt(req.queryParams("userId"));
                } catch (Exception ignored) {}

                List<Offer> offers = offerDao.findByItemId(itemId);
                for (Offer offer : offers) {
                    User u = userDao.findById(offer.getUserId());
                    if (u != null) {
                        offer.setUserName(u.getName());
                    } else {
                        offer.setUserName("Usuario desconocido");
                    }
                }
                double highest = offers.stream()
                        .mapToDouble(Offer::getAmount)
                        .max()
                        .orElse(item.getInitialPrice());

                Map<String, Object> model = new HashMap<>();
                model.put("item", item);
                model.put("offers", offers);
                model.put("highest", highest);
                model.put("userId", userId);
                model.put("success", req.queryParams("success"));
                model.put("error", req.queryParams("error"));

                return renderHtml(new ModelAndView(model, "item_detail.mustache"), res);
            } catch (NumberFormatException e) {
                res.redirect("/?error=Invalid+item+id");
                return null;
            }
        });

        post("/items/:itemId/offers", (req, res) -> {
            try {
                int itemId = Integer.parseInt(req.params(":itemId"));
                int userId = Integer.parseInt(req.queryParams("userId"));
                String amountStr = req.queryParams("amount");

                User user = userDao.findById(userId);
                if (user == null) {
                    res.redirect("/?error=User+not+found");
                    return null;
                }

                Item item = itemDao.findById(itemId);
                if (item == null) {
                    res.redirect("/?error=Item+not+found");
                    return null;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        throw new NumberFormatException("Amount must be positive");
                    }
                } catch (Exception e) {
                    res.redirect("/items/" + itemId + "?userId=" + userId +
                            "&error=Invalid+amount");
                    return null;
                }

                List<Offer> offers = offerDao.findByItemId(itemId);
                double highest = offers.stream()
                        .mapToDouble(Offer::getAmount)
                        .max()
                        .orElse(item.getInitialPrice());

                if (amount <= highest) {
                    res.redirect("/items/" + itemId + "?userId=" + userId +
                            "&error=Offer+must+be+higher+than+" + highest);
                    return null;
                }

                Offer offer = new Offer();
                offer.setItemId(itemId);
                offer.setUserId(userId);
                offer.setAmount(amount);
                offer.setCreateAt(new Date());
                offerDao.insert(offer);

                res.redirect("/items/" + itemId + "?userId=" + userId +
                        "&success=Offer+placed");
                return null;

            } catch (NumberFormatException e) {
                res.redirect("/?error=Invalid+parameters");
                return null;
            }
        });

        get("/users/:id/offers", (req, res) -> {
            try {
                int userId = Integer.parseInt(req.params(":id"));
                User user = userDao.findById(userId);
                if (user == null) {
                    res.redirect("/?error=User+not+found");
                    return null;
                }

                List<Offer> allOffers = offerDao.findByUserId(userId);
                Map<Integer, Offer> latestOffersByItem = new HashMap<>();
                for (Offer offer : allOffers) {
                    Item item = itemDao.findById(offer.getItemId());
                    if (item != null) {
                        offer.setItemName(item.getName());
                    }

                    Offer existing = latestOffersByItem.get(offer.getItemId());
                    if (existing == null || offer.getCreateAt().after(existing.getCreateAt())) {
                        latestOffersByItem.put(offer.getItemId(), offer);
                    }
                }
                List<Offer> myOffers = new ArrayList<>(latestOffersByItem.values());
                myOffers.sort((a, b) -> b.getCreateAt().compareTo(a.getCreateAt()));
                Map<String, Object> model = new HashMap<>();
                model.put("user", user);
                model.put("offers", myOffers);
                model.put("success", req.queryParams("success"));
                model.put("error", req.queryParams("error"));

                return renderHtml(new ModelAndView(model, "user_offers.mustache"), res);
            } catch (NumberFormatException e) {
                res.redirect("/?error=Invalid+user+id");
                return null;
            }
        });
    }
}