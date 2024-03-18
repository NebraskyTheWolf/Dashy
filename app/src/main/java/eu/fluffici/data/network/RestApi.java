package eu.fluffici.data.network;

import eu.fluffici.data.model.impl.UserServiceResponse;
import eu.fluffici.data.model.impl.AuditServiceResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {

    @GET("users")
    Observable<UserServiceResponse> getUser(@Query("per_page") int size, @Query("current_page") int page);

    @GET("audit")
    Observable<AuditServiceResponse> getAudit(@Query("per_page") int size, @Query("current_page") int page);
}