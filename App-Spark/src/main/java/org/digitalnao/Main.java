package org.digitalnao;

import org.digitalnao.controller.ItemApiController;
import org.digitalnao.controller.OfferApiController;
import org.digitalnao.controller.UserApiController;
import org.digitalnao.dao.ItemDao;
import org.digitalnao.dao.OfferDao;
import org.digitalnao.dao.UserDao;
import org.digitalnao.util.DatabaseSeeder;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(8080);

        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        jdbi.installPlugin(new SqlObjectPlugin());

        UserDao userDao = jdbi.onDemand(UserDao.class);
        ItemDao itemDao = jdbi.onDemand(ItemDao.class);
        OfferDao offerDao = jdbi.onDemand(OfferDao.class);

        userDao.createTable();
        itemDao.createTable();
        offerDao.createTable();

        DatabaseSeeder.run(jdbi, "sql/seed-items.sql");

        UserApiController.initRoutes(userDao, offerDao);
        ItemApiController.initRoutes(itemDao, userDao, offerDao);
        OfferApiController.initRoutes(offerDao, userDao, itemDao);

        System.out.println("🚀 Server running on http://localhost:8080");
        System.out.println("🔌 API REST: http://localhost:8080/api/users");

    }
}
