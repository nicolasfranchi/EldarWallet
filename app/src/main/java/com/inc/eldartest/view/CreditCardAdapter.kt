package com.inc.eldartest.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inc.eldartest.R
import com.inc.eldartest.model.CreditCard
import com.google.android.material.bottomsheet.BottomSheetDialog

class CreditCardAdapter(
    private var creditCards: List<CreditCard>,
    private val context: Context,
    private val onDeleteClick: (CreditCard) -> Unit
) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_credit_card, parent, false)
        return CreditCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val card = creditCards[position]
        holder.tvCardIssuer.text = card.cardIssuer
        holder.tvCardNumber.text = card.cardNumber
        holder.tvExpiryDate.text = card.expiryDate
        holder.tvSecurityCode.text = card.cvv

        holder.itemView.setOnClickListener {
            showBottomSheet(card)
        }
    }

    override fun getItemCount(): Int = creditCards.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCreditCards(newCards: List<CreditCard>) {
        creditCards = newCards
        notifyDataSetChanged()
    }

    fun getCreditCards(): List<CreditCard> {
        return creditCards
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showBottomSheet(card: CreditCard) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_card_actions, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.tvCardDetail)
            .setText("${card.cardIssuer} - ${card.cardNumber?.takeLast(4)} - EXP ${card.expiryDate}")

        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            onDeleteClick(card)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCardIssuer: TextView = itemView.findViewById(R.id.tvCardIssuer)
        var tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        var tvExpiryDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        var tvSecurityCode: TextView = itemView.findViewById(R.id.tvSecurityCode)
    }
}
