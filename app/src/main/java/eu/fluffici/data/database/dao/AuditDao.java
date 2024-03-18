package eu.fluffici.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import eu.fluffici.data.database.entity.Audit;

@Dao
public abstract class AuditDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void bulkInsert(List<Audit> audits);

    @Query("DELETE FROM audit")
    abstract void deleteAll();

    @Query("SELECT * FROM audit ORDER BY created_at desc")
    public abstract LiveData<List<Audit>> getAuditLogs();

    @Transaction
    public void updateAll(List<Audit> audits) {
        deleteAll();
        bulkInsert(audits);
    }
}
