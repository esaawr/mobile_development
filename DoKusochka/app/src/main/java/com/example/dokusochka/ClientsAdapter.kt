package com.example.dokusochka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClientsAdapter(private val clients: List<Client>) : RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.clientName)
        val emailTextView: TextView = itemView.findViewById(R.id.clientEmail)
        val phoneTextView: TextView = itemView.findViewById(R.id.clientPhone)
        val dateTextView: TextView = itemView.findViewById(R.id.clientDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.nameTextView.text = "Имя: ${client.name}"
        holder.emailTextView.text = "Email: ${client.email}"
        holder.phoneTextView.text = "Телефон: ${client.phone}"
        holder.dateTextView.text = "Дата: ${client.registrationDate}"
    }

    override fun getItemCount(): Int = clients.size
}