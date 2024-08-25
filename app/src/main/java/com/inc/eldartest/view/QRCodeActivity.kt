package com.inc.eldartest.view

import QRCodeViewModel
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.inc.eldartest.R
import com.inc.eldartest.databinding.ActivityQrcodeBinding
import com.inc.eldartest.data.QRCodeRepository
import com.inc.eldartest.util.RetrofitClient
import com.inc.eldartest.viewmodel.QRCodeViewModelFactory

class QRCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrcodeBinding
    private val qrCodeViewModel: QRCodeViewModel by viewModels {
        QRCodeViewModelFactory(QRCodeRepository(RetrofitClient.qrCodeApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val firstName = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("first_name", "")
        val lastName = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("last_name", "")

        val stringCode = "$firstName$lastName"

        qrCodeViewModel.generateQRCode(stringCode).observe(this) { imageData ->
            if (imageData != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                binding.ivQrcode.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Failed to load QR code", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

    }
}
