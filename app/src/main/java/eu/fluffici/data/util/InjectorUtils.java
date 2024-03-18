package eu.fluffici.data.util;

import android.content.Context;

import eu.fluffici.dashy.AppExecutors;
import eu.fluffici.dashy.ui.core.viewmodel.audit.AuditViewModelFactory;
import eu.fluffici.dashy.ui.core.viewmodel.users.MainViewModelFactory;
import eu.fluffici.data.AuditRepository;
import eu.fluffici.data.UserRepository;
import eu.fluffici.data.database.UserDatabase;
import eu.fluffici.data.database.dao.AuditDao;
import eu.fluffici.data.database.dao.UserDao;
import eu.fluffici.data.network.repository.AuditNetworkDataSource;
import eu.fluffici.data.network.repository.UserNetworkDataSource;

public class InjectorUtils {
    public static UserRepository getRepository(Context context) {
        // Get all we need
        UserDao userDao = UserDatabase.getInstance(context).userDao();
        AppExecutors executors = AppExecutors.getInstance();
        UserNetworkDataSource networkDataSource = UserNetworkDataSource.getInstance(executors);

        return UserRepository.getInstance(userDao, networkDataSource, executors);
    }

    public static AuditRepository getAuditRepository(Context context) {
        // Get all we need
        AuditDao userDao = UserDatabase.getInstance(context).auditDao();
        AppExecutors executors = AppExecutors.getInstance();
        AuditNetworkDataSource networkDataSource = AuditNetworkDataSource.getInstance(executors);

        return AuditRepository.getInstance(userDao, networkDataSource, executors);
    }

    public static MainViewModelFactory getMainViewModelFactory(Context context){
        UserRepository repository = getRepository(context);
        return new MainViewModelFactory(repository);
    }

    public static AuditViewModelFactory getAuditViewModelFactory(Context context){
        AuditRepository repository = getAuditRepository(context);
        return new AuditViewModelFactory(repository);
    }
}