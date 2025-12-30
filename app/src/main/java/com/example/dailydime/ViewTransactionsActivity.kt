package com.example.dailydime

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydime.AddTransactionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewTransactionsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_transactions)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val transactionsListView = findViewById<ListView>(R.id.transactionsListView)

        val transactions = sharedPreferences.getStringSet("transactions", mutableSetOf())?.toList() ?: listOf()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, transactions)
        transactionsListView.adapter = adapter

        transactionsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedTransaction = transactions[position]
            val options = arrayOf("Edit", "Delete")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Edit transaction
                        val intent = Intent(this, EditTransactionActivity::class.java)
                        intent.putExtra("transaction", selectedTransaction)
                        startActivity(intent)
                    }
                    1 -> {
                        // Delete transaction
                        deleteTransaction(selectedTransaction)
                        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(intent)
                    }
                }
            }
            builder.show()
        }

        // Bottom Navigation Bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_view_transactions

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.navigation_add_transaction -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.navigation_view_transactions -> true
                R.id.navigation_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteTransaction(transaction: String) {
        val transactions = sharedPreferences.getStringSet("transactions", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        transactions.remove(transaction)
        sharedPreferences.edit().putStringSet("transactions", transactions).apply()

        // Update budget
        val parts = transaction.split("|")
        val type = parts[0]
        val amount = parts[3].toFloat()
        updateBudget(type, amount)
    }

    private fun updateBudget(type: String, amount: Float) {
        val currentMonth = getCurrentMonth()
        val currentBudget = sharedPreferences.getFloat("budget_$currentMonth", 0f)
        val updatedBudget = if (type == "Income") currentBudget - amount else currentBudget + amount
        sharedPreferences.edit().putFloat("budget_$currentMonth", updatedBudget).apply()
    }

    private fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}