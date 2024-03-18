package eu.fluffici.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audit")
public class Audit {
    @PrimaryKey
    public int id;

    public String name;
    public String type;
    public String slug;

    public String created_at;
}
