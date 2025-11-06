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
            create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
        )
    """)
    void createTable();

    @SqlUpdate("""
        INSERT INTO offers (user_id, item_id, amount, create_at)
        VALUES (:userId, :itemId, :amount, :createAt)
    """)
    int insert(@BindBean Offer offer);

    @SqlQuery("SELECT * FROM offers ORDER BY create_at DESC")
    List<Offer> getAll();

    @SqlQuery("SELECT * FROM offers WHERE id = :id")
    Offer findById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM offers WHERE user_id = :userId ORDER BY create_at DESC")
    List<Offer> findByUserId(@Bind("userId") int userId);

    @SqlQuery("SELECT * FROM offers WHERE item_id = :itemId ORDER BY create_at DESC")
    List<Offer> findByItemId(@Bind("itemId") int itemId);

    @SqlUpdate("""
        UPDATE offers 
        SET user_id = :userId, 
            item_id = :itemId, 
            amount = :amount,
            create_at = :createAt
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
