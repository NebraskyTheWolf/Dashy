package eu.fluffici.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Audit {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("slug")
    @Expose
    public String slug;

    @SerializedName("created_at")
    @Expose
    public String created_at;
}
