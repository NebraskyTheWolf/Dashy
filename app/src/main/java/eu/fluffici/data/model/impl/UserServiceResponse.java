package eu.fluffici.data.model.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import eu.fluffici.data.network.model.Data;

public class UserServiceResponse {

    @SerializedName("total")
    @Expose
    public Integer total;

    @SerializedName("per_page")
    @Expose
    public Integer perPage;

    @SerializedName("current_page")
    @Expose
    public Integer currentPage;

    @SerializedName("last_page")
    @Expose
    public Integer lastPage;

    @SerializedName("next_page_url")
    @Expose
    public String nextPageUrl;

    @SerializedName("prev_page_url")
    @Expose
    public Object prevPageUrl;

    @SerializedName("from")
    @Expose
    public Integer from;

    @SerializedName("to")
    @Expose
    public Integer to;

    @SerializedName("data")
    @Expose
    public List<Data> data = null;
}