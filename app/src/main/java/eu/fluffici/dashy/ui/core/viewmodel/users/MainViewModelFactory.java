package eu.fluffici.dashy.ui.core.viewmodel.users;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import eu.fluffici.data.UserRepository;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link eu.fluffici.data.UserRepository}
 */
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final UserRepository userRepository;

    public MainViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(userRepository);
    }
}