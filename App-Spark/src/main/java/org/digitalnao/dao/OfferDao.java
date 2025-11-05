package org.digitalnao.dao;

import org.digitalnao.model.Offer;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import java.util.List;

@RegisterBeanMapper(Offer.class)
public interface OfferDao {

    @SqlUpdate("""
        CREATE TABLE IF NOT EXISTS offers (
            id IDENTITY PRIMARY KEY,
            user_id INT NOT NULL,
            item_id INT NOT NULL,
            amount DOUBLE NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
        )
    """)
    void createTable();

    @SqlUpdate("INSERT INTO offers (user_id, item_id, amount) VALUES (:userId, :itemId, :amount)")
    int insert(@BindBean Offer offer);

    @SqlQuery("SELECT * FROM offers")
    List<Offer> getAll();

    @SqlQuery("SELECT * FROM offers WHERE id = :id")
    Offer findById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM offers WHERE user_id = :userId")
    List<Offer> findByUserId(@Bind("userId") int userId);

    @SqlQuery("SELECT * FROM offers WHERE item_id = :itemId")
    List<Offer> findByItemId(@Bind("itemId") int itemId);

    @SqlUpdate("""
        UPDATE offers 
        SET user_id = :userId, 
            item_id = :itemId, 
            amount = :amount 
        WHERE id = :id
    """)
    void update(@BindBean Offer offer);

    @SqlUpdate("DELETE FROM offers WHERE id = :id")
    void delete(@Bind("id") int id);

    @SqlUpdate("DELETE FROM offers WHERE user_id = :userId")
    void deleteByUserId(@Bind("userId") int userId);

    @SqlUpdate("DELETE FROM offers WHERE item_id = :itemId")
    void deleteByItemId(@Bind("itemId") int itemId);
}
