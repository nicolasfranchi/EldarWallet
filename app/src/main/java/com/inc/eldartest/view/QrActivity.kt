package com.inc.eldartest.view

import com.inc.eldartest.viewmodel.QrViewModel
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inc.eldartest.R
import com.inc.eldartest.databinding.ActivityQrcodeBinding
import com.inc.eldartest.util.Constants

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrcodeBinding
    private val viewModel = QrViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(Constants.KEY_NAME)
        val lastName = intent.getStringExtra(Constants.KEY_LASTNAME)

        val text = "${name}-${lastName}"

        viewModel.generateQRCode(text).observe(this) { imageData ->
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
