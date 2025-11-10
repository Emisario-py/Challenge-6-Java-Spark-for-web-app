package org.digitalnao.dao;

import org.digitalnao.model.Item;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import java.util.List;

@RegisterBeanMapper(Item.class)
public interface ItemDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS items (id IDENTITY PRIMARY KEY, user_id INT NULL, name VARCHAR(100), description VARCHAR(255), initialPrice double not null)")
    void createTable();

    @SqlUpdate("INSERT INTO items (user_id, name, description, initialPrice) VALUES (:userId, :name, :description, :initialPrice)")
    @GetGeneratedKeys
    int insert(@BindBean Item item);

    @SqlQuery("SELECT * FROM items")
    List<Item> getAll();

    @SqlQuery("SELECT * FROM items WHERE id = :id")
    Item findById(@Bind("id") int id);

    @SqlUpdate("UPDATE items SET name = :name, description = :description, initialPrice = :initialPrice WHERE id = :id")
    void update(@BindBean Item item);

    @SqlUpdate("DELETE FROM items WHERE id = :id")
    void delete(@Bind("id") int id);

    @SqlUpdate("DELETE FROM items WHERE user_id = :userId")
    void deleteByUserId(@Bind("userId") int userId);
}