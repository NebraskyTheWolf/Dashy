package eu.fluffici.data.network;

import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.model.impl.UserServiceResponse;
import eu.fluffici.data.model.impl.AuditServiceResponse;
import eu.fluffici.data.network.model.AuditS;
import eu.fluffici.data.network.model.Data;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_URL = "https://api.fluffici.eu/api/";

    private static Retrofit getRetrofit() {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest  = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + System.getProperty("X-Bearer-token"))
                    .build();

            System.out.println(System.getProperty("X-Bearer-token"));
            System.out.println(System.getProperty("X-Bearer-token"));
            System.out.println(System.getProperty("X-Bearer-token"));
            System.out.println(System.getProperty("X-Bearer-token"));
            System.out.println(System.getProperty("X-Bearer-token"));

            return chain.proceed(newRequest);
        }).build();

        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Disposable getUsersDataFromService(int size, int page, DisposableObserver<UserServiceResponse> observer) {
        Log.d(LOG_TAG, "Getting data from the server");
        try {
            RestApi service = getRetrofit().create(RestApi.class);

            Observable<UserServiceResponse> observable = service.getUser(size, page);
            return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread
                    ()).subscribeWith(observer);

        } catch (Exception e) {
            Log.d(LOG_TAG, "Getting data process has been failed. ", e);
        }
        return null;
    }

    public static Disposable getAuditDataFromService(int size, int page, DisposableObserver<AuditServiceResponse> observer) {
        Log.d(LOG_TAG, "Getting data from the server");
        try {
            RestApi service = getRetrofit().create(RestApi.class);

            Observable<AuditServiceResponse> observable = service.getAudit(size, page);
            return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread
                    ()).subscribeWith(observer);

        } catch (Exception e) {
            Log.d(LOG_TAG, "Getting data process has been failed. ", e);
        }
        return null;
    }

    public static List<Audit> convertToAuditList(AuditServiceResponse auditServiceResponse) {
        List<Audit> audits = new ArrayList<>();

        Log.d(LOG_TAG, "Converting the response.");

        try {
            for (AuditS data : auditServiceResponse.data) {
                Audit user = new Audit();
                user.id = data.id;
                user.name = data.name;
                user.type = data.type;
                user.slug = data.slug;
                user.created_at = data.created_at;
                audits.add(user);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Converting the response process has been failed. ", e);
        }

        return audits;
    }

    public static List<User> convertToUserList(UserServiceResponse userServiceResponse) {
        List<User> users = new ArrayList<>();

        Log.d(LOG_TAG, "Converting the response.");
        try {
            for (Data data : userServiceResponse.data) {
                User user = new User();
                user.id = data.id;
                user.name = data.name;
                user.email = data.email;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    user.created_at = getDate(data.createdAt);
                }
                user.updated_at = getDate(data.updatedAt);
                users.add(user);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Converting the response process has been failed. ", e);
        }

        return users;
    }

    private static String getDate(String stringData) throws ParseException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Instant timeStamp = Instant.parse(stringData);
            return ZonedDateTime.ofInstant(timeStamp, ZoneOffset.UTC).toString();
        }

        return null;
    }

}