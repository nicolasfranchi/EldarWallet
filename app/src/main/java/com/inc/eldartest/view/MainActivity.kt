package com.inc.eldartest.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.auth.FirebaseUser
import com.inc.eldartest.R
import com.inc.eldartest.databinding.ActivityMainBinding
import com.inc.eldartest.model.Card
import com.inc.eldartest.model.User
import com.inc.eldartest.util.Constants
import com.inc.eldartest.util.Utils
import com.inc.eldartest.viewmodel.MainViewModel
import java.text.DecimalFormat

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
            return
        }

        user = viewModel.getCurrentUser()!!
        loadUserData()
        setupRecyclerView()
        observeCreditCards()

        setupAddCardActivityResultLauncher()

        findViewById<Button>(R.id.btnDeposit).setOnClickListener {
            showDepositBottomSheet()
        }

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            showSendBottomSheet()
        }

        findViewById<ImageButton>(R.id.btnLogOut).setOnClickListener {
            viewModel.logOut()
            startActivity(Intent(this, LoginActivity::class.java))
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
    }

    private fun setupRecyclerView() {
        cardAdapter = CardAdapter(
            emptyList()
        ) { card -> showCardOptionsBottomSheet(card) }
        binding.rvCreditCards.adapter = cardAdapter
        binding.rvCreditCards.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("InflateParams")
    private fun showCardOptionsBottomSheet(card: Card) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_card_actions, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.tvCardIssuer).text = card.cardIssuer

        view.findViewById<TextView>(R.id.tvCardNumber)
            .text = Utils.formatCardNumber(card.cardNumber)

        view.findViewById<TextView>(R.id.tvExpiryDate).text = card.expiryDate

        view.findViewById<TextView>(R.id.tvSecurityCode).text = card.cvv

        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            handleDeleteClick(card)
            bottomSheetDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnPayWithNFC).setOnClickListener {
            val intent = Intent(this, NfcActivity::class.java)
            intent.putExtra(Constants.KEY_CARD, card)
            startActivity(intent)
        }

        bottomSheetDialog.show()
    }

    private fun handleDeleteClick(card: Card) {
        viewModel.deleteCreditCard(card.cardId, user.uid)
        Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show()
    }

    private fun observeCreditCards() {
        viewModel.cards.observe(this) { cards ->
            if (cards != null) {
                cardAdapter.updateCards(cards)
            }
        }
    }

    private fun setupAddCardActivityResultLauncher() {
        addCardActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadCreditCards(user.uid)
            }
        }
    }

    private fun loadUserData() {
        viewModel.getUserDetails(user.uid)

        viewModel.userDetails.observe(this) { userDetails ->
            if (userDetails != null) {
                userInfo = userDetails
                val helloUser = "Hi, ${userDetails.firstName}!"
                binding.tvName.text = helloUser
                binding.tvBalance.text = viewModel.formatBalance(userDetails.balance)
            } else {
                Toast.makeText(this@MainActivity, "Error fetching user data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.loadCreditCards(user.uid)
    }

    @SuppressLint("InflateParams")
    private fun showDepositBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_deposit, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val etAmount = bottomSheetView.findViewById<EditText>(R.id.etAmountDeposit)
        var realAmount = 0.0
        val decimalFormat = DecimalFormat("#,##0.00")
        decimalFormat.isGroupingUsed = true
        decimalFormat.groupingSize = 3

        etAmount.addTextChangedListener(object : TextWatcher {
            private var current: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    etAmount.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    realAmount = parsed / 100
                    val formatted = decimalFormat.format(realAmount)

                    current = formatted
                    etAmount.setText(formatted)
                    etAmount.setSelection(formatted.length)

                    etAmount.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        bottomSheetView.findViewById<Button>(R.id.btnConfirmDeposit).setOnClickListener {
            if (realAmount <= 0) {
                etAmount.error = "Amount must be greater than 0"
            } else {
                viewModel.deposit(user.uid, realAmount)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showSendBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_send, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val etAmount = bottomSheetView.findViewById<EditText>(R.id.etAmountSend)
        val etRecipientEmail = bottomSheetView.findViewById<EditText>(R.id.etRecipientEmail)
        var realAmount = 0.0
        val decimalFormat = DecimalFormat("#,##0.00")
        decimalFormat.isGroupingUsed = true
        decimalFormat.groupingSize = 3

        etAmount.addTextChangedListener(object : TextWatcher {
            private var current: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    etAmount.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    realAmount = parsed / 100
                    val formatted = decimalFormat.format(realAmount)

                    current = formatted
                    etAmount.setText(formatted)
                    etAmount.setSelection(formatted.length)

                    etAmount.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        bottomSheetView.findViewById<Button>(R.id.btnConfirmSend).setOnClickListener {
            val email = etRecipientEmail.text.toString()
            var hasError = false

            if (realAmount <= 0) {
                etAmount.error = "Amount must be greater than 0"
                hasError = true
            }

            if (email.isEmpty()) {
                etRecipientEmail.error = "You must fill in the recipient's email."
                hasError = true
            }

            if (!hasError) {
                viewModel.send(user.uid, email, realAmount)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    @SuppressLint("InflateParams")
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
            private var isFormatting = false
            private var deletingSpace = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                deletingSpace = count > 0 && s?.get(start) == ' '
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
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
            val cardNumber = etCardNumber.text.toString().replace(" ", "")
            val inputFirstName = etFirstName.text.toString().trim()
            val inputLastName = etLastName.text.toString().trim()
            val cvv = etCVV.text.toString()

            if (cardNumber.length !in 16..17) {
                etCardNumber.error = "Card number must be 16 or 17 digits"
                return@setOnClickListener
            }

            if (!cvv.matches(Regex("\\d{3,4}"))) {
                etCVV.error = "CVV must be 3 or 4 digits"
                return@setOnClickListener
            }

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
                    cardNumber = cardNumber,
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
}
