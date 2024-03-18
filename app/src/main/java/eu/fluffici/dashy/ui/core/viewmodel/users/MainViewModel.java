package eu.fluffici.dashy.ui.core.viewmodel.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import eu.fluffici.data.UserRepository;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.model.ServiceRequest;

public class MainViewModel extends ViewModel {

    private final UserRepository mRepository;
    private final LiveData<List<User>> mUserData;

    public MainViewModel(UserRepository mRepository) {
        this.mRepository = mRepository;
        this.mUserData = mRepository.getUserList();
    }

    public LiveData<List<User>> getUserList() {
        return mUserData;
    }

    public void postRequest(ServiceRequest serviceRequest) {
        mRepository.postServiceRequest(serviceRequest);
    }
}