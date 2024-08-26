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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.inc.eldartest.R
import com.inc.eldartest.model.Card
import com.inc.eldartest.databinding.ActivityMainBinding
import com.inc.eldartest.model.User
import com.inc.eldartest.util.Constants
import com.inc.eldartest.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var cardAdapter: CardAdapter

    private lateinit var addCardActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var user: FirebaseUser
    private lateinit var userInfo: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        if (viewModel.getCurrentUser() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            user = viewModel.getCurrentUser()!!
            loadUserData()
            setupRecyclerView()
            observeCreditCards()
        }

        setupAddCardActivityResultLauncher()

        findViewById<ImageButton>(R.id.btnLogOut).setOnClickListener {
            viewModel.logOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageButton>(R.id.btnAddCreditcard).setOnClickListener {
            showAddCardBottomSheet()
        }

        findViewById<LinearLayout>(R.id.btnPayWithQR).setOnClickListener {
            val intent = Intent(this, QrActivity::class.java)
            intent.putExtra(Constants.KEY_NAME, userInfo.firstName)
            intent.putExtra(Constants.KEY_LASTNAME, userInfo.lastName)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnPayWithCard).setOnClickListener {

        }
    }


    private fun showAddCardBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_card, null)
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
            private var isFormatting = false  // Para evitar bucles de formato
            private var deletingSpace = false  // Para manejar la eliminación de espacios

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Verifica si el usuario está eliminando un espacio
                deletingSpace = count > 0 && s?.get(start) == ' '
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return  // Evita bucles infinitos

                isFormatting = true

                s?.let {
                    val digitsOnly = s.toString().replace(" ", "")
                    val formattedCardNumber = StringBuilder()

                    for (i in digitsOnly.indices) {
                        if (i > 0 && (i % 4 == 0) && i < 16) {
                            formattedCardNumber.append(" ")
                        }
                        formattedCardNumber.append(digitsOnly[i])
                    }

                    // Detectar emisor de la tarjeta según el primer dígito
                    tvCardIssuer.text = detectCardIssuer(digitsOnly)

                    etCardNumber.removeTextChangedListener(this)
                    etCardNumber.setText(formattedCardNumber.toString())
                    etCardNumber.setSelection(formattedCardNumber.length)
                    etCardNumber.addTextChangedListener(this)
                }

                isFormatting = false
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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                if (deletingSlash) {
                    s?.delete(s.length - 1, s.length)
                    deletingSlash = false
                } else {
                    if (s != null && s.length == 2 && !s.contains("/")) {
                        val month = s.toString().toIntOrNull()
                        if (month == null || month !in 1..12) {
                            etExpiryDate.error = "Enter a valid month (01-12)"
                        } else {
                            s.append('/')
                        }
                    }
                }

                isFormatting = false
            }
        })

        btnSaveCard.setOnClickListener {
            val cardNumber = etCardNumber.text.toString().replace(" ", "")  // Guardar sin espacios
            val inputFirstName = etFirstName.text.toString().trim()
            val inputLastName = etLastName.text.toString().trim()
            val cvv = etCVV.text.toString()

            // Validación del número de tarjeta: 16 o 17 dígitos permitidos
            if (cardNumber.length !in 16..17) {
                etCardNumber.error = "Card number must be 16 or 17 digits"
                return@setOnClickListener
            }

            // Validación del CVV: 3 o 4 dígitos, sin espacios
            if (!cvv.matches(Regex("\\d{3,4}"))) {
                etCVV.error = "CVV must be 3 or 4 digits"
                return@setOnClickListener
            }

            // Verificar nombres
            val isFirstNameValid = inputFirstName.equals(userInfo.firstName, ignoreCase = true)
            val isLastNameValid = inputLastName.equals(userInfo.lastName, ignoreCase = true)

            if (!isFirstNameValid) {
                etFirstName.error = "Cardholder name doesn't match"
            }
            if (!isLastNameValid) {
                etLastName.error = "Cardholder last name doesn't match"
            }

            if (isFirstNameValid && isLastNameValid) {
                val card = Card(
                    cardIssuer = tvCardIssuer.text.toString(),
                    cardNumber = cardNumber,  // Guardar sin espacios
                    expiryDate = etExpiryDate.text.toString(),
                    cvv = cvv,
                    ownerId = user.uid
                )

                viewModel.addCard(card)
                Toast.makeText(this, "Card saved successfully", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    private fun detectCardIssuer(cardNumber: String): String {
        return when {
            cardNumber.startsWith("4") -> "Visa"
            cardNumber.startsWith("5") -> "Mastercard"
            cardNumber.startsWith("3") -> "American Express"
            else -> "Unknown"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAddCardActivityResultLauncher() {
        addCardActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadCreditCards(user.uid)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {

        viewModel.getUserDetails(user.uid)

        viewModel.userDetails.observe(this, Observer { userDetails ->
            if (userDetails != null) {
                userInfo = userDetails
                binding.tvName.text = "Hi, ${userDetails.firstName}!"
                binding.tvBalance.text = "${userDetails.balance}"
            } else {
                Toast.makeText(this@MainActivity, "Error fetching user data", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        viewModel.loadCreditCards(user.uid)
    }

    private fun setupRecyclerView() {
        cardAdapter = CardAdapter(
            emptyList(),
            this
        ) { card -> handleDeleteClick(card) }
        binding.rvCreditCards.adapter = cardAdapter
        binding.rvCreditCards.layoutManager = LinearLayoutManager(this)
    }

    private fun handleDeleteClick(card: Card) {
        viewModel.deleteCreditCard(card.cardId, user.uid)
    }

    private fun observeCreditCards() {
        viewModel.cards.observe(this) { cards ->
            if (cards != null) {
                cardAdapter.updateCards(cards)
            }
        }
    }
}
