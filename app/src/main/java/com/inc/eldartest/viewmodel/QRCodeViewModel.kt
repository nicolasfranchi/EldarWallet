import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.inc.eldartest.data.QRCodeRepository
import kotlinx.coroutines.Dispatchers

class QRCodeViewModel(private val repository: QRCodeRepository) : ViewModel() {
    fun generateQRCode(text: String) = liveData(Dispatchers.IO) {
        try {
            val imageData = repository.generateQRCode(text)
            emit(imageData)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
