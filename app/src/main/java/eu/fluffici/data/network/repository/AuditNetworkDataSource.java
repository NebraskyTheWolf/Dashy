package eu.fluffici.data.network.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import eu.fluffici.dashy.AppExecutors;
import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.model.impl.AuditServiceResponse;
import eu.fluffici.data.model.ServiceRequest;
import eu.fluffici.data.network.NetworkUtils;
import io.reactivex.observers.DisposableObserver;

public class AuditNetworkDataSource {

    private static final String LOG_TAG = AuditNetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static AuditNetworkDataSource sInstance;
    private static final Object LOCK = new Object();

    private final AppExecutors mAppExecutors;
    private final MutableLiveData<List<Audit>> mDownloadedData;

    public AuditNetworkDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
        this.mDownloadedData = new MutableLiveData<>();
    }

    public static AuditNetworkDataSource getInstance(AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AuditNetworkDataSource(executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void fetchData(ServiceRequest serviceRequest) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                NetworkUtils.getAuditDataFromService(serviceRequest.getSize(), serviceRequest.getPage(), new
                        DisposableObserver<AuditServiceResponse>() {

                            @Override
                            public void onNext(AuditServiceResponse auditServiceResponse) {
                                setAuditList(NetworkUtils.convertToAuditList(auditServiceResponse));

                                System.out.println(auditServiceResponse.toString());
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


    private void setAuditList(List<Audit> auditList){
        mDownloadedData.postValue(auditList);
    }

    public LiveData<List<Audit>> getAuditList(){
        return mDownloadedData;
    }

}