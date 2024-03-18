package eu.fluffici.data.network.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import eu.fluffici.dashy.AppExecutors;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.model.ServiceRequest;
import eu.fluffici.data.model.impl.UserServiceResponse;
import eu.fluffici.data.network.NetworkUtils;
import io.reactivex.observers.DisposableObserver;

public class UserNetworkDataSource {

    private static final String LOG_TAG = UserNetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static UserNetworkDataSource sInstance;
    private static final Object LOCK = new Object();

    private final AppExecutors mAppExecutors;
    private final MutableLiveData<List<User>> mDownloadedData;

    public UserNetworkDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
        this.mDownloadedData = new MutableLiveData<>();
    }

    public static UserNetworkDataSource getInstance(AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new UserNetworkDataSource(executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void fetchData(ServiceRequest serviceRequest) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                NetworkUtils.getUsersDataFromService(serviceRequest.getSize(), serviceRequest.getPage(), new
                        DisposableObserver<UserServiceResponse>() {

                            @Override
                            public void onNext(UserServiceResponse userServiceResponse) {
                                setUserList(NetworkUtils.convertToUserList(userServiceResponse));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(LOG_TAG, "Getting data process has been failed.", e);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            } catch (Exception ex) {
                Log.e(LOG_TAG, "Getting data process has been failed.", ex);
            }
        });
    }


    private void setUserList(List<User> userList){
        mDownloadedData.postValue(userList);
    }

    public LiveData<List<User>> getUserList(){
        return mDownloadedData;
    }

}