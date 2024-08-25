package com.inc.eldartest.viewmodel

import QRCodeViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inc.eldartest.data.QRCodeRepository

class QRCodeViewModelFactory(private val repository: QRCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QRCodeViewModel::class.java)) {
            return QRCodeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
