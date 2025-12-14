package com.example.dokusochka

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var emptyOrdersText: TextView
    
    // статистика
    private lateinit var newOrdersCount: TextView
    private lateinit var processingOrdersCount: TextView
    private lateinit var preparingOrdersCount: TextView
    private lateinit var readyOrdersCount: TextView
    private lateinit var onTheWayOrdersCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_orders)

        initViews()
        setupClickListeners()
        loadOrders()
    }

    private fun initViews() {
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        emptyOrdersText = findViewById(R.id.emptyOrdersText)
        
        newOrdersCount = findViewById(R.id.newOrdersCount)
        processingOrdersCount = findViewById(R.id.processingOrdersCount)
        preparingOrdersCount = findViewById(R.id.preparingOrdersCount)
        readyOrdersCount = findViewById(R.id.readyOrdersCount)
        onTheWayOrdersCount = findViewById(R.id.onTheWayOrdersCount)

        databaseHelper = DatabaseHelper(this)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadOrders() {
        showLoading(true)

        // загружаю заказы из базы данных
        val orders = databaseHelper.getAllOrders()

        if (orders.isEmpty()) {
            showEmptyState()
        } else {
            showOrders(orders)
            updateStatistics(orders)
        }

        showLoading(false)
    }

    private fun showOrders(orders: List<Order>) {
        emptyOrdersText.visibility = View.GONE
        ordersRecyclerView.visibility = View.VISIBLE

        ordersAdapter = OrdersAdapter(orders) { orderId, newStatus ->
            updateOrderStatus(orderId, newStatus)
        }
        ordersRecyclerView.adapter = ordersAdapter
    }

    private fun showEmptyState() {
        ordersRecyclerView.visibility = View.GONE
        emptyOrdersText.visibility = View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        val success = databaseHelper.updateOrderStatus(orderId, newStatus)
        if (success) {
            // перезагружаю список заказов
            loadOrders()
        }
    }

    private fun updateStatistics(orders: List<Order>) {
        val newCount = orders.count { it.status == "новый" }
        val processingCount = orders.count { it.status == "в обработке" }
        val preparingCount = orders.count { it.status == "готовится" }
        val readyCount = orders.count { it.status == "готов" }
        val onTheWayCount = orders.count { it.status == "в пути" }

        newOrdersCount.text = newCount.toString()
        processingOrdersCount.text = processingCount.toString()
        preparingOrdersCount.text = preparingCount.toString()
        readyOrdersCount.text = readyCount.toString()
        onTheWayOrdersCount.text = onTheWayCount.toString()
    }

    override fun onResume() {
        super.onResume()
        // обновляю список при возвращении на экран
        loadOrders()
    }
}
