package eu.fluffici.data;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import eu.fluffici.dashy.AppExecutors;
import eu.fluffici.data.database.dao.AuditDao;
import eu.fluffici.data.database.dao.UserDao;
import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.model.ServiceRequest;
import eu.fluffici.data.network.repository.AuditNetworkDataSource;
import eu.fluffici.data.network.repository.UserNetworkDataSource;

/**
 * This class responsible for handling data operations. This is the mediator between different
 * data sources (persistent model, web service, cache, etc.)
 */
public class AuditRepository {
    private static final String LOG_TAG = AuditRepository.class.getSimpleName();

    private final AuditDao mAuditDao;
    private final AuditNetworkDataSource mNetworkDataSource;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AuditRepository sInstance;

    public AuditRepository(AuditDao userDao, AuditNetworkDataSource networkDataSource, AppExecutors
            executors) {
        this.mAuditDao = userDao;
        this.mNetworkDataSource = networkDataSource;

        mNetworkDataSource.getAuditList().observeForever(users -> {
            executors.diskIO().execute(() -> {
                Log.d(LOG_TAG, "user table is updating");
                mAuditDao.updateAll(users);
            });
        });
    }

    public static AuditRepository getInstance(AuditDao userDao, AuditNetworkDataSource
            networkDataSource, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AuditRepository(userDao, networkDataSource, executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<List<Audit>> getAuditList() {
        return mAuditDao.getAuditLogs();
    }

    public void postServiceRequest(ServiceRequest serviceRequest) {
        mNetworkDataSource.fetchData(serviceRequest);
    }
}