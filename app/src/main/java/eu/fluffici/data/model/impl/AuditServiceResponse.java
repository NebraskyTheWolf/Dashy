package eu.fluffici.data.model.impl;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import eu.fluffici.data.network.model.AuditS;

public class AuditServiceResponse {

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
    public List<AuditS> data = null;

    @NonNull
    @Override
    public String toString() {
        return "AuditServiceResponse{" +
                "total=" + total +
                ", perPage=" + perPage +
                ", currentPage=" + currentPage +
                ", lastPage=" + lastPage +
                ", nextPageUrl='" + nextPageUrl + '\'' +
                ", prevPageUrl=" + prevPageUrl +
                ", from=" + from +
                ", to=" + to +
                ", data=" + data +
                '}';
    }
}