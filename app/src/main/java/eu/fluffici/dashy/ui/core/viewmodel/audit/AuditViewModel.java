package eu.fluffici.dashy.ui.core.viewmodel.audit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import eu.fluffici.data.AuditRepository;
import eu.fluffici.data.UserRepository;
import eu.fluffici.data.database.entity.Audit;
import eu.fluffici.data.database.entity.User;
import eu.fluffici.data.model.ServiceRequest;

public class AuditViewModel extends ViewModel {

    private final AuditRepository mRepository;
    private final LiveData<List<Audit>> mUserData;

    public AuditViewModel(AuditRepository mRepository) {
        this.mRepository = mRepository;
        this.mUserData = mRepository.getAuditList();
    }

    public LiveData<List<Audit>> getAuditList() {
        return mUserData;
    }

    public void postRequest(ServiceRequest serviceRequest) {
        mRepository.postServiceRequest(serviceRequest);
    }
}