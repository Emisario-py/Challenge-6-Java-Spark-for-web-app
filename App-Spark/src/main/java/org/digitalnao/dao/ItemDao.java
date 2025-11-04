package org.digitalnao.dao;

import org.digitalnao.model.Item;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import java.util.List;

@RegisterBeanMapper(Item.class)
public interface ItemDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS items (id IDENTITY PRIMARY KEY, user_id INT NULL, name VARCHAR(100), description VARCHAR(255))")
    void createTable();

    @SqlUpdate("INSERT INTO items (user_id, name, description) VALUES (:userId, :name, :description)")
    int insert(@BindBean Item item);

    @SqlQuery("SELECT * FROM items")
    List<Item> getAll();

    @SqlQuery("SELECT * FROM items WHERE id = :id")
    Item findById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM items WHERE user_id = :userId")
    List<Item> findByUserId(@Bind("userId") int userId);

    @SqlUpdate("UPDATE items SET user_id = :userId, name = :name, description = :description")
    void update(@BindBean Item item);

    @SqlUpdate("DELETE FROM items WHERE id = :id")
    void delete(@Bind("id") int id);

    @SqlUpdate("DELETE FROM items WHERE user_id = :userId")
    void deleteByUserId(@Bind("userId") int userId);
}