package org.digitalnao;

import org.digitalnao.controller.UserApiController;
import org.digitalnao.dao.UserDao;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(8080);

        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        jdbi.installPlugin(new SqlObjectPlugin());

        UserDao dao = jdbi.onDemand(UserDao.class);
        dao.createTable();


        UserApiController.initRoutes(dao);

        System.out.println("🚀 Server running on http://localhost:8080");
        System.out.println("🔌 API REST: http://localhost:8080/api/users");

    }
}
