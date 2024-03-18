package eu.fluffici.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    public int id;
    public String name;
    public String email;
    public String created_at;
    public String updated_at;
}
