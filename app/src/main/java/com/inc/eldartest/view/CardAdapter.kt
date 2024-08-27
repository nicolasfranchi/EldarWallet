package com.inc.eldartest.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inc.eldartest.R
import com.inc.eldartest.model.Card
import com.inc.eldartest.util.Utils

class CardAdapter(
    private var cards: List<Card>,
    private val onCardClick: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CreditCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_credit_card, parent, false)
        return CreditCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val card = cards[position]
        holder.tvCardIssuer.text = card.cardIssuer
        holder.tvCardNumber.text = Utils.formatCardNumber(card.cardNumber)
        holder.tvExpiryDate.text = card.expiryDate
        holder.tvSecurityCode.text = card.cvv

        holder.itemView.setOnClickListener {
            onCardClick(card)
        }
    }

    override fun getItemCount(): Int = cards.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }



    class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCardIssuer: TextView = itemView.findViewById(R.id.tvCardIssuer)
        var tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        var tvExpiryDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        var tvSecurityCode: TextView = itemView.findViewById(R.id.tvSecurityCode)
    }
}
