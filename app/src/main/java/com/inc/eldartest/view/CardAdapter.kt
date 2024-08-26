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
import com.inc.eldartest.model.Card
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardAdapter(
    private var cards: List<Card>,
    private val context: Context,
    private val onDeleteClick: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_credit_card, parent, false)
        return CreditCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val card = cards[position]
        holder.tvCardIssuer.text = card.cardIssuer
        holder.tvCardNumber.text = formatCardNumber(card.cardNumber)  // Formatear número de tarjeta
        holder.tvExpiryDate.text = card.expiryDate
        holder.tvSecurityCode.text = card.cvv

        holder.itemView.setOnClickListener {
            showBottomSheet(card)
        }
    }

    override fun getItemCount(): Int = cards.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }

    fun getCards(): List<Card> {
        return cards
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showBottomSheet(card: Card) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_card_actions, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.tvCardDetail)
            .text = "${card.cardIssuer} - ${card.cardNumber.takeLast(4)} - EXP ${card.expiryDate}"

        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            onDeleteClick(card)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun formatCardNumber(cardNumber: String): String {
        return if (cardNumber.length == 16) {
            // Agrupar de a 4 para 16 dígitos
            cardNumber.chunked(4).joinToString(" ")
        } else if (cardNumber.length == 17) {
            // Agrupar 3 grupos de 4 y 1 de 5 para 17 dígitos
            cardNumber.substring(0, 12).chunked(4).joinToString(" ") + " " + cardNumber.substring(12)
        } else {
            cardNumber  // Devolver tal cual si no es 16 o 17 dígitos
        }
    }

    class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCardIssuer: TextView = itemView.findViewById(R.id.tvCardIssuer)
        var tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        var tvExpiryDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        var tvSecurityCode: TextView = itemView.findViewById(R.id.tvSecurityCode)
    }
}
