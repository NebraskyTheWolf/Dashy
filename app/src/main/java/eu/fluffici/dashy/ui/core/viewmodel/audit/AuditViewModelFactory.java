package eu.fluffici.dashy.ui.core.viewmodel.audit;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import eu.fluffici.data.AuditRepository;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link AuditRepository}
 */
public class AuditViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AuditRepository auditRepository;

    public AuditViewModelFactory(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AuditViewModel(auditRepository);
    }
}