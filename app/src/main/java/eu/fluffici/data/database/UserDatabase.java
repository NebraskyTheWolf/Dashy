package eu.fluffici.data.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import eu.fluffici.data.database.dao.AuditDao;
import eu.fluffici.data.database.dao.UserDao;
import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.database.transformer.DateConverter;

@Database(entities = {User.class, Audit.class}, version = 4)
@TypeConverters({DateConverter.class})
public abstract class UserDatabase extends RoomDatabase {

    private static final String LOG_TAG = UserDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "user_table";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static UserDatabase mInstance;


    public static UserDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "Getting " + DATABASE_NAME + " database");

        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = Room.databaseBuilder(context, UserDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
                Log.d(LOG_TAG, DATABASE_NAME + " database has been created.");
            }
        }
        return mInstance;
    }

    // The associated DAOs for the database
    public abstract UserDao userDao();
    public abstract AuditDao auditDao();
}