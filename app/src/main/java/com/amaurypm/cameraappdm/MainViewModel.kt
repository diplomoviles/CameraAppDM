package com.amaurypm.cameraappdm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amaurypm.cameraappdm.data.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val photoRepository: PhotoRepository
): ViewModel() {

    private val _itemRemoved = MutableLiveData<Int>()
    val itemRemoved: LiveData<Int> = _itemRemoved
    
    private val _photoPaths = MutableLiveData<List<String>>() 
    val photoPaths: LiveData<List<String>> = _photoPaths

    //Para que sepa cuál elemento se va a eliminar en el
    //recycler view y pueda borrar el archivo también
    fun notifyItemRemoved(position: Int){
        viewModelScope.launch(Dispatchers.IO) {
            photoRepository.deletePhotoFile(position)
            _itemRemoved.postValue(position)
        }
    }

    //Para cargar las fotos
    fun loadSavedPhotoPaths(){
        viewModelScope.launch(Dispatchers.IO) {
            _photoPaths.postValue(photoRepository.loadSavedPhotoPaths())
        }
    }

}