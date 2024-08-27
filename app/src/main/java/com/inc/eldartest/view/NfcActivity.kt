package com.inc.eldartest.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inc.eldartest.R
import com.inc.eldartest.model.Card
import com.inc.eldartest.util.Constants.KEY_CARD
import com.inc.eldartest.util.Utils
import java.io.Serializable

class NfcActivity : AppCompatActivity() {

    private lateinit var etNFCAmount: EditText
    private lateinit var btnConfirmNFCAmount: Button
    private lateinit var tvNFCSteps: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        val card : Card = intent.getSerializableExtra(KEY_CARD) as Card

        findViewById<TextView>(R.id.tvCardIssuer).text = card.cardIssuer
        findViewById<TextView>(R.id.tvCardNumber).text = Utils.formatCardNumber(card.cardNumber)
        findViewById<TextView>(R.id.tvSecurityCode).text = card.cvv
        findViewById<TextView>(R.id.tvExpiryDate).text = card.expiryDate

        // Inicialización de vistas
        etNFCAmount = findViewById(R.id.etNFCAmount)
        btnConfirmNFCAmount = findViewById(R.id.btnConfirmNFCAmount)
        tvNFCSteps = findViewById(R.id.tvNFCSteps)

        // Configurar OnClickListener para el botón de confirmar
        btnConfirmNFCAmount.setOnClickListener {
            val amount = etNFCAmount.text.toString().trim()

            if (amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0) {
                etNFCAmount.clearFocus()
                etNFCAmount.isEnabled = false

                tvNFCSteps.text = "Tap with your card to proceed"

            } else {

                etNFCAmount.error = "Please enter a valid amount"
            }
        }
    }
}
