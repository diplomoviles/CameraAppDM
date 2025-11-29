package com.amaurypm.cameraappdm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amaurypm.cameraappdm.data.PhotoRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val photoRepository: PhotoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(photoRepository) as T
        }
        throw IllegalArgumentException("Clase viewmodel desconocida")
    }
}