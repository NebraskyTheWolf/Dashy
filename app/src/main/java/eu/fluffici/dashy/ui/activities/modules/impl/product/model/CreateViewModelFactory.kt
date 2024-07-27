package eu.fluffici.dashy.ui.activities.modules.impl.product.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
