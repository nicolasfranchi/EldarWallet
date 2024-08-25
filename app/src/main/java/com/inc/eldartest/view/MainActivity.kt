package com.inc.eldartest.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inc.eldartest.R
import com.inc.eldartest.model.CreditCard
import com.inc.eldartest.databinding.ActivityMainBinding
import com.inc.eldartest.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var creditCardAdapter: CreditCardAdapter

    private lateinit var addCardActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        if (!isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            loadUserData()
            setupRecyclerView()
            observeCreditCards()
        }

        setupAddCardActivityResultLauncher()

        findViewById<ImageButton>(R.id.btnLogOut).setOnClickListener{
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageButton>(R.id.btnAddCreditcard).setOnClickListener {
            showAddCardBottomSheet()
        }

        findViewById<LinearLayout>(R.id.btnPayWithQR).setOnClickListener{
            val intent = Intent(this, QRCodeActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnPayWithCard).setOnClickListener{

        }
    }


    private fun showAddCardBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_new_card, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val etCardNumber = bottomSheetView.findViewById<EditText>(R.id.etCardNumber)
        val tvCardIssuer = bottomSheetView.findViewById<TextView>(R.id.tvCardIssuer)
        val etFirstName = bottomSheetView.findViewById<EditText>(R.id.etCardHolderFirstName)
        val etLastName = bottomSheetView.findViewById<EditText>(R.id.etCardHolderLastName)
        val etExpiryDate = bottomSheetView.findViewById<EditText>(R.id.etExpiryDate)
        val etCVV = bottomSheetView.findViewById<EditText>(R.id.etCVV)
        val btnSaveCard = bottomSheetView.findViewById<Button>(R.id.btnSaveCard)

        etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                tvCardIssuer.text = detectCardIssuer(s.toString())
            }
        })

        etExpiryDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var deletingSlash = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!isFormatting && count == 1 && after == 0 && s != null && s[start] == '/') {
                    deletingSlash = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                if (deletingSlash) {
                    s?.delete(s.length - 1, s.length)
                    deletingSlash = false
                } else {
                    if (s != null && s.length == 2 && !s.contains("/")) {
                        // Verificar si los dos primeros dígitos son un mes válido
                        val monthInput = s.toString()
                        try {
                            val month = monthInput.toInt()
                            if (month < 1 || month > 12) {
                                etExpiryDate.setError("Enter a valid month (01-12)")
                                //s.clear() // Limpiar el input si el mes no es válido
                            } else {
                                s.append('/') // Añadir la barra si el mes es válido
                            }
                        } catch (e: NumberFormatException) {
                            Toast.makeText(etExpiryDate.context, "Invalid month format", Toast.LENGTH_SHORT).show()
                            s.clear()
                        }
                    }
                }

                isFormatting = false
            }
        })

        fun formatCardNumber(cardNumber: String): String {
            // Filtrar solo dígitos
            val digitsOnly = cardNumber.filter { it.isDigit() }

            // Determinar el formato basado en la longitud
            return if (digitsOnly.length == 16) {
                // Formato típico de 4 grupos de 4
                digitsOnly.chunked(4).joinToString(" ")
            } else {
                // Formato de 3 grupos de 4 y uno de 5 al final
                digitsOnly.substring(0, 12).chunked(4).joinToString(" ") + " " + digitsOnly.substring(12)
            }
        }



        btnSaveCard.setOnClickListener {
            val cardNumberFormatted = formatCardNumber(etCardNumber.text.toString())
            val card = CreditCard(
                cardIssuer = tvCardIssuer.text.toString(),
                cardNumber = cardNumberFormatted,
                expiryDate = etExpiryDate.text.toString(),
                cvv = etCVV.text.toString(),
                ownerId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1)
            )

            if (!viewModel.verifyUserFirstName(etFirstName.text.toString().replace(" ", ""))
                || !viewModel.verifyUserLastName(etLastName.text.toString().replace(" ", ""))
            ) {
                if (!viewModel.verifyUserFirstName(etFirstName.text.toString()))
                    etFirstName.setError("Cardholder name doesn't match")
                if (!viewModel.verifyUserLastName(etLastName.text.toString()))
                    etLastName.setError("Cardholder name doesn't match")
            } else {
                viewModel.addCard(card)
                Toast.makeText(this, "Card saved successfully", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    private fun detectCardIssuer(number: String): String {
        if (number.isEmpty()) {
            return "";
        } else {
            return when (number.first()) {
                '3' -> "American Express"
                '4' -> "Visa"
                '5' -> "Mastercard"
                else -> "Unknown"
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupAddCardActivityResultLauncher() {
        addCardActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.e("ENTRE AL RESULT LAUNCHER OK", "OK")
                val userId =
                    getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1)
                viewModel.loadCreditCards(userId)
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val id = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1)
        return id != -1
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        val userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1)
        val firstName =
            getSharedPreferences("user_prefs", MODE_PRIVATE).getString("first_name", "")
        val balance = getSharedPreferences("user_prefs", MODE_PRIVATE).getFloat("balance", 0f)

        binding.tvName.text = "Hi, $firstName!"
        binding.tvBalance.text = "$$balance"

        viewModel.loadCreditCards(userId)
    }

    private fun setupRecyclerView() {
        creditCardAdapter = CreditCardAdapter(
            emptyList(),
            this,
            { card -> handleDeleteClick(card) }
        )
        binding.rvCreditCards.adapter = creditCardAdapter
        binding.rvCreditCards.layoutManager = LinearLayoutManager(this)
    }

    private fun handleDeleteClick(card: CreditCard) {
        val userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1)
        viewModel.deleteCreditCard(card.cardId, userId)
    }

    private fun observeCreditCards() {
        viewModel.creditCards.observe(this) { cards ->
            Log.e("TAG", "ENTRE AL OBSERVER")
            Log.e("CARDS", cards.toString());
            if (cards != null) {
                creditCardAdapter.updateCreditCards(cards)
            }
        }
    }
}
