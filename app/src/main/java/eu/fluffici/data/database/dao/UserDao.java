package eu.fluffici.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;
import eu.fluffici.data.database.entity.User;

@Dao
public abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void bulkInsert(List<User> users);

    @Query("DELETE FROM users")
    abstract void deleteAll();

    @Query("Select * FROM users ORDER BY id ASC")
    public abstract LiveData<List<User>> getUserList();

    @Transaction
    public void updateAll(List<User> users) {
        deleteAll();
        bulkInsert(users);
    }
}