package org.digitalnao.dao;

import org.digitalnao.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import java.util.List;

@RegisterBeanMapper(User.class)
public interface UserDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS users (id IDENTITY PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL)")
    void createTable();

    @SqlUpdate("INSERT INTO users (name, email) VALUES (:name, :email)")
    int insert(@BindBean User user);

    @SqlQuery("SELECT * FROM users")
    List<User> getAll();

    @SqlQuery("SELECT * FROM users WHERE id = :id")
    User findById(@Bind("id") int id);

    @SqlUpdate("UPDATE users SET name = :name, email = :email WHERE id = :id")
    void update(@BindBean User user);

    @SqlUpdate("DELETE FROM users WHERE id = :id")
    void delete(@Bind("id") int id);
}