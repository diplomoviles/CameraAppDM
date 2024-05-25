package com.amaurypm.cameraappdm.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val _itemRemoved = MutableLiveData<Int>()
    val itemRemoved: LiveData<Int> = _itemRemoved

    fun notifyItemRemoved(position: Int){
        _itemRemoved.postValue(position)
    }

}