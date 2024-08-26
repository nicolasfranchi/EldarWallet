package com.inc.eldartest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.inc.eldartest.data.QrRepository
import kotlinx.coroutines.Dispatchers

class QrViewModel : ViewModel() {

    private val repository = QrRepository()
    fun generateQRCode(text: String) = liveData(Dispatchers.IO) {
        try {
            val imageData = repository.generateQRCode(text)
            emit(imageData)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
