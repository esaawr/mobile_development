package com.example.dokusochka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(
    private val orders: List<Order>,
    private val onStatusChange: (orderId: Int, newStatus: String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdText: TextView = itemView.findViewById(R.id.orderIdText)
        val orderStatusText: TextView = itemView.findViewById(R.id.orderStatusText)
        val customerInfoText: TextView = itemView.findViewById(R.id.customerInfoText)
        val orderDateText: TextView = itemView.findViewById(R.id.orderDateText)
        val itemsListText: TextView = itemView.findViewById(R.id.itemsListText)
        val totalPriceText: TextView = itemView.findViewById(R.id.totalPriceText)
        
        val processingButton: LinearLayout = itemView.findViewById(R.id.processingButton)
        val preparingButton: LinearLayout = itemView.findViewById(R.id.preparingButton)
        val readyButton: LinearLayout = itemView.findViewById(R.id.readyButton)
        val onTheWayButton: LinearLayout = itemView.findViewById(R.id.onTheWayButton)
        val cancelButton: LinearLayout = itemView.findViewById(R.id.cancelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.orderIdText.text = "Заказ #${order.id}"
        holder.orderStatusText.text = order.status.uppercase()
        holder.customerInfoText.text = "Клиент: ${order.customerName}"
        holder.orderDateText.text = "Дата: ${order.orderDate}"
        holder.itemsListText.text = order.itemsList
        holder.totalPriceText.text = "Итого: ${order.totalPrice.toInt()} Р"

        // установка цвета статуса
        val statusColor = when (order.status.lowercase()) {
            "новый" -> android.R.color.holo_blue_light
            "в обработке" -> android.R.color.holo_orange_light
            "готовится" -> android.R.color.holo_green_light
            "в пути" -> android.R.color.holo_red_light
            "готов" -> android.R.color.holo_green_dark
            "отменен" -> android.R.color.darker_gray
            else -> android.R.color.darker_gray
        }
        holder.orderStatusText.setBackgroundColor(
            holder.itemView.context.getColor(statusColor)
        )

        // обработчики кнопок смены статуса
        holder.processingButton.setOnClickListener {
            onStatusChange(order.id, "в обработке")
        }

        holder.preparingButton.setOnClickListener {
            onStatusChange(order.id, "готовится")
        }

        holder.readyButton.setOnClickListener {
            onStatusChange(order.id, "готов")
        }

        holder.onTheWayButton.setOnClickListener {
            onStatusChange(order.id, "в пути")
        }

        holder.cancelButton.setOnClickListener {
            onStatusChange(order.id, "отменен")
        }

        // подсветка текущего статуса
        highlightCurrentStatus(holder, order.status)
    }

    private fun highlightCurrentStatus(holder: OrderViewHolder, status: String) {
        // сброс всех кнопок
        resetButtonOpacity(holder.processingButton)
        resetButtonOpacity(holder.preparingButton)
        resetButtonOpacity(holder.readyButton)
        resetButtonOpacity(holder.onTheWayButton)
        resetButtonOpacity(holder.cancelButton)

        // подсветка активной кнопку
        when (status.lowercase()) {
            "в обработке" -> setButtonActive(holder.processingButton)
            "готовится" -> setButtonActive(holder.preparingButton)
            "готов" -> setButtonActive(holder.readyButton)
            "в пути" -> setButtonActive(holder.onTheWayButton)
            "отменен" -> setButtonActive(holder.cancelButton)
        }
    }

    private fun resetButtonOpacity(button: LinearLayout) {
        button.alpha = 0.5f
    }

    private fun setButtonActive(button: LinearLayout) {
        button.alpha = 1.0f
    }

    override fun getItemCount(): Int = orders.size
}
